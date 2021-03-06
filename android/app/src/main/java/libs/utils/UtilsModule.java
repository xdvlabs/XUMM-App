package libs.utils;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Debug;
import android.content.pm.ApplicationInfo;
import android.app.Activity;
import android.view.WindowManager;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;

import java.util.TimeZone;
import java.util.Calendar;
import java.util.Locale;

import java.text.DecimalFormatSymbols;

import android.os.Vibrator;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;


public class UtilsModule extends ReactContextBaseJavaModule {

    protected final ReactApplicationContext reactContext;

    public UtilsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "UtilsModule";
    }


    /**
     * @author kristiansorens
     */
    @ReactMethod
    public void flagSecure(Boolean enable, Promise promise) {
        final Activity activity = getCurrentActivity();

        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (enable) {
                        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
                    } else {
                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                    }

                }
            });
        }

        promise.resolve(true);
    }


    @ReactMethod
    public void isFlagSecure(Promise promise) {
        final Activity activity = getCurrentActivity();

        if (activity != null) {
            if ((activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_SECURE) != 0) {
                promise.resolve(true);
            } else {
                promise.resolve(false);
            }
        } else {
            promise.resolve(false);
        }
    }


    /**
     * @author jail-monkey
     */
    @ReactMethod
    public void isDebugged(Promise promise) {
        if (Debug.isDebuggerConnected()) {
            promise.resolve(true);
        }

        boolean isDebug = (reactContext.getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        promise.resolve(isDebug);
    }


    /**
     * @author Kevin Kowalewski
     */
    @ReactMethod
    public void isRooted(Promise promise) {
        try {
            boolean isRooted = checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
            promise.resolve(isRooted);
        } catch (Exception e) {
            promise.reject(e);
        }
    }


    @ReactMethod
    public void restartBundle() {
        PackageManager packageManager = reactContext.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(reactContext.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        reactContext.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

    @ReactMethod
    public void exitApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @ReactMethod
    public void getElapsedRealtime(Promise promise) {
        // System time in milliseconds
        long time = android.os.SystemClock.elapsedRealtime();

        // React Native bridge complains if we try to pass back a long directly
        promise.resolve(Long.toString(time / 1000));
    }


    @ReactMethod
    public void getTimeZone(Promise promise) {
        try {
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            TimeZone zone = calendar.getTimeZone();
            promise.resolve(zone.getID());
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getLocalSetting(Promise promise) {
        try {

            WritableMap settings = Arguments.createMap();

            Locale locale = getReactApplicationContext().getResources().getConfiguration().getLocales().get(0);

            DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);

            String languageCode = Locale.getDefault().getLanguage();

            settings.putString("locale", String.valueOf(locale));
            settings.putString("languageCode", languageCode);
            settings.putString("separator", String.valueOf(symbols.getDecimalSeparator()));
            settings.putString("delimiter", String.valueOf(symbols.getGroupingSeparator()));

            promise.resolve(settings);

        } catch (Exception e) {
            promise.reject(e);
        }
    }


    // From:
    // https://github.com/junina-de/react-native-haptic-feedback
    @ReactMethod
    public void hapticFeedback(String type) {
        Vibrator v = (Vibrator) reactContext.getSystemService(Context.VIBRATOR_SERVICE);
        if (v == null) return;
        long durations[] = {0, 20};
        int hapticConstant = 0;

        switch (type) {
            case "impactLight":
                durations = new long[]{0, 20};
                break;
            case "impactMedium":
                durations = new long[]{0, 40};
                break;
            case "impactHeavy":
                durations = new long[]{0, 60};
                break;
            case "notificationSuccess":
                durations = new long[]{0, 40, 60, 20};
                break;
            case "notificationWarning":
                durations = new long[]{0, 20, 60, 40};
                break;
            case "notificationError":
                durations = new long[]{0, 20, 40, 30, 40, 40};
                break;
        }

        if (hapticConstant != 0) {
            v.vibrate(hapticConstant);
        } else {
            v.vibrate(durations, -1);
        }
    }


    @ReactMethod
    public void timeoutEvent(final String id, final int timeout) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getReactApplicationContext().hasActiveReactInstance()) {
                    getReactApplicationContext()
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit("Utils.timeout", id);
                }
            }
        }, timeout);
    }

    @ReactMethod
    public void addListener(String eventName) {
        // Keep: Required for RN built in Event Emitter Calls.
    }

    @ReactMethod
    public void removeListeners(Integer count) {
        // Keep: Required for RN built in Event Emitter Calls.
    }


    // private methods
    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return in.readLine() != null;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }
}