
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

xappWrapper.playAd = function(ad, successCallback, failureCallback) {
    cordova.exec(successCallback, failureCallback, 'XAPP', 'playAd', [ad]);
};

xappWrapper.recordPermission = function(grantedCallback, notGrantedCallback) {
    cordova.exec(successCallback, notGrantedCallback, 'XAPP', 'recordPermission', []);
};

xappWrapper.requestRecordPermission = function(apiKey, appKey, successCallback, failureCallback) {
    cordova.exec(successCallback, failureCallback, 'XAPP', 'requestRecordPermission', [apiKey, appKey]);
};

module.exports = xappWrapper;
