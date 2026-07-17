package com.med.remedy.core.alarm

/*
This file contains the impl for Alarm sound playing responsibilities
 */

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

//DEFINITION: alarm player function impl
object AlarmPlayer {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    @Volatile
    private var isCurrentlyPlaying = false
    private var stopJob: Job? = null

    // DEFINITION: core play function
    fun play(
        context: Context,
        reminderId: Long,
        soundResId: Int,
        vibrationEnabled: Boolean
    ) {
        synchronized(this) {
            if (isCurrentlyPlaying) { stop() }
            isCurrentlyPlaying = true
        }

        try {
            if (vibrationEnabled) startVibration(context)

            mediaPlayer = MediaPlayer().apply {

                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(
                            AudioAttributes.CONTENT_TYPE_SONIFICATION
                        )
                        .build()
                )

                Log.d("AlarmSound", "Resource id: $soundResId")

                setDataSource(
                    context,
                    "android.resource://${context.packageName}/$soundResId".toUri()
                )

                isLooping = true
                prepare()
                start()
            }
            // Auto stop after 1 minute
            stopJob?.cancel()

            stopJob = CoroutineScope(Dispatchers.Main).launch {
                delay(60_000L.milliseconds)
                NotificationManagerCompat.from(context).cancel(reminderId.toInt())
                stop()
            }

        } catch (e: Exception) {
            stop()
        }
    }

    // DEFINITION: core stop function to stop the sound
    fun stop() {
        stopJob?.cancel()
        stopJob = null

        synchronized(this) { isCurrentlyPlaying = false }
        try { vibrator?.cancel() } catch (_: Exception) {}
        vibrator = null

        try { mediaPlayer?.stop() } catch (_: Exception) {}
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // DEFINITION: impl for vibration
    @Suppress("DEPRECATION")
    private fun startVibration(context: Context) {
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val pattern = longArrayOf(0, 1000, 1000)
        val repeatIndex = 0
        val effect = VibrationEffect.createWaveform(pattern, repeatIndex)
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()
        vibrator?.vibrate(effect, attributes)
    }

}
