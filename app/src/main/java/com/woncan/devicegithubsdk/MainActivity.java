package com.woncan.devicegithubsdk;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.woncan.device.Device;
import com.woncan.device.Jni;
import com.woncan.device.JniImp;
import com.woncan.device.NMEA;
import com.woncan.device.ScanManager;
import com.woncan.device.bean.DeviceInfo;
import com.woncan.device.bean.DeviceNtripAccount;
import com.woncan.device.bean.WLocation;
import com.woncan.device.device.DeviceInterval;
import com.woncan.device.listener.AttachmentStateListener;
import com.woncan.device.listener.DeviceStatesListener;
import com.woncan.device.listener.WLocationListener;
import com.woncan.device.uitl.AttachmentState;
import com.woncan.devicegithubsdk.databinding.ActivityMainBinding;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "WONCAN_DEVICE_MAIN";

    private ActivityMainBinding binding;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceAdapter adapter = new DeviceAdapter();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        binding.btnSearch.setOnClickListener(v -> {
            //搜索所有设备
            if (!requestPermission()) {
                Toast.makeText(this, "需要蓝牙和定位权限", Toast.LENGTH_SHORT).show();
                return;
            }
            adapter.setNewInstance(null);
            adapter.addData(ScanManager.getPhoneDevice());
            ScanManager.scanDevice(this, device -> {
                Log.i(TAG, "onCreate: " + device.getName());
                if (!adapter.getData().contains(device)) {
                    adapter.addData(device);
                }
            });
        });
        binding.btnAttach.setOnClickListener(v -> {
            if (mDevice==null){
                return;
            }
            mDevice.registerAttachmentStateListener(this, attachmentState -> {
                Log.i(TAG, "onCreate: "+attachmentState);
            });
        });
        binding.btnDetach.setOnClickListener(v -> {
            if (mDevice==null){
                return;
            }
            mDevice.unregisterAttachmentStateListener(this);
        });


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            ScanManager.stopScan(MainActivity.this);
            connect(adapter.getItem(position));
        });

    }


    private Device mDevice=null;

    private void connect(Device device) {
        device.registerSatesListener(new DeviceStatesListener() {
            @Override
            public void onConnectionStateChange(boolean isConnect) {
                binding.tvLog.append(isConnect ? "设备已连接\n" : "断开连接\n");
                if (isConnect){
                    mDevice=device;
                }else {
                    mDevice=null;
                }
            }

            @Override
            public void onDeviceAccountChange(@NonNull DeviceNtripAccount account) {
                super.onDeviceAccountChange(account);

            }

            @Override
            public void onDeviceInfoChange(@NonNull DeviceInfo deviceInfo) {
                binding.tvDeviceInfo.setText(String.format(Locale.CHINA, "型号：%s\n设备ID：%s\n产品名：%s", deviceInfo.getModel(), deviceInfo.getDeviceID(), deviceInfo.getProductNameZH()));
            }

            @Override
            public void onLaserStateChange(boolean isOpen) {

            }
        });

        device.registerLocationListener(new WLocationListener() {
            @Override
            public void onReceiveLocation(@NonNull WLocation wLocation) {
                Log.i(TAG, "onReceiveLocation: wLocation");
                binding.tvLocation.setText(String.format(Locale.CHINA, "纬度：%.8f\n经度：%.8f\n海拔：%.3f\n解状态：%d", wLocation.getLatitude(), wLocation.getLongitude(), wLocation.getAltitude(), wLocation.getFixStatus()));
            }

            @Override
            public void onError(int i, @NonNull String s) {
                binding.tvLog.append(String.format(Locale.CHINA, "onError:%d  %s\n", i, s));
            }
        });
        device.setNMEAListener(s -> Log.i(TAG, "onReceiveNMEA: "+s));
//        device.openRTCM(new RTCM[]{RTCM.RTCM1074}, RTCMInterval.SECOND_3);
//        device.registerRTCMAListener(new RTCMListener() {
//            @Override
//            public void onReceiveRTCM(int[] ints, byte[] bytes) {
//
//            }
//
//            @Override
//            public void onReceiveSFR(byte[] bytes) {
//
//            }
//        });
//        device.registerSatelliteListener(new SatelliteListener() {
//            @Override
//            public void onReceiveSatellite(List<SatelliteInfo> list) {
//
//            }
//        });

//        device.closeRTCM();
//        device.setLaserState(true);
//        int port=1;
        device.setNMEAEnable(NMEA.GGA , true);
        device.setNMEAEnable(NMEA.GSV , true);
        device.setNMEAEnable(NMEA.GSA , true);
        device.setNMEAEnable(NMEA.GLL , true);
        device.setNMEAEnable(NMEA.GMC , true);
        device.setNMEAEnable(NMEA.VTG , true);

        device.setNMEAListener(s -> {

        });
        device.connect(this);
        new Thread(() -> {
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            device.setInterval(DeviceInterval.HZ_5);
        }).start();

//        device.setAccount("",0,"","","");
    }


    private final ActivityResultLauncher<String[]> resultLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            });


    private boolean requestPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        List<String> list = new ArrayList<>();
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            list.add(Manifest.permission.BLUETOOTH);
        }
        if (checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            list.add(Manifest.permission.BLUETOOTH_ADMIN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.BLUETOOTH_SCAN);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }

        resultLauncher.launch(list.toArray(new String[0]));
        return list.size() == 0;
    }







}