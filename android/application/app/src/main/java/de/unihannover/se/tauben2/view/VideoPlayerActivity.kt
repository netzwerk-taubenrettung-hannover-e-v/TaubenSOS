package de.unihannover.se.tauben2.view

import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import de.unihannover.se.tauben2.R
import kotlinx.android.synthetic.main.activity_record_video.*
import kotlinx.android.synthetic.main.activity_video_player.*

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var player: SimpleExoPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        val tb = toolbar_video_player as Toolbar
        setSupportActionBar(tb)

        tb.setNavigationOnClickListener { onBackPressed() }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setTitle(R.string.watch_video)

        backgroundColor()

        player = ExoPlayerFactory.newSimpleInstance(this)

        playerView.player = player

        val dataSourceFactory = DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Tauben SOS"))


        val source = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(
                Uri.parse(intent.getStringExtra("url")))

        player.prepare(source)

        player.playWhenReady = true


    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    // sets the gradient for the status bar
    private fun backgroundColor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.setBackgroundDrawableResource(de.unihannover.se.tauben2.R.drawable.gradient)
    }
}
