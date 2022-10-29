package com.bobbyesp.spowlo.util

import android.os.Build
import java.util.*

object CPUInfoUtil {

    fun getPrincipalCPUArch(): String{
        return Build.SUPPORTED_ABIS[0].toString()
    }

    fun getOSArch(): String{
        return System.getProperty("os.arch")?.toString() ?: "Unknown"
    }

    fun get32BitSupportedArchs(): String {
        return Arrays.toString(Build.SUPPORTED_32_BIT_ABIS)
    }

    fun  get64BitSupportedArchs(): String {
        return Arrays.toString(Build.SUPPORTED_64_BIT_ABIS)
    }

    //Get cpu architecture
    fun getCPUArch(): String {
        val cpuArch = StringBuilder()
        val runtime = Runtime.getRuntime()
        val process = runtime.exec("cat /proc/cpuinfo")
        val inputStream = process.inputStream
        val bufferedReader = inputStream.bufferedReader()
        val lineList = mutableListOf<String>()
        bufferedReader.useLines { lines -> lines.forEach { lineList.add(it) } }
        for (i in lineList.indices) {
            if (lineList[i].contains("CPU architecture")) {
                cpuArch.append(lineList[i].substring(lineList[i].indexOf(":") + 1))
            }
        }
        return cpuArch.toString()
    }
}