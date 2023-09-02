package com.bobbyesp.spotdl_android

import android.content.Context
import android.util.Log
import com.bobbyesp.library.SpotDLUpdater
import com.bobbyesp.spotdl_android.LibNames.androidLibName
import com.bobbyesp.spotdl_android.LibNames.spotdlBinaryName
import com.bobbyesp.spotdl_android.LibNames.spotdlInternalDirectoryName
import com.bobbyesp.spotdl_android.data.CanceledException
import com.bobbyesp.spotdl_android.data.SpotDLException
import com.bobbyesp.spotdl_android.data.SpotDLRequest
import com.bobbyesp.spotdl_android.data.SpotDLResponse
import com.bobbyesp.spotdl_android.data.streams.StreamDataProcessExtractor
import com.bobbyesp.spotdl_android.data.streams.StreamGobbler
import com.bobbyesp.spotdl_utilities.FileUtils
import com.bobbyesp.spotdl_utilities.FileUtils.copyRawResourceToFile
import com.bobbyesp.spotdl_utilities.ZipUtils
import com.bobbyesp.spotdl_utilities.preferences.PYTHON_VERSION
import com.bobbyesp.spotdl_utilities.preferences.PreferencesUtil.getString
import com.bobbyesp.spotdl_utilities.preferences.PreferencesUtil.updateString
import java.io.File
import java.io.IOException
import java.util.Collections

/**
 * The main class of the library.
 */
object SpotDL {
    private const val TAG = "SpotDL"

    private lateinit var binariesDirectory: File
    private lateinit var pythonPath: File
    private lateinit var ffmpegPath: File
    private lateinit var spotDLPath: File

    private lateinit var pythonDirectory: File
    private lateinit var ffmpegDirectory: File

    private lateinit var appPath: File

    //START: MAIN ENVIRONMENT VARIABLES
    private lateinit var ENV_LD_LIBRARY_PATH: String
    private lateinit var ENV_SSL_CERT_FILE: String
    private lateinit var ENV_PYTHONHOME: String
    private lateinit var HOME: String
    //END: MAIN ENVIRONMENT VARIABLES

    private var isInitialized = false
    private val isDebug = BuildConfig.DEBUG

    private val idProcessMap = Collections.synchronizedMap(HashMap<String, Process>())

    /**
     * Initialize the library.
     *
     * @param applicationContext The application context.
     */
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

        spotDLPath = File(androidLibBaseDir, spotdlBinaryName)

        pythonDirectory = File(packagesDirectory, LibNames.pythonInternalDirectoryName)
        ffmpegDirectory = File(packagesDirectory, LibNames.ffmpegInternalDirectoryName)

        appPath = File(applicationContext.filesDir, spotdlInternalDirectoryName)

        setupEnvironmentVariables()

        if (isDebug) printAllDirectories()

        initializePython(pythonDirectory)
        initializeSpotDL(applicationContext, androidLibBaseDir)

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

