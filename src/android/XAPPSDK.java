
package com.xappmedia.cordova.xapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;


import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;


import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;


import xappmedia.sdk.AdRequest;
import xappmedia.sdk.Advertisement;
import xappmedia.sdk.XappAds;
import xappmedia.sdk.XappAdsListener;
import xappmedia.sdk.model.AdResult;
import xappmedia.sdk.model.Error;
import xappmedia.sdk.permissions.ui.Permission;
import xappmedia.sdk.permissions.ui.RequestPermissionsCreativeFragment;
import xappmedia.sdk.permissions.ui.RequestPermissionsSoftAskActivity;


public class XAPPSDK extends CordovaPlugin implements XappAdsListener {

    int REQUEST_PERMISSIONS = 567;
    String TAG = "XAPP";

    XappAds xappAds;

    CallbackContext sessionStartContext;
    CallbackContext requestStartContext;
    CallbackContext playbackContext;
    CallbackContext permissionContext;

    HashMap<String, Advertisement> adStore;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("version")) {
            callbackContext.success(XappAds.VERSION_NAME);
            return true;
        } else if (action.equals("startSession")) {
            startSession(args.getString(0), args.getString(1), callbackContext);
            return true;
        } else if (action.equals("requestAd")) {
            requestAd(callbackContext);
            return true;
        } else if (action.equals("playAd")) {
            playAd(args.getString(0), callbackContext);
            return true;
        } else if (action.equals("recordPermission")) {

            int permissionCheck = ContextCompat.checkSelfPermission(cordova.getActivity(),
                    Manifest.permission.RECORD_AUDIO);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                callbackContext.success("Permission granted");
            } else {
                callbackContext.error("Permission not granted");
            }

            return true;
        } else if (action.equals("requestRecordPermission")) {
            requestRecordPermission(args.getString(0), args.getString(1), callbackContext);
            return true;
        }
        return false;
    }

    public void requestRecordPermission(String apiKey, String appKey, CallbackContext callbackContext) {

        permissionContext = callbackContext;

        ArrayList<Permission> collection = new ArrayList<Permission>();
        collection.add(Permission.newPermission(Manifest.permission.RECORD_AUDIO).build());

        Intent requestPermission = RequestPermissionsSoftAskActivity.newIntent(collection)
                .keys(apiKey, appKey)
                .type(RequestPermissionsCreativeFragment.TYPE_IMAGE)
                .creativeOnly(true).buildIntent(this.cordova.getActivity());

        this.cordova.startActivityForResult(this, requestPermission, REQUEST_PERMISSIONS);

    }

    @Override
    public void onActivityResult(int code, int result, Intent data) {
        super.onActivityResult(code, result, data);

        if (code == REQUEST_PERMISSIONS) {
            if (result == Activity.RESULT_OK) {
                final List<Permission> granted = data.getParcelableArrayListExtra(RequestPermissionsSoftAskActivity.RESULT_PERMISSION_GRANTED_LIST);
                final List<Permission> denied = data.getParcelableArrayListExtra(RequestPermissionsSoftAskActivity.RESULT_PERMISSION_DENIED_LIST);

                StringBuilder sb = new StringBuilder();

                if (!denied.isEmpty()) {
                    sb.append("The following have not been granted:\n");
                    for (Permission p : denied) {
                        sb.append(p.getName()).append("\n");
                    }
                }

                if (!granted.isEmpty()) {
                    sb.append("The following have been granted: \n");
                    for (Permission p : granted) {
                        sb.append(p.getName()).append("\n");
                    }
                }

                permissionContext.success(sb.toString());
            }
        }
    }

    public void startSession(final String apiKey, final String appKey, CallbackContext callbackContext) {

        sessionStartContext = callbackContext;
        xappAds = new XappAds();
        final XAPPSDK listener = this;

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                xappAds.start(apiKey, appKey, null, null, cordova.getActivity().getApplicationContext(), listener);
            }
        });
    }

    public void requestAd(CallbackContext callbackContext) {

        if (xappAds == null) {
            callbackContext.error("XAPP must be started before you can request an ad");
        } else {
            requestStartContext = callbackContext;

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    AdRequest adRequest = new AdRequest();
                    xappAds.requestAd(adRequest);
                }
            });
        }
    }

    public void playAd(String ad, CallbackContext callbackContext) {

        if (adStore == null) {
            callbackContext.error("A ad must be requested before it can be played.");
        } else if (ad != null && !ad.isEmpty()) {
            callbackContext.error("Ad doesn't exist.");
        } else {
            playbackContext = callbackContext;
            //pull out the ad
            Advertisement advertisement = adStore.get(ad);
            xappAds.playAsInterstitial(advertisement);
        }
    }

    /**
     * XappAds Listener Methods
     */
    @Override
    public void onXappAdsStarted () {
        sessionStartContext.success();
    }

    @Override
    public void onXappAdsFailed(Error error) {
        Log.e(TAG, "XappAds failed: " + error.toString());
        sessionStartContext.error(error.toString());
    }

    @Override
    public void onAdRequestFailed(AdRequest adRequest, Error error) {
        Log.e(TAG, "Ad request failed " + error.toString());
        requestStartContext.error(error.toString());
    }

    @Override
    public void onAdRequestCompleted (final Advertisement advertisement) {
        Log.d(TAG, "Ad received");

        String adKey = advertisement.getAdName();

        if (adStore == null) {
            adStore = new HashMap<String, Advertisement>();
        }

        adStore.put(adKey, advertisement);
        requestStartContext.success(adKey);

    }

    @Override
    public void onAdStarted(Advertisement advertisement) {
        Log.d(TAG, "Ad started");
    }

    @Override
    public void onAdCompleted(AdResult adResult) {
        Log.d(TAG, "Ad Complete");
        playbackContext.success();
    }

    @Override
    public void onAdFailed(Error error) {
        Log.e(TAG, "Ad failed " + error.toString());
        playbackContext.error(error.toString());
    }

    @Override
    public void onXappAdsTerminated() {
        Log.d(TAG, "XappAds terminated");
    }
}
