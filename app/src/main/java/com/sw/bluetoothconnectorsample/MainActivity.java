package com.sw.bluetoothconnectorsample;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ListView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.Collection;

public class MainActivity extends Activity implements BeaconConsumer, RangeNotifier {

    private final String TAG = "BTConnectorSample";
    private static final int PERMISSION_REQUEST_TAG = 1;
    private BeaconManager beaconManager;
    private boolean isLocationServiceOn = false, isBluetoothServiceOn = false, isBluetoothAdminServiceOn = false;
    private final String[] permissionRequired = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN};
    private ListView lv;
    private CustomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager
                .getBeaconParsers()
                .add(new org.altbeacon.beacon.BeaconParser()
                        .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:52-52"));
        beaconManager.setBackgroundMode(false);
        beaconManager.setForegroundBetweenScanPeriod(5000);
        checkPermission();
        new BackgroundPowerSaver(this);
        lv = (ListView) findViewById(R.id.lv);
        mAdapter = new CustomAdapter(this, null);
        lv.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissionRequired)
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{
                                    permission
                            },
                            PERMISSION_REQUEST_TAG);
                else {
                    switch (permission) {
                        case Manifest.permission.ACCESS_COARSE_LOCATION:
                            isLocationServiceOn = true;
                            break;
                        case Manifest.permission.BLUETOOTH:
                            isBluetoothServiceOn = true;
                            break;
                        case Manifest.permission.BLUETOOTH_ADMIN:
                            isBluetoothAdminServiceOn = true;
                            break;
                        default:
                    }
                }
        } else {
            isLocationServiceOn = true;
            isBluetoothServiceOn = true;
            isBluetoothAdminServiceOn = true;
        }
        if (isLocationServiceOn && isBluetoothServiceOn && isBluetoothAdminServiceOn)
            initAltBeacon();
    }

    private void initAltBeacon() {
        beaconManager.bind(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_TAG: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0)
                    break;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        switch (permissions[i]) {
                            case Manifest.permission.ACCESS_COARSE_LOCATION:
                                isLocationServiceOn = true;
                                break;
                            case Manifest.permission.BLUETOOTH:
                                isBluetoothServiceOn = true;
                                break;
                            case Manifest.permission.BLUETOOTH_ADMIN:
                                isBluetoothAdminServiceOn = true;
                                break;
                            default:
                        }
                }
                if (isLocationServiceOn && isBluetoothServiceOn && isBluetoothAdminServiceOn)
                    initAltBeacon();
                break;
            }
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(this);
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("BTConnectorRegion", null, null, null));
        } catch (RemoteException e) {
            android.util.Log.e(TAG, "onBeaconServiceConnect e : " + e.toString());
        }
    }

    @Override
    public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
        if (beacons.size() > 0) {
            for (Beacon beacon : beacons)
                android.util.Log.d(TAG, "Id1:" + beacon.getId1() + "\nId2:" + beacon.getId2() + "\nId3:" + beacon.getId3());
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setData(beacons);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
