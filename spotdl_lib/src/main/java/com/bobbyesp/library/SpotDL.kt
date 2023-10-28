package com.bobbyesp.library

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import com.bobbyesp.commonutilities.SharedPrefsHelper
import com.bobbyesp.commonutilities.utils.ZipUtilities
import com.bobbyesp.library.dto.Song
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

open class SpotDL {

    /*
    * INFO: Python tries to run a binary file that is standalone and does not require any type of python installation or dependencies.
    * The thing is that the binary file is not compatible with all the android devices (or it's kinda hard). So, we need to run the python file instead.
    * We should take the source code and run it directly here. The problem, the dependencies. We need to install them first. So, we need to run the libraries installation python file first.
    * Then, we can run the main python file and run the library.
    * */

    /*
    lib.so.6: https://www.golinuxcloud.com/how-do-i-install-the-linux-library-libc-so-6/
    Because: ImportError: dlopen failed: library "libc.so.6" not found: needed by /data/data/com.bobbyesp.spotdl_android/no_backup/spotdl_android/packages/python/usr/lib/python3.8/site-packages/pydantic/__init__.cpython-38.so in namespace (default)
    */


    val baseName = "spotdl_android"

    val spotdlDirName = "spotdl"
    private val spotdlBin = "spotdl"

    private val packagesRoot = "packages"

    private val pythonBinName = "libpython.bin.so"
    private val pythonLibName = "libpython.zip.so"
    private val pythonDirName = "python"
    private val pythonLibVersion = "pythonLibVersion"

    private val ffmpegDirName = "ffmpeg"
    private val ffmpegBinName = "libffmpeg.so"

    private var initialized: Boolean = false

    private var pythonPath: File? = null
    private var ffmpegPath: File? = null
    private var spotdlPath: File? = null
    private var binDir: File? = null

    private var ENV_LD_LIBRARY_PATH: String? = null
    private var ENV_SSL_CERT_FILE: String? = null
    private var ENV_PYTHONHOME: String? = null
    private var HOME: String? = null
    private var LDFLAGS: String? = null

    private val ansiCleaner = Regex("(\\x1B[@-Z\\\\-_]|[\\x80-\\x9A\\x9C-\\x9F]|(?:\\x1B\\[|\\x9B)[0-?]*[ -/]*[@-~])")

    private val isDebug = BuildConfig.DEBUG

    private val idProcessMap = Collections.synchronizedMap(HashMap<String, Process>())

    //ignore jsonUnknownKeys
    private val json = Json {
        ignoreUnknownKeys = true
    }

    //create a function that can be called out of this class to get the instance
    companion object {
        private val spotDl: SpotDL = SpotDL()

        fun getInstance(): SpotDL {
            return spotDl
        }
    }

    @SuppressLint("SdCardPath") //because SpotDL was thought to be used only in Termux
    @Synchronized
    @Throws(SpotDLException::class)
    open fun init(appContext: Context) {
        if (initialized) {
            return
        }

        val termuxSpotDlPath = File("/data/data/com.termux/files/home/.spotdl")
        if (!termuxSpotDlPath.exists()) {
            termuxSpotDlPath.mkdirs()
        }

        val baseDir = File(appContext.noBackupFilesDir, baseName)
        Log.d("SpotDL", "Base dir: $baseDir")
        if (!baseDir.exists()) baseDir.mkdir()

        val packagesDir = File(baseDir, packagesRoot)

        //Setup the files directories to be used
        binDir = File(appContext.applicationInfo.nativeLibraryDir)

        if (isDebug) Log.d("SpotDL", "Bin dir: $binDir")

        pythonPath = File(binDir, pythonBinName)

        if (isDebug) Log.d("SpotDL", "Python path: $pythonPath")

        ffmpegPath = File(binDir, ffmpegBinName)

        if (isDebug) Log.d("SpotDL", "FFMPEG path: $ffmpegPath")

        val pythonDir = File(packagesDir, pythonDirName)
        val ffmpegDir = File(packagesDir, ffmpegDirName)

        val spotdlDir = File(baseDir, spotdlDirName)
        spotdlPath = File(spotdlDir, spotdlBin)

        val appPath = File(appContext.filesDir, "spotdl")

        ENV_LD_LIBRARY_PATH =
            pythonDir.absolutePath + "/usr/lib" + ":" + ffmpegDir.absolutePath + "/usr/lib"
        ENV_SSL_CERT_FILE = pythonDir.absolutePath + "/usr/etc/tls/cert.pem"
        ENV_PYTHONHOME = pythonDir.absolutePath + "/usr"
        HOME = appPath.absolutePath
        LDFLAGS = "-rdynamic"

        Log.i("SpotDL", "HOME: $HOME")
        Log.i("SpotDL", "ffmpegDir: ${ffmpegDir.absolutePath}")

        //Initialize the python and spotdl files
        try {
            if (HOME != null) {
                val homeDir = File(HOME)
                if (!homeDir.exists()) {
                    homeDir.mkdirs()
                }
            }
            initPython(appContext, pythonDir)
            initSpotDL(appContext, spotdlDir)

        } catch (e: Exception) {
            throw SpotDLException("Error initializing python and spotdl", e)
        }

        initialized = true
    }
    @Throws(SpotDLException::class)
    private fun initPython(appContext: Context, pythonDir: File) {

        val pythonLib = File(binDir, pythonLibName)

        // using size of lib as version number, so when the lib is updated, the python will be updated too
        val pythonSize = pythonLib.length().toString()

        if (!pythonDir.exists() || shouldUpdatePython(appContext, pythonSize)) {

            //We delete the python directory to ensure that we have a clean install
            FileUtils.deleteQuietly(pythonDir)

            //We make another directory to put in it Python
            pythonDir.mkdirs()

            //And now we try to extract the python files
            try {
                ZipUtilities.unzip(pythonLib, pythonDir)
            } catch (e: Exception) {
                FileUtils.deleteQuietly(pythonDir)
                throw SpotDLException("Error extracting python files", e)
            }
            //We check if there's a new python version
            updatePython(appContext, pythonSize)
        }

    }

