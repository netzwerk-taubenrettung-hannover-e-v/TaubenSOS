package de.unihannover.se.tauben2.view

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import de.unihannover.se.tauben2.R
import kotlinx.android.synthetic.main.activity_video_player.*

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var player: SimpleExoPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

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
}
