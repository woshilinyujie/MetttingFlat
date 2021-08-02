package com.example.meettingflat.Utils;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import static android.content.Context.KEYGUARD_SERVICE;

public class DeviceUtils {
    //获取序列号
    public static String getSerialNumber(Context context){

        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

    }



}
