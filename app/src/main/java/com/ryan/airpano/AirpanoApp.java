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

        // DJI SDK requires an "install" step to decrypt and load its classes at runtime.
        // The helper class and method signature can vary depending on the SDK build and packer used.
        if (!installHelper(base)) {
            Log.e(TAG, "DJI SDK Helper install failed. This will likely cause a VerifyError or NoClassDefFoundError.");
        }
    }

    private boolean installHelper(Context context) {
        // Common helper class names used by DJI SDK
        String[] helperClasses = {"com.secneo.sdk.Helper", "com.cySdkyc.clx.Helper"};
        for (String className : helperClasses) {
            try {
                Class<?> helper = Class.forName(className);
                
                // Try install(Context) - common in many versions
                try {
                    helper.getMethod("install", Context.class).invoke(null, context);
                    Log.i(TAG, "Helper.install(Context) OK: " + className);
                    return true;
                } catch (NoSuchMethodException ignored) {
                }

                // Try install(Application) - some versions require the Application instance
                try {
                    helper.getMethod("install", android.app.Application.class).invoke(null, this);
                    Log.i(TAG, "Helper.install(Application) OK: " + className);
                    return true;
                } catch (NoSuchMethodException ignored) {
                }
                
                Log.w(TAG, "Found helper class " + className + " but no compatible install method found via reflection.");
            } catch (ClassNotFoundException ignored) {
                // Class not found, try the next one
            } catch (Throwable t) {
                Log.e(TAG, "Error invoking install on " + className, t);
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize SDK
        // Note: If installHelper failed, the following call will likely throw a VerifyError or NoClassDefFoundError
        try {
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
        } catch (Throwable t) {
            Log.e(TAG, "Failed to initialize SDKManager", t);
        }
    }
}
