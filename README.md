## Installation

Currently, we only support installing the plugin from github:

```
$ cordova plugin add https://github.com/michaelmyers/cordova-plugin-xapp
```

## Basic Usage

### Requesting Record Permission

For iOS and Android versions Marshmallow and above, record permission first must be granted by the user.

```javascript
XAPP.requestRecordPermission('yourAPIKey', 'yourAPPKey', function (message) {
    // Permission was granted
}, function (error) {
    // Permission was not granted.
});
```

### Starting the Session

Before requesting an ad, you first must start the session.  Place this with other app startup code:

```javascript
XAPP.startSession('yourAPIKey', 'yourAPPKey', function() {
    //Session started, you are ready to go
}, function (error) {
    //Something went wrong, check the error message
});
```

### Requesting An Ad

To request an ad:

```javascript
XAPP.requestAd(function (ad) {
    //Ad received
    //Store off the ad, this is what is used to play it later
    self.currentAd = ad;
}, function (error) {
    //Something went wrong, check the error message
});

```

### Ad Playback

Ad playback:

```javascript
//Pass in the ad you received from XAPP.requestAd
XAPP.playAd(self.currentAd, function () {
    //Ad finished playing successfully.
}, function (error) {
    //Ad may or may not have played, it is finished and there was a problem
});
```

Currently, we only support a full-screen interstitial playback.  The content will be blocked until the ad is complete.

## Harness

The harness (found within the `harness/` directory) is a simple program that tests the plugin and is a good place to start for example code.

To run, first from the root project directory run:

```
$ sh refresh-plugin.sh
```

This will setup the platforms with the plugin.  Then open Android Studio and open the existing project at the `harness/platforms/android` directory.  

## Cordova Development Tips

* [Create Plugins | Cordova Documentation](http://cordova.apache.org/docs/en/latest/guide/hybrid/plugins/index.html)
* [Cordova Development Tips](https://gist.github.com/revolunet/fdc0eefbcdf8cc58edea)

## TODO

- [] iOS Support
- [] Support other types of companions
