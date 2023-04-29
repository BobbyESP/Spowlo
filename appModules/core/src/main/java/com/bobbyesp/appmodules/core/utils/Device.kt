package com.bobbyesp.appmodules.core.utils

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService

object Device {
  fun getInternalStorageSize(context: Context): StorageSize = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    context.getSystemService<StorageStatsManager>()?.let { modernImpl(it) } ?: legacyImpl()
  } else {
    legacyImpl()
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun modernImpl(manager: StorageStatsManager) = StorageSize(manager.getFreeBytes(StorageManager.UUID_DEFAULT) to manager.getTotalBytes(StorageManager.UUID_DEFAULT))

  private fun statFs() = StatFs(Environment.getDataDirectory().path)
  private fun legacyImpl() = statFs().let { fs -> StorageSize(fs.availableBlocksLong * fs.blockSizeLong to fs.blockCountLong * fs.blockSizeLong) }

  @JvmInline
  value class StorageSize(private val src: Pair<Long, Long>) {
    val free get() = src.first
    val total get() = src.second
    val taken get() = total - free
  }
}