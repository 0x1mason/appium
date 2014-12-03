package com.android.uiautomator.common;

import com.android.uiautomator.core.UiDevice;
import io.appium.android.bootstrap.Logger;
import android.view.InputEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static io.appium.android.bootstrap.utils.API.API_18;
import java.lang.reflect.InvocationTargetException;

public class ReflectionUtils {
  private static Field enableField(final Class<?> clazz, final String field)
      throws SecurityException, NoSuchFieldException {
    Logger.debug("Updating class \"" + clazz + "\" to enable field \"" + field
        + "\"");
    final Field fieldObject = clazz.getDeclaredField(field);
    fieldObject.setAccessible(true);
    return fieldObject;
  }

  private Object controller = null;
  private Object bridge = null;
  private Object queryController = null;

  public ReflectionUtils() throws IllegalArgumentException,
      IllegalAccessException, SecurityException, NoSuchFieldException {
    final UiDevice device = UiDevice.getInstance();
    bridge = enableField(device.getClass(), "mUiAutomationBridge")
        .get(device);
    Method getQueryController = null;
    Logger.debug(bridge.getClass().getCanonicalName());
 //   try {
      //getQueryController = getMethod(bridge.getClass(), "getQueryController");
      //queryController = getQueryController.invoke(bridge);
      queryController = enableField(bridge.getClass().getSuperclass(), "mQueryController")
        .get(bridge);
  //  } catch (NoSuchMethodException e) {
 //     throw new IllegalAccessException("Did not find getQueryController");
  //  } catch (InvocationTargetException ex) {
 //     throw new IllegalAccessException("Failed to invoke getQueryController");
 //   }
    
    if (API_18) {
      controller = enableField(bridge.getClass().getSuperclass(),
          "mInteractionController").get(bridge);
    } else {
      controller = enableField(bridge.getClass(), "mInteractionController")
          .get(bridge);
    }
  }
  
  public AccessibilityNodeInfo getAccessibilityRootNode() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      Class queryControllerClass = queryController.getClass();
      Method getRoot = getMethod(queryControllerClass, "getAccessibilityRootNode");
      return (AccessibilityNodeInfo) getRoot.invoke(queryController);
  }

  /*
   * getAutomatorBridge is private so we access the bridge via reflection to use
   * the touchDown / touchUp / touchMove methods.
   */
  public Object getController() throws IllegalArgumentException,
       SecurityException {
    return controller;
  }

  public Object getBridge() {
    return bridge;
  }

  public Method getControllerMethod(final String name, final Class<?>... parameterTypes)
      throws NoSuchMethodException, SecurityException {
    return getMethod(controller.getClass(), name, parameterTypes);
  }

  public Method getMethodInjectInputEvent() throws NoSuchMethodException, SecurityException {
      Class bridgeClass = bridge.getClass();
      if (API_18) {
          bridgeClass = bridgeClass.getSuperclass();
      }
      return getMethod(bridgeClass, "injectInputEvent", InputEvent.class, boolean.class);
  }

  public Method getMethod(final Class clazz, String name, final Class<?>... parameterTypes)
      throws NoSuchMethodException, SecurityException {
    Logger.debug("Finding methods on class: " + clazz);
    final Method method = clazz.getDeclaredMethod(name, parameterTypes);

    method.setAccessible(true);
    return method;
  }
}
