
var argscheck = require('cordova/argscheck'),
    exec = require('cordova/exec');

var xappWrapper = {};

xappWrapper.version = function (successCallback, failureCallback) {
    cordova.exec(successCallback, failureCallback, 'XAPP', 'version', []);
};

xappWrapper.startSession = function(apiKey, appKey, successCallback, failureCallback) {
    cordova.exec(successCallback, failureCallback, 'XAPP', 'startSession', [apiKey, appKey]);
};

xappWrapper.requestAd = function(successCallback, failureCallback) {
    cordova.exec(successCallback, failureCallback, 'XAPP', 'requestAd', []);
};

xappWrapper.playAd = function(successCallback, failureCallback) {
    cordova.exec(successCallback, failureCallback, 'XAPP', 'playAd', []);
};

xappWrapper.requestRecordPermission = function(successCallback, failureCallback) {
    cordova.exec(successCallback, failureCallback, 'XAPP', 'requestRecordPermission', []);
};

module.exports = xappWrapper;