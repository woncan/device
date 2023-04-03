# Device

Device SDK说明

##### Gradle集成SDK

######  1.1在Project的build.gradle文件中配置repositories，添加maven仓库地址（新版本Android studio需要在settings.gradle里面配置）
```
    repositories {
            maven { url "https://jitpack.io" }
    }
```

###### 在主工程的build.gradle文件配置dependencies

[![](https://jitpack.io/v/woncan/device.svg)](https://jitpack.io/#woncan/device)
```
    dependencies {
        implementation 'com.github.woncan:device:latest.release'
    }
```

##### 添加混淆配置
```
-keep class com.woncan.device.**{*;}
-keep class com.cmcc.sy.hap.** { *;}
-keep class com.qxwz.sdk.** { *;}
-keep class com.sixents.sdk.** { *;}
```
###  使用



#### 搜索设备
**以下方式选择其一**
- 搜索所有设备
```
    ScanManager.scanDevice(this, device -> {  
    });
```
- 搜索蓝牙设备
```
    ScanManager.scanBluetoothDevice(this, device -> {
    });
```
- 搜索串口设备
```
    ScanManager.scanSerialDevice(this, device -> {
    }); 
```
#### 关闭搜索
```
    ScanManager.stopScan(context);
```

#### 连接并配置设备
```
    //连接设备
    device.connect(context);

    //设置数据频率
    device.setInterval(DeviceInterval.HZ_5);

    //配置Ntrip账号
    device.setAccount("ip",port,"account","password","mountPoint");

    //配置激光开关
    device.setLaserState(true);

```

#### 注册回调
```
    //设备状态
    device.registerSatesListener(new DeviceStatesListener() {
        @Override
        public void onConnectionStateChange(boolean isConnect) {
            //连接状态
        }

        @Override
        public void onDeviceAccountChange(@NonNull DeviceNtripAccount account) {
            //写入设备的Ntrip账号
        }

        @Override
        public void onDeviceInfoChange(@NonNull DeviceInfo deviceInfo) {
            //设备信息
        }

        @Override
        public void onLaserStateChange(boolean isOpen) {
            //激光开关
        }
    });

    //设备定位
    device.registerLocationListener(new WLocationListener() {
        @Override
        public void onReceiveLocation(@NonNull WLocation wLocation) {
            //定位信息
        }

        @Override
        public void onError(int i, @NonNull String s) {
            //错误信息
        }
    });
    
    //卫星数据
    device.registerSatelliteListener(new SatelliteListener() {
        @Override
        public void onReceiveSatellite(List<SatelliteInfo> list) {
            //卫星数据
        }
    });
```
- [WLocation](https://github.com/woncan/device/blob/master/readme/bean.md#WLocation)
- [SatelliteInfo](https://github.com/woncan/device/blob/master/readme/bean.md#SatelliteInfo)
- [errCode](https://github.com/woncan/device/blob/master/readme/errCode.md)
#### 静态记录
```
    //开启RTCM
    device.openRTCM(new RTCM[]{RTCM.RTCM1074}, RTCMInterval.SECOND_3);
    //注册监听
    device.registerRTCMAListener(new RTCMListener() {
        @Override
        public void onReceiveRTCM(int[] type, byte[] bytes) {
            //type：RTCM类型    bytes：RTCM数据
        }

        @Override
        public void onReceiveSFR(byte[] bytes) {
            //SFR数据
        }
    });
    //记录完毕后关闭RTCM 或者 重启设备
    device.closeRTCM();
```
#### 断开连接
```
    device.disconnect();

```
