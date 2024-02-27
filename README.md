# Device

Device SDK Description  [中文文档](https://github.com/woncan/device/blob/master/readme/README_CN.md)

##### Integrate SDK with Gradle

######  1.1 Configure repositories in the build.gradle file of the Project, and add the Maven repository ddress. (For the latest version of Android Studio, the repository address needs to be configured in the settings.gradle file.)
```
    repositories {
            maven { url "https://jitpack.io" }
    }
```

###### Configure dependencies in the main project's build.gradle file.

[![](https://jitpack.io/v/woncan/device.svg)](https://jitpack.io/#woncan/device)
```
    dependencies {
        implementation 'com.github.woncan:device:latest.release'
    }
```

##### Please add obfuscation configuration.
```
-keep class com.woncan.device.**{*;}
-keep class com.cmcc.sy.hap.** { *;}
-keep class com.qxwz.sdk.** { *;}
-keep class com.sixents.sdk.** { *;}
```
###  Use



#### Search devices
**Please choose one of the following options.**
- Search all devices.
```
    ScanManager.scanDevice(this, device -> {  
    });
```
- Search for Bluetooth devices.
```
    ScanManager.scanBluetoothDevice(this, device -> {
    });
```
- Search for USB devices.
```
    ScanManager.scanSerialDevice(this, device -> {
    }); 
```
#### Stop searching
```
    ScanManager.stopScan(context);
```

#### Connect and configure the device.
```
    //Connect the device.
    device.connect(context);

    //Set data frequency
    device.setInterval(DeviceInterval.HZ_5);

    //Set up Ntrip account.
    device.setAccount("ip",port,"account","password","mountPoint");

    //Configure laser switch
    device.setLaserState(true);

```

#### Register callback.
```
    //Device state
    device.registerSatesListener(new DeviceStatesListener() {
        @Override
        public void onConnectionStateChange(boolean isConnect) {
            //Connection state
        }

        @Override
        public void onDeviceAccountChange(@NonNull DeviceNtripAccount account) {
            //Ntrip account for writing to device
        }

        @Override
        public void onDeviceInfoChange(@NonNull DeviceInfo deviceInfo) {
            //Device information
        }

        @Override
        public void onLaserStateChange(boolean isOpen) {
            //laser switch
        }
    });

    //Device positioning
    device.registerLocationListener(new WLocationListener() {
        @Override
        public void onReceiveLocation(@NonNull WLocation wLocation) {
            //Positioning information
        }

        @Override
        public void onError(int i, @NonNull String s) {
            //Error message
        }
    });
    
    //Satellite data
    device.registerSatelliteListener(new SatelliteListener() {
        @Override
        public void onReceiveSatellite(List<SatelliteInfo> list) {
            //Satellite data
        }
    });
    //NMEA data
    device.setNMEAEnable(NMEA.GSV , true);
    device.setNMEAEnable(NMEA.GSA , true);
    device.setNMEAEnable(NMEA.GLL , true);
    device.setNMEAEnable(NMEA.GMC , true);
    device.setNMEAEnable(NMEA.VTG , true);

    device.setNMEAListener(new NMEAListener() {
        @Override
        public void onReceiveNMEA(String s) {
            //NMEA data
        }
    });
```
- [WLocation](https://github.com/woncan/device/blob/master/readme/bean.md#WLocation)
- [SatelliteInfo](https://github.com/woncan/device/blob/master/readme/bean.md#SatelliteInfo)
- [errCode](https://github.com/woncan/device/blob/master/readme/errCode.md)
#### Static record
```
    //Enable RTCM
    device.openRTCM(new RTCM[]{RTCM.RTCM1074}, RTCMInterval.SECOND_3);
    //Register listener
    device.registerRTCMAListener(new RTCMListener() {
        @Override
        public void onReceiveRTCM(int[] type, byte[] bytes) {
            //type：RTCM type    bytes：RTCM data
        }

        @Override
        public void onReceiveSFR(byte[] bytes) {
            //SFR data
        }
    });
    //Please turn off the RTCM or restart the device after recording.
    device.closeRTCM();
```
#### disconnect
```
    device.disconnect();

```
