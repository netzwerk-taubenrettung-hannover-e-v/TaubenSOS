package de.unihannover.se.tauben2.view

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.otaliastudios.cameraview.*
import de.unihannover.se.tauben2.R
import kotlinx.android.synthetic.main.activity_record_video.*
import java.io.File


class RecordVideoActivity : AppCompatActivity() {

    private var cam: CameraView? = null
    private var recording = false

    companion object {

        private const val maxDurationSeconds = 15
        private const val maxWidth = 640
        private const val maxHeight = 480
        private const val videoKbps = 1000
        private const val audioKbps = 24

        private const val kilo = 1000

        private val LOG_TAG = RecordVideoActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_video)

        val tb = toolbar as Toolbar
        setSupportActionBar(tb)

        tb.setNavigationOnClickListener { onBackPressed() }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setTitle(R.string.record_video)

        cam = cameraView
        cam?.apply {
            setLifecycleOwner(this@RecordVideoActivity)
            mode = Mode.VIDEO
            facing = Facing.BACK
            videoBitRate = videoKbps * kilo
            videoMaxDuration = maxDurationSeconds * kilo
            audioBitRate = audioKbps * kilo

            val width = SizeSelectors.maxWidth(maxWidth)
            val height = SizeSelectors.maxHeight(maxHeight)
            setVideoSize(SizeSelectors.and(width, height))
        }


        cam?.addCameraListener(object : CameraListener() {
            override fun onVideoTaken(result: VideoResult) {
                Log.d(LOG_TAG, "Video taken")
                setResult(RESULT_OK)
                finish()
            }

            override fun onCameraError(exception: CameraException) {
                super.onCameraError(exception)
                throw exception
            }
        })
    }

    fun record(view: View) {
        if (recording) {
            recording = false
            cam?.stopVideo()
            return
        }

        val path = intent.getStringExtra("url")
        cam?.takeVideo(File(path))
        btnRecord.text = getString(R.string.stop)
        recording = true

        Log.d(LOG_TAG, "recording...")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
