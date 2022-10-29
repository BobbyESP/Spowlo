package com.bobbyesp.spowlo.util

object CPUInfoUtil {

    //Get full CPU info
    fun getCPUInfo(): String {
        val cpuInfo = StringBuilder()
        val runtime = Runtime.getRuntime()
        val process = runtime.exec("cat /proc/cpuinfo")
        val inputStream = process.inputStream
        val bufferedReader = inputStream.bufferedReader()
        val lineList = mutableListOf<String>()
        bufferedReader.useLines { lines -> lines.forEach { lineList.add(it) } }
        for (i in lineList.indices) {
            if (lineList[i].contains("Hardware")) {
                cpuInfo.append(lineList[i].substring(lineList[i].indexOf(":") + 1))
            }
            if (lineList[i].contains("Processor")) {
                cpuInfo.append(lineList[i].substring(lineList[i].indexOf(":") + 1))
            }
            if (lineList[i].contains("model name")) {
                cpuInfo.append(lineList[i].substring(lineList[i].indexOf(":") + 1))
            }
        }
        return cpuInfo.toString()
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