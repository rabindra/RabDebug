package com.rab.debug.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Rabindra on 11/20/17.
 */

public class BatteryUtils {
    private final static String TAG = "BatteryUtils";
    Context mContext;

    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            updateBatteryInfo(intent);
        }
    };

    private BatteryUtils(){

    }

    private BatteryUtils(Context context){
        mContext = context;
    }

    public static BatteryUtils getInstance(Context context){
        return new BatteryUtils(context);
    }

    public void readChargeInfo(){

        BatteryManager mBatteryManager =
                (BatteryManager) mContext.getSystemService(Context.BATTERY_SERVICE);
        Long energy =
                mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
        String msg =  "Remaining energy = " + energy + "nWh";
        Log.i(TAG,msg);
        _presentToast( msg );

        /*
            BATTERY_PROPERTY_CHARGE_COUNTER   Remaining battery capacity in microampere-hours
            BATTERY_PROPERTY_CURRENT_NOW      Instantaneous battery current in microamperes
            BATTERY_PROPERTY_CURRENT_AVERAGE  Average battery current in microamperes
            BATTERY_PROPERTY_CAPACITY         Remaining battery capacity as an integer percentage
            BATTERY_PROPERTY_ENERGY_COUNTER   Remaining energy in nanowatt-hours
        */

        loadBatterySection();
    }

    private void loadBatterySection() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        mContext.registerReceiver(mBatteryInfoReceiver, intentFilter);
    }

    private void updateBatteryInfo(Intent intent){
        boolean present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);

        if (present) {

            _readHealth(intent);
            _readBatteryPct(intent);
            _readPluginInfo(intent);
            _readChargingInfo(intent);
            _readTechnology(intent);
            _readTempVoltCapacity(intent);

        } else {
            _presentToast("No Battery present");
        }
    }

    private void _readTempVoltCapacity( Intent intent) {

        int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
        String tempTv = "-";
        String voltageTv = "-";
        String capacityTv = "-";

        if (temperature > 0) {
            float temp = ((float) temperature) / 10f;
            tempTv = "Temperature : " + temp + "°C";
        }

        int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

        if (voltage > 0) {
            voltageTv = "Voltage : " + voltage + " mV";
        }

        long capacity = _getBatteryCapacity();

        if (capacity > 0) {
            capacityTv = "Capacity : " + capacity + " mAh";
        }

        Log.i(TAG, tempTv+" | "+voltageTv+" | "+capacityTv);
        _presentToast(tempTv+" | "+voltageTv+" | "+capacityTv);
    }

    private void _readTechnology(Intent intent) {
        String technologyTv = "Unknown";
        if (intent.getExtras() != null) {
            String technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);

            if (!"".equals(technology)) {
                technologyTv= "Technology : " + technology;
            }
        }

        Log.i(TAG, technologyTv);
        _presentToast(technologyTv);
    }

    private void _readChargingInfo(Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        String statusLbl = "battery_status_discharging";

        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusLbl = "battery_status_charging";
                break;

            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusLbl = "battery_status_discharging";
                break;

            case BatteryManager.BATTERY_STATUS_FULL:
                statusLbl ="battery_status_full";
                break;

            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                statusLbl = "Unknown";
                break;

            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
            default:
                statusLbl = "battery_status_discharging";
                break;
        }


        String chargingStatusTv = "Battery Charging Status : " + statusLbl;
        Log.i(TAG, chargingStatusTv);
        _presentToast(chargingStatusTv);
    }

    private void _readPluginInfo(Intent intent) {
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
        String pluggedLbl = "battery_plugged_none";

        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                pluggedLbl = "battery_plugged_wireless";
                break;

            case BatteryManager.BATTERY_PLUGGED_USB:
                pluggedLbl = "battery_plugged_usb";
                break;

            case BatteryManager.BATTERY_PLUGGED_AC:
                pluggedLbl = "battery_plugged_ac";
                break;

            default:
                pluggedLbl = "battery_plugged_none";
                break;
        }

        // display plugged status ...
        String pluggedTv = "Plugged : " + pluggedLbl;
        Log.i(TAG, pluggedTv);
        _presentToast(pluggedTv);
    }

    private void _readBatteryPct(Intent intent) {

        String batteryPctTv = "";

        // Calculate Battery Pourcentage ...
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if (level != -1 && scale != -1) {
            int batteryPct = (int) ((level / (float) scale) * 100f);
            batteryPctTv = "Battery Pct : " + batteryPct + " %";
        }

        Log.i(TAG, batteryPctTv);
        _presentToast(batteryPctTv);
    }

    private long _getBatteryCapacity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager mBatteryManager = (BatteryManager) mContext.getSystemService(Context.BATTERY_SERVICE);
            Long chargeCounter = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            Long capacity = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

            if (chargeCounter != null && capacity != null) {
                long value = (long) (((float) chargeCounter / (float) capacity) * 100f);
                return value;
            }
        }

        return 0;
    }

    private void _readHealth(Intent intent){
        int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
        String healthLbl = "Unknown";

        switch (health) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                healthLbl = "battery_health_cold";
                break;

            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthLbl = "battery_health_dead";
                break;

            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthLbl ="battery_health_good";
                break;

            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthLbl = "battery_health_over_voltage";
                break;

            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthLbl = "battery_health_overheat";
                break;

            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthLbl = "battery_health_unspecified_failure";
                break;

            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
            default:
                break;
        }


        // display battery health ...
        String healthTv = "Health : " + healthLbl;
        Log.i(TAG, healthTv);
        _presentToast(healthTv);
    }

    private void _presentToast(String msg){
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }
}