        if (!pythonDirectory.exists() || shouldUpdatePython(librarySize)) {
            FileUtils.deleteFileSilently(pythonDirectory)
            pythonDirectory.mkdirs()
            try {
                ZipUtils.decompressFile(pythonLibPath, pythonDirectory)
            } catch (e: Exception) {
                FileUtils.deleteFileSilently(pythonDirectory)
                throw Exception("Error while decompressing Python library: ${e.message} \nInitialization of the Python interpreter failed.")
            }
            updatePython(librarySize)
            Log.i(TAG, "Python library extracted successfully.")
        }
    }


    @Throws(SpotDLException::class)
    fun initializeSpotDL(appContext: Context, spotDLdirectory: File) {
        if (!spotDLdirectory.exists()) spotDLdirectory.mkdirs()

        val spotDlBinary = File(spotDLdirectory, spotdlBinaryName)

        if (!spotDlBinary.exists()) {
            try {
                //See https://github.com/containerd/containerd/blob/269548fa27e0089a8b8278fc4fc781d7f65a939b/platforms/platforms.go#L88
                //Also https://www.digitalocean.com/community/tutorials/building-go-applications-for-different-operating-systems-and-architectures
                val binaryFileId = R.raw.spotdl
                val outputFile = File(spotDlBinary.absolutePath)
                copyRawResourceToFile(appContext, binaryFileId, outputFile)
            } catch (e: Exception) {
                FileUtils.deleteFileSilently(spotDLdirectory)
                throw SpotDLException("Error extracting spotdl files", e)
            }
        }
    }

    /**
     * Setup the environment variables that will be used by the Python interpreter.
     */
    private fun setupEnvironmentVariables() {
        ENV_LD_LIBRARY_PATH =
            pythonDirectory.absolutePath + "/usr/lib" + ":" + ffmpegDirectory.absolutePath + "/usr/lib"
        ENV_SSL_CERT_FILE = pythonDirectory.absolutePath + LibNames.certificatePath
        ENV_PYTHONHOME = pythonDirectory.absolutePath + "/usr"
        HOME = appPath.absolutePath
    }

    /**
     * Print all the directories and paths used by the library.
     */
    private fun printAllDirectories() {
        Log.d(TAG, "------------ DIRECTORIES ------------")
        Log.d(TAG, "BINARIES DIRECTORY: ${binariesDirectory.absolutePath}")
        Log.d(TAG, "PYTHON DIRECTORY: ${pythonDirectory.absolutePath}")
        Log.d(TAG, "FFMPEG DIRECTORY: ${ffmpegDirectory.absolutePath}")

        Log.d(TAG, "------------ PATHS ------------")
        Log.d(TAG, "PYTHON PATH: ${pythonPath.absolutePath}")
        Log.d(TAG, "FFMPEG PATH: ${ffmpegPath.absolutePath}")
    }

    @JvmOverloads
    @Throws(SpotDLException::class, InterruptedException::class, CanceledException::class)
    fun execute(
        request: SpotDLRequest,
        processId: String? = null,
        callback: ((Float, Long, String) -> Unit)? = null
    ): SpotDLResponse {
        assertInit()

        //Check if the process ID already exists or not.
        if (processId != null && idProcessMap.containsKey(processId)) throw SpotDLException("Process ID already exists! Change the process ID and retry.")

        // disable caching unless it is explicitly requested
        if (!request.hasOption("--cache-path") || request.getOption("--cache-path") == null) {
            request.addOption("--no-cache")
        }

        request.addOption("--ffmpeg", ffmpegPath.absolutePath)

        val spotDlResponse: SpotDLResponse
        val process: Process
        val exitCode: Int
        val outBuffer = StringBuffer() //stdout
        val errBuffer = StringBuffer() //stderr
        val startTime = System.currentTimeMillis()
        val args = request.buildCommand()
        val command: MutableList<String?> = ArrayList()

        if (isDebug) {
            //print environment variables
            Log.i(TAG, "------------ ENVIRONMENT VARIABLES ------------")
            Log.i(TAG, "LD_LIBRARY_PATH: $ENV_LD_LIBRARY_PATH")
            Log.i(TAG, "SSL_CERT_FILE: $ENV_SSL_CERT_FILE")
            Log.i(TAG, "PYTHONHOME: $ENV_PYTHONHOME")
            Log.i(TAG, "Path: ${System.getenv("PATH")!! + ":" + binariesDirectory.absolutePath}")
            Log.i(TAG, "HOME: $HOME")
        }

        command.addAll(listOf(pythonPath.absolutePath, spotDLPath.absolutePath))
        command.addAll(args)

        if (isDebug) {
            Log.i(TAG, "Running the process: $command")
        }

        val processBuilder = ProcessBuilder(command)
        processBuilder.putEnvironmentVariables()

        process = try {
            processBuilder.start()
        } catch (e: IOException) {
            throw SpotDLException(e)
        }

        Log.d("SpotDL", "Started process: $process; the process ID is: $processId")

        if (processId != null) {
            idProcessMap[processId] = process
            Log.d("SpotDL", "Added process to the process map: $processId")
        }

        val outStream = process.inputStream
        val errStream = process.errorStream

        if (isDebug){
            Log.d("SpotDL", "Out stream: $outStream")
            Log.d("SpotDL", "Err stream: $errStream")
        }

        val stdOutProcessor = StreamDataProcessExtractor(
            outBuffer,
            outStream,
            callback
        )
        val stdErrProcessor = StreamGobbler(errBuffer, errStream)
        exitCode = try {
            stdOutProcessor.join()
            stdErrProcessor.join()
            process.waitFor()
        } catch (e: InterruptedException) {
            process.destroy()
            if (processId != null) idProcessMap.remove(processId)
            throw e
        }
        val out = outBuffer.toString()
        val err = errBuffer.toString()

        if (exitCode > 0) {
            if (processId != null && !idProcessMap.containsKey(processId))
                throw CanceledException()
            if (!ignoreErrors(request, out)) {
                idProcessMap.remove(processId)
                throw SpotDLException(err)
            }
        }
        idProcessMap.remove(processId)

        val elapsedTime = System.currentTimeMillis() - startTime
        spotDlResponse = SpotDLResponse(command, exitCode, elapsedTime, out, err)
        return spotDlResponse
    }

    /**
     * Execute a Python command.
     *
     * @param request The command to execute.
     * @param processId The ID of the process.
     * @param callback The callback to be called when the process is running.
     *
     * @return The response of the process.
     */
    @JvmOverloads
    @Throws(SpotDLException::class, InterruptedException::class, CanceledException::class)
    fun executePythonCommand(
        request: SpotDLRequest,
        processId: String? = null,
        callback: ((Float, Long, String) -> Unit)? = null
    ): SpotDLResponse {
        assertInit()
        if (isDebug) {
            Log.i(TAG, "Starting process with command: $request")
        }
        if (processId != null && idProcessMap.containsKey(processId)) throw SpotDLException("Process ID already exists")

        val response: SpotDLResponse
        val process: Process
        val exitCode: Int
        val outBuffer = StringBuffer() //stdout
        val errBuffer = StringBuffer() //stderr
        val startTime = System.currentTimeMillis()
        val args = request.buildCommand()
        val command: MutableList<String?> = ArrayList()

        command.add(pythonPath.absolutePath)
        command.addAll(args)

        val processBuilder = ProcessBuilder(command)

        processBuilder.putEnvironmentVariables()

        if (isDebug) {
            Log.i(TAG, "Running the process: $processBuilder")
        }

        process = try {
            processBuilder.start()
        } catch (e: IOException) {
            throw SpotDLException(e)
        }
        if (processId != null) {
            idProcessMap[processId] = process
        }
        val outStream = process.inputStream
        val errStream = process.errorStream
        val stdOutProcessor = StreamDataProcessExtractor(outBuffer, outStream, callback)
        val stdErrProcessor = StreamGobbler(errBuffer, errStream)
        exitCode = try {
            stdOutProcessor.join()
            stdErrProcessor.join()
            process.waitFor()
        } catch (e: InterruptedException) {
            process.destroy()
            if (processId != null) idProcessMap.remove(processId)
            throw e
        }
        val out = outBuffer.toString()
        val err = errBuffer.toString()
        if (exitCode > 0) {
            if (processId != null && !idProcessMap.containsKey(processId))
                throw CanceledException()
        }
        idProcessMap.remove(processId)

        val elapsedTime = System.currentTimeMillis() - startTime
        response = SpotDLResponse(command, exitCode, elapsedTime, out, err)
        if (isDebug) {
            Log.i(TAG, "STDOUT: $out")
            Log.e(TAG, "STDERR: $err")
            Log.i(TAG, "Process finished with exit code: $exitCode")
        }
        return response
    }

    @Throws(SpotDLException::class)
    suspend fun updateSpotDL(appContext: Context): SpotDLUpdater.Companion.UpdateStatus? {
        assertInit()
        return try {
            SpotDLUpdater.getInstance().update(appContext)
        } catch (e: IOException) {
            throw SpotDLException("Failed to update the spotDL library.", e)
        }
    }

    private fun ignoreErrors(request: SpotDLRequest, out: String): Boolean {
        return out.isNotEmpty() && !request.hasOption("--print-errors")
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

    private fun assertInit() {
        check(isInitialized) { "Python instance is not initialized" }
    }
    fun ProcessBuilder.putEnvironmentVariables() {
        this.environment().apply {
            this["LD_LIBRARY_PATH"] = SpotDL.ENV_LD_LIBRARY_PATH
            this["SSL_CERT_FILE"] = SpotDL.ENV_SSL_CERT_FILE
            this["PATH"] = System.getenv("PATH")!! + ":" + SpotDL.binariesDirectory.absolutePath
            this["PYTHONHOME"] = SpotDL.ENV_PYTHONHOME
            this["HOME"] = SpotDL.HOME
            this["ffmpeg"] = SpotDL.ffmpegPath.absolutePath
            //ENVIRONMENT VARIABLES TO FORCE RICH PYTHON LIB TO SHOW THE PROGRESS LINE.
            //Thanks xnetcat (https://github.com/xnetcat) (principal spotdl library developer/maintainer) for the help and time!
            this["TERM"] = "xterm-256color"
            this["FORCE_COLOR"] = "true"
        }
    }
}

