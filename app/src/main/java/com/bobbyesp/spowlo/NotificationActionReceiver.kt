import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bobbyesp.library.SpotDL
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.Downloader
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.utils.NotificationsUtil
import com.bobbyesp.spowlo.utils.ToastUtil

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Early exit if context or intent is null.
        if (context == null || intent == null) {
            Log.e(TAG, "Context or Intent is null")
            return
        }

        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
        val action = intent.getIntExtra(ACTION, ActionType.CANCEL_TASK.ordinal)
        Log.d(TAG, "onReceive: $action")

        when (ActionType.fromInt(action)) {
            ActionType.CANCEL_TASK -> {
                val taskId = intent.getStringExtra(TASK_ID)
                cancelTask(context, taskId, notificationId)
            }

            ActionType.ERROR_REPORT -> {
                val errorReport = intent.getStringExtra(ERROR_REPORT)
                if (!errorReport.isNullOrEmpty()) {
                    copyErrorReport(context, errorReport, notificationId)
                }
            }

            else -> {
                Log.w(TAG, "Unknown action received: $action")
            }
        }
    }

    private fun cancelTask(context: Context, taskId: String?, notificationId: Int) {
        if (taskId.isNullOrEmpty()) {
            Log.w(TAG, "Task ID is null or empty")
            return
        }
        NotificationsUtil.cancelNotification(notificationId, context)
        val result = SpotDL.getInstance().destroyProcessById(taskId)
        if (result) {
            Log.d(TAG, "Task (id:$taskId) was killed.")
            Downloader.onProcessCanceled(taskId)
        } else {
            Log.w(TAG, "Task (id:$taskId) could not be killed.")
        }
    }

    private fun copyErrorReport(context: Context, error: String, notificationId: Int) {
        App.clipboard.setPrimaryClip(ClipData.newPlainText(null, error))
        ToastUtil.makeToastSuspend(context, context.getString(R.string.error_copied))
        NotificationsUtil.cancelNotification(notificationId, context)
    }

    companion object {
        private const val TAG = "NotificationActionReceiver"
        private const val PACKAGE_NAME_PREFIX = "com.bobbyesp.spowlo."

        // Define clear keys for the extras.
        const val ACTION = PACKAGE_NAME_PREFIX + "action"
        const val TASK_ID = PACKAGE_NAME_PREFIX + "taskId"
        const val NOTIFICATION_ID = PACKAGE_NAME_PREFIX + "notificationId"
        const val ERROR_REPORT = PACKAGE_NAME_PREFIX + "error_report"
    }

    enum class ActionType {
        CANCEL_TASK, ERROR_REPORT;

        companion object {
            fun fromInt(value: Int) = entries.firstOrNull { it.ordinal == value }
        }
    }
}