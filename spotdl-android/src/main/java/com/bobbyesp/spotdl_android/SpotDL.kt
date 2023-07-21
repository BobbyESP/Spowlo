package com.bobbyesp.spotdl_android

import android.content.Context
import android.util.Log
import com.bobbyesp.spotdl_android.LibNames.androidLibName
import com.bobbyesp.spotdl_android.data.CanceledException
import com.bobbyesp.spotdl_android.data.SpotDLException
import com.bobbyesp.spotdl_android.data.streams.StreamDataProcessExtractor
import com.bobbyesp.spotdl_android.data.streams.StreamGobbler
import com.bobbyesp.spotdl_utilities.FileUtils
import com.bobbyesp.spotdl_utilities.ZipUtils
import com.bobbyesp.spotdl_utilities.preferences.PYTHON_VERSION
import com.bobbyesp.spotdl_utilities.preferences.PreferencesUtil
import com.bobbyesp.spotdl_utilities.preferences.PreferencesUtil.getString
import com.bobbyesp.spotdl_utilities.preferences.PreferencesUtil.updateString
import java.io.File

/**
 * The main class of the library.
 */
object SpotDL {
    private const val TAG = "SpotDL"

    private lateinit var binariesDirectory: File
    private lateinit var pythonPath: File
    private lateinit var ffmpegPath: File

    private lateinit var pythonDirectory: File
    private lateinit var ffmpegDirectory: File

    private lateinit var pipZipDirectory: File
    private lateinit var pipZip: File  // Initialization with null value

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

    fun init(applicationContext: Context) {
        if (isInitialized) return

        val androidLibBaseDir = File(applicationContext.noBackupFilesDir, androidLibName)

        pipZipDirectory = File(androidLibBaseDir, LibNames.pipDirName)
        pipZip = File(pipZipDirectory, LibNames.pipZipName)

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

        initializePython(applicationContext, pythonDirectory)

        isInitialized = true
    }

    /**
     * Initialize the Python interpreter.
     *
     * @param pythonDirectory The directory where the Python interpreter is located.
     */
    private fun initializePython(context: Context, pythonDirectory: File) {
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
            Log.i(TAG, "Python library extracted successfully.")
        }
        try {
            setupPip(context)
        } catch (e: Exception) {
            FileUtils.deleteFileSilently(pipZipDirectory)
            throw Exception("Error while decompressing pip library: ${e.message} \nInitialization of pip failed.")
        }
    }

    @Throws(SpotDLException::class)
    fun setupPip(context: Context): Boolean {
        if(!pipZipDirectory.exists()) {
            pipZipDirectory.mkdirs()
            Log.i(TAG, "Pip directory created successfully.")
        }

        if(!pipZip.exists()) {
            return try {
                val zipFileId = R.raw.pip
                val outputFile = pipZip.absoluteFile
                FileUtils.copyInputStreamToFile(context.resources.openRawResource(zipFileId), outputFile)
                Log.i(TAG, "Pip library extracted successfully.")
                true
            } catch (e: Exception) {
                FileUtils.deleteFileSilently(pipZipDirectory)
                throw SpotDLException("Error while decompressing pip library: ${e.message} \nInitialization of the pip failed.")
            }
        }
        return true
    }

    @Throws(
        Exception::class,
        SpotDLException::class,
        InterruptedException::class,
        CanceledException::class
    )
    private fun installSpotDL(
    ): Boolean {
        val process: Process

        val exitCode: Int

        //STDERR and STDOUT
        val outStrBuffer = StringBuffer() //stdout
        val errStrBuffer = StringBuffer() //stderr

        //Time on start
        val startTime = System.currentTimeMillis()

        /*****************************************/

        //Command to execute
        val command = mutableListOf<String>()
        command.addAll(
            listOf(
                pythonPath.absolutePath,
                "--version"
            )
        )

        val processBuilder = ProcessBuilder(command)

        processBuilder.environment().apply {
            put("LD_LIBRARY_PATH", ENV_LD_LIBRARY_PATH)
            put("SSL_CERT_FILE", ENV_SSL_CERT_FILE)
            put("PYTHONHOME", ENV_PYTHONHOME)
            put("HOME", ENV_PYTHONHOME)
            put("PATH", System.getenv("PATH")!! + ":" + pythonPath.absolutePath)
        }

        /**
         * Start the process.
         */
        process = try {
            Log.i(TAG, "Running the process: $processBuilder")
            processBuilder.start()
        } catch (e: Exception) {
             throw Exception("Error while running the process: ${e.message}")
        }

        /**
         * Get the outputs of the process.
         */
        val outStream = process.inputStream //stdout
        val errStream = process.errorStream //stderr

        val stdOutProcessor =
            StreamDataProcessExtractor(outStrBuffer, outStream) { progress, eta, line ->
                if (isDebug) Log.i(TAG, "PROGRESS: $progress, ETA: $eta, LINE: $line")
            }
        val stdErrProcessor = StreamGobbler(errStrBuffer, errStream)

        exitCode = try {
            stdOutProcessor.start()
            stdErrProcessor.start()
            process.waitFor()
            Log.i(TAG, "Process finished with exit code: ${process.exitValue()}")
        } catch (e: InterruptedException) {
            process.destroy()
            throw e
        } catch (e: Exception) {
            throw Exception("Error while waiting for the process: ${e.message}")
        }

        val out = outStrBuffer.toString()
        val err = errStrBuffer.toString()

        Log.i(TAG, "STDOUT: $out")
        Log.i(TAG, "STDERR: $err")

        if (exitCode != 0) {
            Log.e(TAG, err)
            throw SpotDLException("Error while installing SpotDL: $err")
        }

        return true
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
        if (isDebug) Log.i(TAG, "FILES: $files")
        return files
    }

    /**
     * Setup the environment variables that will be used by the Python interpreter.
     */
    private fun setupEnvironmentVariables() {
        ENV_LD_LIBRARY_PATH =
            pythonDirectory.absolutePath + "/usr/lib" + ":" + ffmpegDirectory.absolutePath + "/usr/lib"
        ENV_SSL_CERT_FILE = pythonDirectory.absolutePath + LibNames.certificatePath
        ENV_PYTHONHOME = pythonDirectory.absolutePath + "/usr"
    }

    /**
     * Print all the directories and paths used by the library.
     */
    private fun printAllDirectories() {
        Log.i(TAG, "------------ DIRECTORIES ------------")
        Log.i(TAG, "BINARIES DIRECTORY: ${binariesDirectory.absolutePath}")
        Log.i(TAG, "PYTHON DIRECTORY: ${pythonDirectory.absolutePath}")
        Log.i(TAG, "FFMPEG DIRECTORY: ${ffmpegDirectory.absolutePath}")

        Log.i(TAG, "------------ PATHS ------------")
        Log.i(TAG, "PYTHON PATH: ${pythonPath.absolutePath}")
        Log.i(TAG, "FFMPEG PATH: ${ffmpegPath.absolutePath}")

        Log.i(TAG, "------------ PIP ------------")
        Log.i(TAG, "PIP DIRECTORY: ${pipZipDirectory.absolutePath}")
        Log.i(TAG, "PIP PATH: ${pipZip.absolutePath}")
    }

    /**
     * Execute a Python command.
     *
     * @param command The command to execute.
     */
    suspend fun executePythonCommand(command: String){
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