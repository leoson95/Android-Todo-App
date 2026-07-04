package com.example.util

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

object SoundManager {
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            // Using system stream volume
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 60)
        } catch (e: Exception) {
            Log.e("SoundManager", "Failed to initialize ToneGenerator", e)
        }
    }

    fun playTap() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing tap sound", e)
        }
    }

    fun playSuccess() {
        try {
            // Standard success confirm tone
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 100)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing success sound", e)
        }
    }

    fun playDelete() {
        try {
            // Low beep for delete/removal
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 120)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing delete sound", e)
        }
    }

    fun playAlert() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 150)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing alert sound", e)
        }
    }
}
