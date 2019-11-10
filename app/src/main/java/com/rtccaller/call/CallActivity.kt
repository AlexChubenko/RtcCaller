package com.rtccaller.call

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

class CallActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)


    }

    companion object{
        val EXTRA_ROOMID = "org.appspot.apprtc.ROOMID"
        val EXTRA_URLPARAMETERS = "org.appspot.apprtc.URLPARAMETERS"
        val EXTRA_LOOPBACK = "org.appspot.apprtc.LOOPBACK"
        val EXTRA_VIDEO_CALL = "org.appspot.apprtc.VIDEO_CALL"
        val EXTRA_SCREENCAPTURE = "org.appspot.apprtc.SCREENCAPTURE"
        val EXTRA_CAMERA2 = "org.appspot.apprtc.CAMERA2"
        val EXTRA_VIDEO_WIDTH = "org.appspot.apprtc.VIDEO_WIDTH"
        val EXTRA_VIDEO_HEIGHT = "org.appspot.apprtc.VIDEO_HEIGHT"
        val EXTRA_VIDEO_FPS = "org.appspot.apprtc.VIDEO_FPS"
        val EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED =
            "org.appsopt.apprtc.VIDEO_CAPTUREQUALITYSLIDER"
        val EXTRA_VIDEO_BITRATE = "org.appspot.apprtc.VIDEO_BITRATE"
        val EXTRA_VIDEOCODEC = "org.appspot.apprtc.VIDEOCODEC"
        val EXTRA_HWCODEC_ENABLED = "org.appspot.apprtc.HWCODEC"
        val EXTRA_CAPTURETOTEXTURE_ENABLED = "org.appspot.apprtc.CAPTURETOTEXTURE"
        val EXTRA_FLEXFEC_ENABLED = "org.appspot.apprtc.FLEXFEC"
        val EXTRA_AUDIO_BITRATE = "org.appspot.apprtc.AUDIO_BITRATE"
        val EXTRA_AUDIOCODEC = "org.appspot.apprtc.AUDIOCODEC"
        val EXTRA_NOAUDIOPROCESSING_ENABLED = "org.appspot.apprtc.NOAUDIOPROCESSING"
        val EXTRA_AECDUMP_ENABLED = "org.appspot.apprtc.AECDUMP"
        val EXTRA_OPENSLES_ENABLED = "org.appspot.apprtc.OPENSLES"
        val EXTRA_DISABLE_BUILT_IN_AEC = "org.appspot.apprtc.DISABLE_BUILT_IN_AEC"
        val EXTRA_DISABLE_BUILT_IN_AGC = "org.appspot.apprtc.DISABLE_BUILT_IN_AGC"
        val EXTRA_DISABLE_BUILT_IN_NS = "org.appspot.apprtc.DISABLE_BUILT_IN_NS"
        val EXTRA_ENABLE_LEVEL_CONTROL = "org.appspot.apprtc.ENABLE_LEVEL_CONTROL"
        val EXTRA_DISABLE_WEBRTC_AGC_AND_HPF = "org.appspot.apprtc.DISABLE_WEBRTC_GAIN_CONTROL"
        val EXTRA_DISPLAY_HUD = "org.appspot.apprtc.DISPLAY_HUD"
        val EXTRA_TRACING = "org.appspot.apprtc.TRACING"
        val EXTRA_CMDLINE = "org.appspot.apprtc.CMDLINE"
        val EXTRA_RUNTIME = "org.appspot.apprtc.RUNTIME"
        val EXTRA_VIDEO_FILE_AS_CAMERA = "org.appspot.apprtc.VIDEO_FILE_AS_CAMERA"
        val EXTRA_SAVE_REMOTE_VIDEO_TO_FILE = "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE"
        val EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH =
            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_WIDTH"
        val EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT =
            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT"
        val EXTRA_USE_VALUES_FROM_INTENT = "org.appspot.apprtc.USE_VALUES_FROM_INTENT"
        val EXTRA_DATA_CHANNEL_ENABLED = "org.appspot.apprtc.DATA_CHANNEL_ENABLED"
        val EXTRA_ORDERED = "org.appspot.apprtc.ORDERED"
        val EXTRA_MAX_RETRANSMITS_MS = "org.appspot.apprtc.MAX_RETRANSMITS_MS"
        val EXTRA_MAX_RETRANSMITS = "org.appspot.apprtc.MAX_RETRANSMITS"
        val EXTRA_PROTOCOL = "org.appspot.apprtc.PROTOCOL"
        val EXTRA_NEGOTIATED = "org.appspot.apprtc.NEGOTIATED"
        val EXTRA_ID = "org.appspot.apprtc.ID"}

}