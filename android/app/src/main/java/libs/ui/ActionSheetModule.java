// Licence
// https://github.com/yfuks/react-native-action-sheet

package libs.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.util.List;
import java.util.ArrayList;

import com.xrpllabs.xumm.R;

public class ActionSheetModule extends ReactContextBaseJavaModule {
  WritableMap response;

  public ActionSheetModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "ActionSheetAndroid";
  }

  @ReactMethod
  public void showActionSheetWithOptions(final ReadableMap options, final Callback callback) {
    Activity currentActivity = getCurrentActivity();

    if (currentActivity == null) {
      response = Arguments.createMap();
      response.putString("error", "can't find current Activity");
      callback.invoke(response);
      return;
    }

    final List<String> titles = new ArrayList<String>();

    if (options.hasKey("options")) {
      ReadableArray customButtons = options.getArray("options");
      for (int i = 0; i < customButtons.size(); i++) {
        int currentIndex = titles.size();
        titles.add(currentIndex, customButtons.getString(i));
      }
    }

    ArrayAdapter<String> adapter = new ArrayAdapter<String>(currentActivity,
            R.layout.dialog_item, titles);
    AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity, R.style.DialogStyle);
    if (options.hasKey("title") && options.getString("title") != null && !options.getString("title").isEmpty()) {
      builder.setTitle(options.getString("title"));
    }

    /* @TODO message currently disable the options
    if (options.hasKey("message") && options.getString("message") != null && !options.getString("message").isEmpty()) {
      builder.setMessage(options.getString("message"));
    }
    */

    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int index) {
        callback.invoke(index);
      }
    });

    final AlertDialog dialog = builder.create();
    /**
     * override onCancel method to callback cancel in case of a touch outside of
     * the dialog or the BACK key pressed
     */
    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override
      public void onCancel(DialogInterface dialog) {
        dialog.dismiss();
        callback.invoke();
      }
    });
    dialog.show();
  }
}
