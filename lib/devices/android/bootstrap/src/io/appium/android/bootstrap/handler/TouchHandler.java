
package io.appium.android.bootstrap.handler;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObjectNotFoundException;
import io.appium.android.bootstrap.AndroidCommand;
import io.appium.android.bootstrap.AndroidCommandResult;
import io.appium.android.bootstrap.AndroidElement;
import io.appium.android.bootstrap.CommandHandler;
import io.appium.android.bootstrap.Logger;
import io.appium.android.bootstrap.PositionHelper;
import io.appium.android.bootstrap.exceptions.InvalidCoordinatesException;
import io.appium.android.bootstrap.utils.Point;
import java.util.Hashtable;
import org.json.JSONException;


public abstract class TouchHandler  extends CommandHandler 
{
  
  abstract protected AndroidCommandResult onElementCommand(final AndroidCommand command, Point start, AndroidElement el);
  
  abstract protected AndroidCommandResult onCommand(final AndroidCommand command, Point start);
  
  private AndroidCommandResult onExecute(final AndroidCommand command) {
    AndroidElement el = null;
    Point pt = null;
    try {
      if (command.isElementCommand()) {

        el = command.getElement();
        
        // check if element exists without wait
        if(! el.exists()) {
          throw new UiObjectNotFoundException("Element does not exist.");
        }
        pt = getStartEventCoords(command.params(), el);
        return this.onElementCommand(command, pt, command.getElement());
      } else {
        pt = getStartEventCoords(command.params(), el);
        return this.onCommand(command, pt);
      }
    } catch (final UiObjectNotFoundException e) {
      return getErrorResult(e.getMessage());
    } catch (final InvalidCoordinatesException e) {
      return getErrorResult(e.getMessage());
    } catch (final Exception e) { // handle NullPointerException
      return getErrorResult("Unknown error: " + e.getMessage());
    }
  }
  
  protected Point getStartEventCoords(Hashtable<String, Object> params, AndroidElement el) 
      throws UiObjectNotFoundException, InvalidCoordinatesException {

        Object paramX = "0";
        Object paramY = "0";
        
        if (params.get("x") != null ) {
          paramX = params.get("x");
        } else if (params.get("startX") != null) {
          paramX = params.get("startX");
        }
        
        if (params.get("y") != null ) {
          paramX = params.get("y");
        } else if (params.get("startY") != null) {
          paramX = params.get("startXY");
        }

        final Point start = new Point(paramX, paramY);
        
        if (el == null) {
          return PositionHelper.getDeviceAbsPos(start);
        } 
        
        return el.getAbsolutePosition(start);
  }
  
    public AndroidCommandResult execute(final AndroidCommand command)
      throws JSONException {
    final Hashtable<String, Object> params = command.params();
    final Point start = new Point(params.get("startX"), params.get("startY"));
    final Point end = new Point(params.get("endX"), params.get("endY"));
    final Integer steps = (Integer) params.get("steps");
    final UiDevice device = UiDevice.getInstance();

    Point absStartPos = new Point();
    Point absEndPos = new Point();

    try {
      if (command.isElementCommand()) {
        final AndroidElement el = command.getElement();
        absStartPos = el.getAbsolutePosition(start);
        absEndPos = el.getAbsolutePosition(end);
      } else {
        absStartPos = PositionHelper.getDeviceAbsPos(start);
        absEndPos = PositionHelper.getDeviceAbsPos(end);
      }
    } catch (final UiObjectNotFoundException e) {
      return getErrorResult(e.getMessage());
    } catch (final InvalidCoordinatesException e) {
      return getErrorResult(e.getMessage());
    } catch (final Exception e) { // handle NullPointerException
      return getErrorResult("Unknown error");
    }

    Logger.debug("Swiping from " + absStartPos.toString() + " to "
        + absEndPos.toString() + " with steps: " + steps.toString());
    final boolean rv = device.swipe(absStartPos.x.intValue(),
        absStartPos.y.intValue(), absEndPos.x.intValue(),
        absEndPos.y.intValue(), steps);
    if (!rv) {
      return getErrorResult("The swipe did not complete successfully");
    }
    return getSuccessResult(rv);
  }
}
