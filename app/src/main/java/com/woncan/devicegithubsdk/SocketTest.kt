package com.woncan.devicegithubsdk

import android.util.Base64
import android.util.Log
import java.net.Socket
import java.nio.charset.StandardCharsets

object SocketTest {



		fun startJob(){


				fun getMountPoint(logBlock:(String)->Unit,block:(Array<String>)->Unit) {
						val ip = "210.241.63.193"
						val port = 81
//				 				val ip = "117.135.142.201"
//				val port = 8001
						var account =
								Base64.encodeToString(("jacky:jacky").toByteArray() ,
								                      Base64.NO_WRAP)
						object : Thread() {
								override fun run() {
										super.run()
										try {
												val socket = Socket(ip , port)
												socket.soTimeout = 10000
												if (socket.isConnected) {
														val outputStream = socket.getOutputStream()
														val inputStream = socket.getInputStream()
														if (outputStream == null || inputStream == null) {
																return
														}
														val params =
																"GET / HTTP/1.0\r\nUser-Agent: NTRIP GNSSInternetRadio/1.4.11\r\nAccept: */*\r\nConnection: close\r\n\r\n\r\n"
														outputStream.write("""$params
    """.toByteArray(StandardCharsets.UTF_8))
														outputStream.flush()
														var isEnd = false
														val source = StringBuilder()
														while (! isEnd) {
																var count : Int
																do {
																		count = inputStream.available()
																} while (count == 0)
																val data = ByteArray(count)
																val length = inputStream.read(data , 0 , count)
																if (length != - 1) {
																		val string = String(data)
																		logBlock.invoke(string)
																		source.append(string)
																		if (! source.toString().startsWith("SOURCETABLE")) {
																				isEnd = true
																		}
																		if (source.toString().contains("ENDSOURCETABLE")) {
																				isEnd = true
																		}
																}
																Log.i("TAG" , "run: " + source.toString().trim { it <= ' ' })
														}
														val result = source.toString()
														if (result.contains("SOURCETABLE 200 OK")) {
																val pointList : MutableList<String> = ArrayList()
																val split = result.split("\r\n").toTypedArray()
																for (s in split) {
																		if (s.startsWith("STR;")) {
																				val split1 = s.split(";").toTypedArray()
																				pointList.add(split1[1])
																		}
																}

																if (pointList.isNotEmpty()) {
																		block.invoke(pointList.toTypedArray())
																}
														}
												}
												socket.close()
										} catch (e : Exception) {
												e.message?.let {
														logBlock.invoke(it)
												}
												e.printStackTrace()
										}
								}
						}.start()
				}








		}






}