    private fun updatePython(appContext: Context, version: String) {
        SharedPrefsHelper.update(
            appContext,
            pythonLibVersion,
            version
        )
    }

    private fun shouldUpdatePython(appContext: Context, version: String): Boolean {
        return version != SharedPrefsHelper[appContext, pythonLibVersion]
    }

    @Throws(SpotDLException::class)
    fun initSpotDL(appContext: Context, spotDlDir: File) {
        if (!spotDlDir.exists()) spotDlDir.mkdirs()

        val spotDlBinary = File(spotDlDir, spotdlBin)

        if (!spotDlBinary.exists()) {
            try {
                //See https://github.com/containerd/containerd/blob/269548fa27e0089a8b8278fc4fc781d7f65a939b/platforms/platforms.go#L88
                //Also https://www.digitalocean.com/community/tutorials/building-go-applications-for-different-operating-systems-and-architectures
                val binaryFileId = R.raw.spotdl
                val outputFile = File(spotDlBinary.absolutePath)
                copyRawResourceToFile(appContext, binaryFileId, outputFile)
            } catch (e: Exception) {
                FileUtils.deleteQuietly(spotDlDir)
                throw SpotDLException("Error extracting spotdl files", e)
            }
        }
    }

    private fun copyRawResourceToFile(context: Context, resourceId: Int, file: File) {
        val inputStream = context.resources.openRawResource(resourceId)
        val outputStream = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var read = inputStream.read(buffer)
        while (read != -1) {
            outputStream.write(buffer, 0, read)
            read = inputStream.read(buffer)
        }
        outputStream.close()
        inputStream.close()
    }

