##### WLocation

| 方法名 | 说明| 备注|
| :--| :-- | :-- |
| getFixStatus| 获取定位状态 |0=无法定位；1=单点定位；5=浮点定位；4=固定定位|
| getDiffAge| 差分延时 |单位：秒|
|getLatitude| 纬度 |单位：度|
| getLongitude|经度  |单位：度|
| getAltitude|海拔高 |单位：米|
| getAltitudeCorr|高程异常值 |大地高=海拔高+高程异常值|
| getnSatsInView| 卫星数 |
| getnSatsInUse| 参与解算的卫星数 |
| getTime| 时间戳 |
| getSpeed| 速度 |单位：米/秒|
| getBearing| 速度方向 |
| getAccuracy| 定位水平精度|单位：米|
| getmVerticalAccuracy| 定位垂直精度|
| gethDOP| hdop|
| getvDOP| vdop|
| getpDOP| pdop|

##### SatelliteInfo
| 方法名 | 说明| 备注|
| :--| :-- | :-- |
| getType| 卫星类型|
| getNSigs| 编号|
| isUsed| 是否参与解算|
| getCn0| 信噪比|
| getElev| elev|
| getAzi| azi|
| getPrn| prn|
