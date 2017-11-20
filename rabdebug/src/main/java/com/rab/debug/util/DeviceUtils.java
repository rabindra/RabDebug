package com.rab.debug.util;

import android.content.Context;
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Rabindra on 11/20/17.
 */

public class DeviceUtils {


    public static String getDeviceSerial(Context context) {
        final String EMPTY = "";
        String deviceSerial = null;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            deviceSerial = (String) get.invoke(c, "sys.serialnumber", EMPTY);

            if (TextUtils.isEmpty(deviceSerial)) {
                deviceSerial = (String) get.invoke(c, "ril.serialnumber", EMPTY);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if (TextUtils.isEmpty(deviceSerial)) {
                deviceSerial = android.os.Build.SERIAL;
            }

            if (TextUtils.isEmpty(deviceSerial)) {
                deviceSerial = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }
        }

        return deviceSerial;
    }
}
