package com.example.bottomnavigation.home

import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.example.bottomnavigation.R
import java.text.SimpleDateFormat

class Content(val index:Int) : Fragment(), TextureView.SurfaceTextureListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private var surface: Surface? = null
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var textureView: TextureView
    private var isFirstPlayed: Boolean = false
    private lateinit var playButton: Button
    private lateinit var seekBar1: SeekBar
    private lateinit var seekBar2: SeekBar
    private lateinit var timePanel: ConstraintLayout
    private lateinit var timeCurrent: TextView
    private lateinit var timeDuration: TextView
    private var handler: Handler = Handler()
    private var ticker: Runnable = object : Runnable{
        override fun run() {
            handler.postDelayed(this,50)

            mediaPlayer?.let {
                if(it.isPlaying){
                    seekBar1.setProgress(it.currentPosition)
                    seekBar2.setProgress(it.currentPosition)
                }
            }
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        seekBar1.setProgress(0)
        seekBar2.setProgress(0)
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.let {
            playVideo()
            seekBar1.max = it.duration
            seekBar2.max = it.duration
            timeDuration.text = durationFormat(it.duration/1000)
            handler.post(ticker)
        }
    }

    private fun surfaceTextureCropCenter() {
        mediaPlayer?.let {
            var videoHeight = it.videoHeight.toFloat()
            var videoWidth = it.videoWidth.toFloat()
            var height = textureView.height.toFloat()
            var width = textureView.width.toFloat()

            var sx = (width/videoWidth).toFloat()
            var sy = (height/videoHeight).toFloat()

            var maxScale = Math.max(sx,sy)

            var matrix = Matrix()
//        matrix.preTranslate(width-videoWidth, height-videoHeight)
            //matrix.preScale(videoWidth/width, videoHeight/height)

            matrix.postScale(maxScale*(videoWidth/width),maxScale*(videoHeight/height),width/2,height/2)

            textureView.setTransform(matrix)
            textureView.postInvalidate()
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        surfaceTextureCropCenter()
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        surfaceTextureCropCenter()
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        stopVideo()
        return true
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        this.surface = Surface(surface)
        if(index == 0 && !isFirstPlayed){
            prepareVideo()
            isFirstPlayed = true
        }
    }


    public fun prepareVideo(){
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer()
            mediaPlayer?.let {
                it.setSurface(this.surface)
                //mediaPlayer.setScreenOnWhilePlaying(true)
                it.setDataSource(context!!,Uri.parse("android.resource://" + context?.packageName + "/" + R.raw.test2))
                //mediaPlayer.prepare()
                it.isLooping = true
                it.prepareAsync()
                it.setOnPreparedListener(this)
                it.setOnCompletionListener(this)
            }
        }
    }

    private fun playVideo(){
        mediaPlayer?.let {
            if(!it.isPlaying){
                it.start()
                ViewCompat.animate(playButton).setDuration(100).alpha(0f)
            }
        }
    }

    private fun pauseVideo(){
        mediaPlayer?.let {
            if(it.isPlaying){
                it.pause()
                ViewCompat.animate(playButton).setDuration(100).alpha(1f)
            }
        }
    }

    public fun stopVideo(){
        mediaPlayer?.let {
            it.stop()
            it.reset()
            it.release()
            mediaPlayer = null
        }
        handler.removeCallbacks(ticker)
    }

    private fun durationFormat(duration:Int): String{
        var m = duration / 60
        var s = 0
        if (duration % 60 != 0) {
           s = duration % 60
        }
        var mm = String.format("%02d",m)
        var ss = String.format("%02d",s)
        return "$mm:$ss"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_content,container,false)

        textureView = view.findViewById(R.id.texture_view)

        textureView.setSurfaceTextureListener(this)

        /*arguments?.let {
            index = it.getInt("index")
        }*/

        playButton = view.findViewById<Button>(R.id.play_button)

        seekBar1 = view.findViewById(R.id.seek_bar1)
        seekBar2 = view.findViewById(R.id.seek_bar2)

        timePanel = view.findViewById(R.id.time_panel)
        timeCurrent = view.findViewById(R.id.time_current)
        timeDuration = view.findViewById(R.id.time_duration)

        playButton.setOnClickListener{
            mediaPlayer?.let {
                if (!it.isPlaying){
                    playVideo()
                }else{
                    pauseVideo()
                }
            }
        }

        seekBar1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBar2.setProgress(progress)
                timeCurrent.text = durationFormat(progress/1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                timePanel.visibility = View.VISIBLE
                seekBar1.visibility = View.INVISIBLE
                seekBar2.visibility = View.VISIBLE
                mediaPlayer?.let {
                    if(it.isPlaying){
                        pauseVideo()
                    }
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                timePanel.visibility = View.INVISIBLE
                seekBar1.visibility = View.VISIBLE
                seekBar2.visibility = View.INVISIBLE
                mediaPlayer?.let {
                    it.seekTo(seekBar!!.progress)
                    playVideo()
                }
            }
        })

        return view
    }
}