    fun destroyProcessById(id: String, forceProcessDestroy: Boolean = false): Boolean {
        // Logging for debugging purposes
        Log.d("SpotDL", "Destroying process $id")
        Log.d("SpotDL", "--------------------------------------")
        Log.d("SpotDL", "idProcessMap: $idProcessMap")
        Log.d("SpotDL", "--------------------------------------")
        Log.d("SpotDL", "Does the map contain the id? ${idProcessMap.containsKey(id)}")

        val p = idProcessMap[id]
        if (p != null) {
            var alive = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                alive = p.isAlive
            }
            if (alive) {
                try {
                    if (forceProcessDestroy && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        p.destroyForcibly()
                    } else {
                        p.destroy()
                    }
                    idProcessMap.remove(id)
                    return true
                } catch (e: Exception) {
                    // Handle any exceptions that might occur during process destruction
                    Log.e("SpotDL", "Failed to destroy process $id: ${e.message}")
                }
            }
        }
        return false
    }

    @Throws(SpotDLException::class)
    open suspend fun updateSpotDL(appContext: Context, apiUrl: String? = null): UpdateStatus? {
        assertInit()
        return try {
            SpotDLUpdater.getInstance().update(appContext)
        } catch (e: IOException) {
            throw SpotDLException("Failed to update the spotDL library.", e)
        }
    }

    open fun version(appContext: Context?): String? {
        return SpotDLUpdater.getInstance().version(appContext!!)
    }

    class CanceledException : Exception()

    @JvmOverloads
    @Throws(SpotDLException::class, InterruptedException::class, CanceledException::class)
    fun execute(
        request: SpotDLRequest,
        processId: String? = null,
        callback: ((Float, Long, String) -> Unit)? = null,
        forceProcessDestroy : Boolean = false
    ): SpotDLResponse {
        assertInit()
        //Check if the process ID already exists or not.
        if (processId != null && idProcessMap.containsKey(processId)) throw SpotDLException("Process ID already exists! Change the process ID and retry.")

        request.addOption("--ffmpeg", ffmpegPath!!.absolutePath)

        val spotDLResponse: SpotDLResponse
        val process: Process
        val exitCode: Int

        val outBuffer = StringBuffer() //stdout

        val errBuffer = StringBuffer() //stderr

        val startTime = System.currentTimeMillis()

        val args = request.buildCommand()

        //Full command
        val command = mutableListOf<String>()
        command.addAll(listOf(pythonPath!!.absolutePath, spotdlPath!!.absolutePath))
        command.addAll(args)

        val processBuilder = ProcessBuilder(command)
        val env = processBuilder.environment()
        env["LD_LIBRARY_PATH"] = ENV_LD_LIBRARY_PATH!!
        env["SSL_CERT_FILE"] = ENV_SSL_CERT_FILE!!
        env["PATH"] =
            System.getenv("PATH")!! + ":" + binDir!!.absolutePath + ":" + ffmpegPath!!.absolutePath
        env["PYTHONHOME"] = ENV_PYTHONHOME!!
        env["HOME"] = HOME!!
        env["ffmpeg"] = ffmpegPath!!.absolutePath
        //ENVIRONMENT VARIABLES TO FORCE RICH PYTHON LIB TO SHOW THE PROGRESS LINE.
        //Thanks xnetcat (https://github.com/xnetcat) (principal spotdl library developer/maintainer) for the help and time!
        env["TERM"] = "xterm-256color"
        env["FORCE_COLOR"] = "true"

        process = try {
            processBuilder.start()
        } catch (e: IOException) {
            throw SpotDLException("Error starting process", e)
        }

        Log.d("SpotDL", "Started process: $process; the process ID is: $processId")

        if (processId != null) {
            idProcessMap[processId] = process
            Log.d("SpotDL", "Added process to the process map: $processId")
        }

        val outStream: InputStream = process.inputStream
        val errStream: InputStream = process.errorStream
        if (isDebug) Log.d("SpotDL", "Out stream: $outStream")
        if (isDebug) Log.d("SpotDL", "Err stream: $errStream")

        val stdOutProcessor = StreamProcessExtractor(
            outBuffer,
            outStream,
            callback
        )

        val stdErrProcessor = StreamGobbler(
            errBuffer,
            errStream
        )

        exitCode = try {
            stdOutProcessor.join()
            stdErrProcessor.join()
            process.waitFor()
        } catch (e: InterruptedException) {
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && forceProcessDestroy) {
                process.destroyForcibly()
            }
            process.destroy()
            if (processId != null) idProcessMap.remove(processId)
            throw e
        }

        val out = outBuffer.toString()
        val err = errBuffer.toString()

        //Delete ANSI (cleaner output)
        val outClean = out.replace(
            regex = ansiCleaner,
            replacement = ""
        )
        //Cleaner output
        val errClean = err.replace(
            regex = ansiCleaner,
            replacement = ""
        )

        if (exitCode > 0) {
            if (processId != null && !idProcessMap.containsKey(processId))
                throw CanceledException()
            if(!command.contains("--print-errors")) {
                throw SpotDLException("Error executing command: $command, exit code: $exitCode, stderr: $errClean \n\n stdout: $outClean")
            }
            if (!ignoreErrors(request, out)) {
                idProcessMap.remove(processId)
                throw SpotDLException(err)
            }
        }

        try {
            idProcessMap.remove(processId)
            Log.d("SpotDL", "Removed process from the process map: $processId")
        } catch (e: Exception) {
            Log.e("SpotDL", "Error removing process from the process map: $processId")
        }

        val elapsedTime = System.currentTimeMillis() - startTime

        spotDLResponse = SpotDLResponse(command, exitCode, elapsedTime, outClean, errClean)

        if (BuildConfig.DEBUG) {
            Log.d("SpotDL", "Stdout: $outClean")
            Log.e("SpotDL", "Stderr: $errClean")
            Log.d(
                "SpotDL",
                "------------------------------------------------------------------------------"
            )
            Log.d("SpotDL", "Process: $processId finished with exit code: $exitCode")
            Log.d("SpotDL", "Process: $spotDLResponse")
        }

        return spotDLResponse
    }

    private fun ignoreErrors(request: SpotDLRequest, out: String): Boolean {
        return out.isNotEmpty() && !request.hasOption("--print-errors")
    }

    @Throws(SpotDLException::class, InterruptedException::class, CanceledException::class)
    fun getSongInfo(url: String, songId: String = UUID.randomUUID().toString()): List<Song> {
        assertInit()
        //Make sure that the path exists
        val metadataDirectory = File("$HOME/.spotdl/meta_info/")

        if (!metadataDirectory.exists()) {
            metadataDirectory.mkdirs()
        }

        //UUID for song identification
        val request = SpotDLRequest()
        request.addOption("save", url)
        request.addOption("--save-file", "$HOME/.spotdl/meta_info/$songId.spotdl")
        execute(request, null, null)

        val songInfo: List<Song>?
        try {
            //get the song info from the file with the songId and deserialize it
            val file = File("$HOME/.spotdl/meta_info/$songId.spotdl")
            val builder = StringBuilder()

            file.forEachLine { builder.append(it) }
            songInfo = json.decodeFromString(
                ListSerializer(Song.serializer()),
                builder.toString()
            )
        } catch (e: Exception) {
            throw SpotDLException("Error parsing song info", e)
        }

        return songInfo
    }

    @Throws(SpotDLException::class)
    private fun assertInit() {
        check(initialized) { "The SpotDL instance is not initialized" }
    }

    enum class UpdateStatus {
        DONE, ALREADY_UP_TO_DATE
    }
}