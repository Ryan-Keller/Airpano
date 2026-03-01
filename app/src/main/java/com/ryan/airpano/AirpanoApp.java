package com.ryan.airpano;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import dji.v5.manager.SDKManager;
import dji.v5.manager.interfaces.SDKManagerCallback;
import dji.v5.common.register.DJISDKInitEvent;
import dji.v5.common.error.IDJIError;

public class AirpanoApp extends Application {
    private static final String TAG = "AirpanoApp";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // DJI “install/decrypt” step (helper class name varies by SDK build)
        try {
            Class<?> helper = Class.forName("com.secneo.sdk.Helper");
            helper.getMethod("install", Context.class).invoke(null, this);
            Log.i(TAG, "Helper.install OK: com.secneo.sdk.Helper");
        } catch (Throwable e1) {
            try {
                Class<?> helper = Class.forName("com.cySdkyc.clx.Helper");
                helper.getMethod("install", Context.class).invoke(null, this);
                Log.i(TAG, "Helper.install OK: com.cySdkyc.clx.Helper");
            } catch (Throwable e2) {
                Log.e(TAG, "Helper.install FAILED (both helper class names)", e2);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SDKManager.getInstance().init(this, new SDKManagerCallback() {

            @Override
            public void onInitProcess(DJISDKInitEvent event, int totalProcess) {
                Log.i(TAG, "SDK init: " + totalProcess + "%");
            }

            @Override
            public void onRegisterSuccess() {
                Log.i(TAG, "SDK register SUCCESS");
            }

            @Override
            public void onRegisterFailure(IDJIError error) {
                Log.e(TAG, "SDK register FAILED: " + (error == null ? "null" : error.description()));
            }

            @Override
            public void onProductDisconnect(int productId) {
                Log.i(TAG, "onProductDisconnect: " + productId);
            }

            @Override
            public void onProductConnect(int productId) {
                Log.i(TAG, "onProductConnect: " + productId);
            }

            @Override
            public void onProductChanged(int productId) {
                Log.i(TAG, "onProductChanged: " + productId);
            }

            @Override
            public void onDatabaseDownloadProgress(long current, long total) {
                Log.i(TAG, "onDatabaseDownloadProgress: " + current + "/" + total);
            }
        });
    }
}
