package com.balakrishnan.poorna.call_recordingapp;

import android.util.Log;

public class SrcApplog {
    private static final String APP_TAG = "AudioRecorder";

    public static int logString(String message){
        return Log.i(APP_TAG,message);
    }
}