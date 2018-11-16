package de.unihannover.se.tauben2

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors private constructor() {

    private val diskIO: Executor

    private val networkIO: Executor

    private val mainThread: Executor

    private object Holder { val INSTANCE = AppExecutors() }

    companion object {
        val INSTANCE: AppExecutors by lazy { Holder.INSTANCE }

        class MainThreadExecutor : Executor {
            private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())

            override fun execute(command: Runnable) {
                mainThreadHandler.post(command)
            }
        }
    }

    init {
        diskIO = Executors.newSingleThreadExecutor()
        networkIO = Executors.newFixedThreadPool(3)
        mainThread = MainThreadExecutor()
    }

    fun diskIO() = diskIO
    fun networkIO() = networkIO
    fun mainThread() = mainThread
}
