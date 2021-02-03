package mobile.birdie.exam1.messages.data.offlineSupport

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.Worker

class MessagesRepoWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
        override fun doWork(): Result {
            when (inputData.getString("operation")) {
                "read" -> RepoHelper.read()
                 else -> return Result.failure()
            }
            return Result.success()
        }
}