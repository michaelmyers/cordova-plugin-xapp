
package com.xappmedia.cordova.xapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    CallbackContext permissionContext;


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
        } else if (action.equals("requestRecordPermission")) {
            requestRecordPermission(args.getString(0), args.getString(1), callbackContext);
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

        requestStartContext = callbackContext;

        AdRequest adRequest = new AdRequest();
        xappAds.requestAd(adRequest);
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
        Log.e(TAG, "XappAds failed: " + error.getLocalizedMessage());
        sessionStartContext.error(error.toString());
    }

    @Override
    public void onAdRequestFailed(AdRequest adRequest, Error error) {
        Log.e(TAG, "Ad request failed " + error.getLocalizedMessage());
        requestStartContext.error(error.toString());
    }

    @Override
    public void onAdRequestCompleted (final Advertisement advertisement) {
        Log.d(TAG, "Ad received");
        requestStartContext.success(advertisement.getAdName());
        //Plays the ad as a full-screen interstitial
        xappAds.playAsInterstitial(advertisement);
    }

    @Override
    public void onAdStarted(Advertisement advertisement) {
        Log.d(TAG, "Ad started");
    }

    @Override
    public void onAdCompleted(AdResult adResult) {
        Log.d(TAG, "Ad Complete");
    }

    @Override
    public void onAdFailed(Error error) {
        Log.e(TAG, "Ad failed " + error.getLocalizedMessage());
        //Failed to get audioFocus or ad failed to play for some other reason described in the error message
    }

    @Override
    public void onXappAdsTerminated() {
        Log.d(TAG, "XappAds terminated");
    }
}
