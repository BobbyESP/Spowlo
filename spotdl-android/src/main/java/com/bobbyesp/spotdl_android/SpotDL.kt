package com.bobbyesp.spotdl_android

import android.content.Context
import android.util.Log
import com.bobbyesp.spotdl_android.LibNames.androidLibName
import com.bobbyesp.spotdl_utilities.FileUtils
import com.bobbyesp.spotdl_utilities.ZipUtils
import com.bobbyesp.spotdl_utilities.preferences.PYTHON_VERSION
import com.bobbyesp.spotdl_utilities.preferences.PreferencesUtil
import com.bobbyesp.spotdl_utilities.preferences.PreferencesUtil.getString
import com.bobbyesp.spotdl_utilities.preferences.PreferencesUtil.updateString
import kotlinx.coroutines.DelicateCoroutinesApi
import java.io.File

/**
 * The main class of the library.
 */
object SpotDL {
    const val TAG = "SpotDL"

    private lateinit var binariesDirectory: File
    private lateinit var pythonPath: File
    private lateinit var ffmpegPath: File

    private lateinit var pythonDirectory: File
    private lateinit var ffmpegDirectory: File

    //START: MAIN ENVIRONMENT VARIABLES
    private lateinit var ENV_LD_LIBRARY_PATH: String
    private lateinit var ENV_SSL_CERT_FILE: String
    private lateinit var ENV_PYTHONHOME: String
    //END: MAIN ENVIRONMENT VARIABLES

    private var isInitialized = false
    private val isDebug = BuildConfig.DEBUG

    /**
     * Initialize the library.
     *
     * @param applicationContext The application context.
     */
    @Synchronized
    fun init(applicationContext: Context) {
        if (isInitialized) return

        val androidLibBaseDir = File(applicationContext.noBackupFilesDir, androidLibName)

        if (!androidLibBaseDir.exists()) {
            androidLibBaseDir.mkdirs()
        }

        val packagesDirectory = File(androidLibBaseDir, LibNames.packagesRoot)

        binariesDirectory = File(applicationContext.applicationInfo.nativeLibraryDir)

        pythonPath = File(binariesDirectory, LibNames.pythonInterpreterName)
        ffmpegPath = File(binariesDirectory, LibNames.ffmpegBinaryName)

        pythonDirectory = File(packagesDirectory, LibNames.pythonInternalDirectoryName)
        ffmpegDirectory = File(packagesDirectory, LibNames.ffmpegInternalDirectoryName)

        setupEnvironmentVariables()

        if (isDebug) printAllDirectories()

        initializePython(pythonDirectory)

        isInitialized = true
    }

    /**
     * Initialize the Python interpreter.
     *
     * @param pythonDirectory The directory where the Python interpreter is located.
     */
    private fun initializePython(pythonDirectory: File) {
        val pythonLibPath = File(binariesDirectory, LibNames.pythonLibraryName)
        val librarySize = pythonLibPath.length().toString() //We are gonna use this a checksum

        if (!pythonDirectory.exists()) {
            FileUtils.deleteFileSilently(pythonDirectory)
            pythonDirectory.mkdirs()
            try {
                ZipUtils.decompressFile(pythonLibPath, pythonDirectory)
            } catch (e: Exception) {
                FileUtils.deleteFileSilently(pythonDirectory)
                throw Exception("Error while decompressing Python library: ${e.message} \nInitialization of the Python interpreter failed.")
            }
            PreferencesUtil.encodeString(PYTHON_VERSION, librarySize)
        }
    }

    /**
     * Get the list of files in a directory.
     *
     * @param directory The directory to search.
     * @return The list of files in the directory.
     */
    private fun getListOfFilesInDirectory(directory: File): List<File> {
        val files = mutableListOf<File>()
        directory.listFiles()?.let {
            for (file in it) {
                if (file.isDirectory) {
                    files.addAll(getListOfFilesInDirectory(file))
                } else {
                    files.add(file)
                }
            }
        }
        if(isDebug) Log.i(TAG,"FILES: $files")
        return files
    }

    /**
     * Setup the environment variables that will be used by the Python interpreter.
     */
    private fun setupEnvironmentVariables() {
        ENV_LD_LIBRARY_PATH =
            pythonDirectory.absolutePath + "/usr/lib" + ":" + ffmpegDirectory.absolutePath
        ENV_SSL_CERT_FILE = pythonDirectory.absolutePath + LibNames.certificatePath
        ENV_PYTHONHOME = pythonDirectory.absolutePath + "/usr"
    }

    /**
     * Print all the directories and paths used by the library.
     */
    private fun printAllDirectories() {
        Log.i(TAG,"------------ DIRECTORIES ------------")
        Log.i(TAG,"BINARIES DIRECTORY: ${binariesDirectory.absolutePath}")
        Log.i(TAG,"PYTHON DIRECTORY: ${pythonDirectory.absolutePath}")
        Log.i(TAG,"FFMPEG DIRECTORY: ${ffmpegDirectory.absolutePath}")

        Log.i(TAG, "------------ PATHS ------------")
        Log.i(TAG,"PYTHON PATH: ${pythonPath.absolutePath}")
        Log.i(TAG, "FFMPEG PATH: ${ffmpegPath.absolutePath}")
    }

    /**
     * Execute a Python command.
     *
     * @param command The command to execute.
     */
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun executePythonCommand() {
        TODO("Not yet implemented")
    }

    /**
     * Check if the Python interpreter needs to be updated.
     *
     * @return True if the Python interpreter needs to be updated, false otherwise.
     */
    private fun shouldUpdatePython(version: String): Boolean {
        return version != PYTHON_VERSION.getString()
    }

    /**
     * Update the version string of Python interpreter.
     *
     * @param newVersion The new version of the Python interpreter.
     */
    private fun updatePython(newVersion: String) {
        PYTHON_VERSION.updateString(newVersion)
    }


    /**
     * Get the instance of the class.
     *
     * @return The instance of the class.
     */
    @JvmStatic
    fun getInstance() = this
}