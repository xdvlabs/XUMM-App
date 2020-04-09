package com.xrpllabs.xumm;

import com.reactnativenavigation.NavigationActivity;

import android.os.Bundle;
import androidx.annotation.Nullable;

public class MainActivity extends NavigationActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSplashLayout();
  }

  private void setSplashLayout() {
          setContentView(R.layout.activity_splash);
  }
}