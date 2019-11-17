package com.rtccaller.call

import android.content.Intent

public class CallIntentParameters(val intent: Intent) {

    val loopback = intent.getBooleanExtra(EXTRA_LOOPBACK, false)
    val tracing = intent.getBooleanExtra(EXTRA_TRACING, false)
    val ordered = intent.getBooleanExtra(EXTRA_ORDERED, true)
    val maxRentransmitsMs = intent.getIntExtra(EXTRA_MAX_RETRANSMITS_MS, -1)
    val maxRentransmits = intent.getIntExtra(EXTRA_MAX_RETRANSMITS, -1)
    val protocol = intent.getStringExtra(EXTRA_PROTOCOL)
    val negotiated = intent.getBooleanExtra(EXTRA_NEGOTIATED, false)
    val id = intent.getIntExtra(EXTRA_ID, -1)

    var videoWidth = intent.getIntExtra(EXTRA_VIDEO_WIDTH, 0)
    var videoHeight = intent.getIntExtra(EXTRA_VIDEO_HEIGHT, 0)

    val videoFps = intent.getIntExtra(EXTRA_VIDEO_FPS, 0)
    val videoBitrate = intent.getIntExtra(EXTRA_VIDEO_BITRATE, 0)
    val videoCall = intent.getBooleanExtra(EXTRA_VIDEO_CALL, true)
    val videocodec = intent.getStringExtra(EXTRA_VIDEOCODEC)
    val hwCodecEnbled = intent.getBooleanExtra(EXTRA_HWCODEC_ENABLED, true)
    val flexfecEnabled = intent.getBooleanExtra(EXTRA_FLEXFEC_ENABLED, false)
    val audioBitrade = intent.getIntExtra(EXTRA_AUDIO_BITRATE, 0)
    val audiocodec = intent.getStringExtra(EXTRA_AUDIOCODEC)
    val noAudioProcessingEnabled = intent.getBooleanExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, false)
    val aecdumpEnbled = intent.getBooleanExtra(EXTRA_AECDUMP_ENABLED, false)
    val openslesEnabled = intent.getBooleanExtra(EXTRA_OPENSLES_ENABLED, false)
    val disableBuiltInAec = intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AEC, false)
    val disableBuiltInAgc = intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AGC, false)
    val disableBuiltInNc = intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_NS, false)
    val enableLevelControl = intent.getBooleanExtra(EXTRA_ENABLE_LEVEL_CONTROL, false)
    val disableWebrtcAgcAndHpf = intent.getBooleanExtra(EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, false)
    public val saveRemoteVideoToFile = intent.getStringExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)
    val videoOutWidth = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0)
    val videoOutHeight = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0)
    val roomId = intent.getStringExtra(EXTRA_ROOMID)
    val screencaptureEnabled = intent.getBooleanExtra(EXTRA_SCREENCAPTURE, false)
    val commandLineRun = intent.getBooleanExtra(EXTRA_CMDLINE, false)
    val runTimeMs = intent.getIntExtra(EXTRA_RUNTIME, 0)
    val urlParameters = intent.getStringExtra(EXTRA_URLPARAMETERS)
    val videoFileAsCamera = intent.getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA)

    companion object {
        public val EXTRA_ROOMID = "org.appspot.apprtc.ROOMID"
        public val EXTRA_URLPARAMETERS = "org.appspot.apprtc.URLPARAMETERS"
        public val EXTRA_LOOPBACK = "org.appspot.apprtc.LOOPBACK"
        public val EXTRA_VIDEO_CALL = "org.appspot.apprtc.VIDEO_CALL"
        public val EXTRA_SCREENCAPTURE = "org.appspot.apprtc.SCREENCAPTURE"
        public val EXTRA_CAMERA2 = "org.appspot.apprtc.CAMERA2"
        public val EXTRA_VIDEO_WIDTH = "org.appspot.apprtc.VIDEO_WIDTH"
        public val EXTRA_VIDEO_HEIGHT = "org.appspot.apprtc.VIDEO_HEIGHT"
        public val EXTRA_VIDEO_FPS = "org.appspot.apprtc.VIDEO_FPS"
        public val EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED =
            "org.appsopt.apprtc.VIDEO_CAPTUREQUALITYSLIDER"
        public val EXTRA_VIDEO_BITRATE = "org.appspot.apprtc.VIDEO_BITRATE"
        public val EXTRA_VIDEOCODEC = "org.appspot.apprtc.VIDEOCODEC"
        public val EXTRA_HWCODEC_ENABLED = "org.appspot.apprtc.HWCODEC"
        public val EXTRA_CAPTURETOTEXTURE_ENABLED = "org.appspot.apprtc.CAPTURETOTEXTURE"
        public val EXTRA_FLEXFEC_ENABLED = "org.appspot.apprtc.FLEXFEC"
        public val EXTRA_AUDIO_BITRATE = "org.appspot.apprtc.AUDIO_BITRATE"
        public val EXTRA_AUDIOCODEC = "org.appspot.apprtc.AUDIOCODEC"
        public val EXTRA_NOAUDIOPROCESSING_ENABLED = "org.appspot.apprtc.NOAUDIOPROCESSING"
        public val EXTRA_AECDUMP_ENABLED = "org.appspot.apprtc.AECDUMP"
        public val EXTRA_OPENSLES_ENABLED = "org.appspot.apprtc.OPENSLES"
        public val EXTRA_DISABLE_BUILT_IN_AEC = "org.appspot.apprtc.DISABLE_BUILT_IN_AEC"
        public val EXTRA_DISABLE_BUILT_IN_AGC = "org.appspot.apprtc.DISABLE_BUILT_IN_AGC"
        public val EXTRA_DISABLE_BUILT_IN_NS = "org.appspot.apprtc.DISABLE_BUILT_IN_NS"
        public val EXTRA_ENABLE_LEVEL_CONTROL = "org.appspot.apprtc.ENABLE_LEVEL_CONTROL"
        public val EXTRA_DISABLE_WEBRTC_AGC_AND_HPF = "org.appspot.apprtc.DISABLE_WEBRTC_GAIN_CONTROL"
        public val EXTRA_DISPLAY_HUD = "org.appspot.apprtc.DISPLAY_HUD"
        public val EXTRA_TRACING = "org.appspot.apprtc.TRACING"
        public val EXTRA_CMDLINE = "org.appspot.apprtc.CMDLINE"
        public val EXTRA_RUNTIME = "org.appspot.apprtc.RUNTIME"
        public val EXTRA_VIDEO_FILE_AS_CAMERA = "org.appspot.apprtc.VIDEO_FILE_AS_CAMERA"
        public  val EXTRA_SAVE_REMOTE_VIDEO_TO_FILE = "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE"
        public val EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH =
            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_WIDTH"
        public val EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT =
            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT"
        public val EXTRA_USE_VALUES_FROM_INTENT = "org.appspot.apprtc.USE_VALUES_FROM_INTENT"
        public val EXTRA_DATA_CHANNEL_ENABLED = "org.appspot.apprtc.DATA_CHANNEL_ENABLED"
        public val EXTRA_ORDERED = "org.appspot.apprtc.ORDERED"
        public val EXTRA_MAX_RETRANSMITS_MS = "org.appspot.apprtc.MAX_RETRANSMITS_MS"
        public val EXTRA_MAX_RETRANSMITS = "org.appspot.apprtc.MAX_RETRANSMITS"
        public val EXTRA_PROTOCOL = "org.appspot.apprtc.PROTOCOL"
        public val EXTRA_NEGOTIATED = "org.appspot.apprtc.NEGOTIATED"
        public val EXTRA_ID = "org.appspot.apprtc.ID"
    }
}