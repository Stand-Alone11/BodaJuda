package com.aeye.thirdeye.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.aeye.thirdeye.R

object SoundAlarmUtil {

    private const val MAX_STREAM = 1;
    private const val PRIORITY = 8;

    private val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
    private lateinit var soundPool: SoundPool
    var loadId: Int = -1
    private const val TAG = "SoundAlarmUtil_debuk"

    fun load(context: Context) {
        if(loadId == -1) {
            soundPool = SoundPool.Builder().setMaxStreams(MAX_STREAM).setAudioAttributes(audioAttributes).build()
            soundPool.setOnLoadCompleteListener { _, sampleId, status ->
                Log.d(TAG, "load: ${status}")
                if(status == 0) {
                    loadId = sampleId
                    Log.d(TAG, "loadCompleted: $loadId")
                }
            }
            soundPool.load(context, R.raw.alarm, PRIORITY)
            Log.d(TAG, "beforeLoad: $loadId")
        }
    }

    fun play() {
        Log.d(TAG, "play: $loadId")
        if(loadId != -1) {
            soundPool.play(loadId, 1f, 1f, PRIORITY, 0, 1f)
        }
    }

    /**
     * release() 호출 되면 soundPool은 null
     */
    fun release() {
        soundPool.unload(loadId)
        loadId = -1
        soundPool.release()
    }




}