package com.myapps.compilerfarsi;

import android.app.Application;

import io.github.kbiakov.codeview.classifier.CodeProcessor;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CodeProcessor.init(getApplicationContext());
    }
}
