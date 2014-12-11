var net = require("net");
var exec = require('child_process').exec;
var ADB = require('appium-adb');
//var adb = new ADB();

var template = '{"action":"Prepare", "cmd":"foo"}';

// var args = process.argv.slice(2).join(' ');

// adb.getConnectedDevices(function(err, devices) {
// 	if (err) {
// 		console.log(err);
// 		process.exit(1);
// 	}
// 	devices.forEach(function(device) {
// 		var arg = "adb -s " + device.udid + " " + args;
// 		exec(arg, function(err, stdout, stderr){
// 			if (err) {	 console.log ('error for device:', device.udid, err); }
// 			console.log(device.udid, ':', stdout, stderr || '');
// 		});
// 	});
// });

var startSock = function(port) {
  var socket = net.connect(port, function () {
    socket.setEncoding('utf8');
    console.log("Connected on " + port);
  });

  socket.on('error', function (err) {
    console.dir(err);
  });

  socket.on('close', function () {
    console.dir("Closing " + port);
  });

  socket.on('data', function(data) {
    console.dir(data);
  });

  socket.write(template);
}

//exports.start = function (cb, onDie) {
var start = function (cb, onDie) {
  startSock(4724);

};
start(); 