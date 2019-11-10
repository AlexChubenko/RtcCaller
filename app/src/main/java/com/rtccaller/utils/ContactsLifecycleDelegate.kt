package com.rtccaller.utils

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.anuntis.rtccaller.R
import com.rtccaller.call.CallActivity
import com.rtccaller.call.CallLifecycleDelegate.Companion
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_AECDUMP_ENABLED
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_AUDIOCODEC
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_AUDIO_BITRATE
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_CAMERA2
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_CAPTURETOTEXTURE_ENABLED
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_CMDLINE
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_DATA_CHANNEL_ENABLED
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_DISABLE_BUILT_IN_AEC
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_DISABLE_BUILT_IN_AGC
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_DISABLE_BUILT_IN_NS
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_DISPLAY_HUD
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_ENABLE_LEVEL_CONTROL
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_FLEXFEC_ENABLED
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_HWCODEC_ENABLED
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_ID
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_LOOPBACK
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_MAX_RETRANSMITS
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_MAX_RETRANSMITS_MS
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_NEGOTIATED
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_NOAUDIOPROCESSING_ENABLED
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_OPENSLES_ENABLED
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_ORDERED
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_PROTOCOL
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_ROOMID
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_RUNTIME
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_SCREENCAPTURE
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_TRACING
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_VIDEOCODEC
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_VIDEO_BITRATE
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_VIDEO_CALL
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_VIDEO_FILE_AS_CAMERA
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_VIDEO_FPS
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_VIDEO_HEIGHT
import com.rtccaller.call.CallLifecycleDelegate.Companion.EXTRA_VIDEO_WIDTH
import java.util.*



class ContactsLifecycleDelegate(val sharedPref: SharedPreferences, val preferencesReader: PreferencesReader): LifecycleObserver {

    private var keyprefVideoCallEnabled: String? = null
    private var keyprefScreencapture: String? = null
    private var keyprefCamera2: String? = null
    private var keyprefResolution: String? = null
    private var keyprefFps: String? = null
    private var keyprefCaptureQualitySlider: String? = null
    private var keyprefVideoBitrateType: String? = null
    private var keyprefVideoBitrateValue: String? = null
    private var keyprefVideoCodec: String? = null
    private var keyprefAudioBitrateType: String? = null
    private var keyprefAudioBitrateValue: String? = null
    private var keyprefAudioCodec: String? = null
    private var keyprefHwCodecAcceleration: String? = null
    private var keyprefCaptureToTexture: String? = null
    private var keyprefFlexfec: String? = null
    private var keyprefNoAudioProcessingPipeline: String? = null
    private var keyprefAecDump: String? = null
    private var keyprefOpenSLES: String? = null
    private var keyprefDisableBuiltInAec: String? = null
    private var keyprefDisableBuiltInAgc: String? = null
    private var keyprefDisableBuiltInNs: String? = null
    private var keyprefEnableLevelControl: String? = null
    private var keyprefDisableWebRtcAGCAndHPF: String? = null
    private var keyprefDisplayHud: String? = null
    private var keyprefTracing: String? = null
    private var keyprefRoomServerUrl: String? = null
    private var keyprefRoom: String? = null
    private var keyprefRoomList: String? = null
    private var keyprefEnableDataChannel: String? = null
    private var keyprefOrdered: String? = null
    private var keyprefMaxRetransmitTimeMs: String? = null
    private var keyprefMaxRetransmits: String? = null
    private var keyprefDataProtocol: String? = null
    private var keyprefNegotiated: String? = null
    private var keyprefDataId: String? = null


    var commandLineRun = false

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun initializeProperties() {
        keyprefVideoCallEnabled = getString(R.string.pref_videocall_key)
        keyprefScreencapture = getString(R.string.pref_screencapture_key)
        keyprefCamera2 = getString(R.string.pref_camera2_key)
        keyprefResolution = getString(R.string.pref_resolution_key)
        keyprefFps = getString(R.string.pref_fps_key)
        keyprefCaptureQualitySlider = getString(R.string.pref_capturequalityslider_key)
        keyprefVideoBitrateType = getString(R.string.pref_maxvideobitrate_key)
        keyprefVideoBitrateValue = getString(R.string.pref_maxvideobitratevalue_key)
        keyprefVideoCodec = getString(R.string.pref_videocodec_key)
        keyprefHwCodecAcceleration = getString(R.string.pref_hwcodec_key)
        keyprefCaptureToTexture = getString(R.string.pref_capturetotexture_key)
        keyprefFlexfec = getString(R.string.pref_flexfec_key)
        keyprefAudioBitrateType = getString(R.string.pref_startaudiobitrate_key)
        keyprefAudioBitrateValue = getString(R.string.pref_startaudiobitratevalue_key)
        keyprefAudioCodec = getString(R.string.pref_audiocodec_key)
        keyprefNoAudioProcessingPipeline = getString(R.string.pref_noaudioprocessing_key)
        keyprefAecDump = getString(R.string.pref_aecdump_key)
        keyprefOpenSLES = getString(R.string.pref_opensles_key)
        keyprefDisableBuiltInAec = getString(R.string.pref_disable_built_in_aec_key)
        keyprefDisableBuiltInAgc = getString(R.string.pref_disable_built_in_agc_key)
        keyprefDisableBuiltInNs = getString(R.string.pref_disable_built_in_ns_key)
        keyprefEnableLevelControl = getString(R.string.pref_enable_level_control_key)
        keyprefDisableWebRtcAGCAndHPF = getString(R.string.pref_disable_webrtc_agc_and_hpf_key)
        keyprefDisplayHud = getString(R.string.pref_displayhud_key)
        keyprefTracing = getString(R.string.pref_tracing_key)
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key)
        keyprefRoom = getString(R.string.pref_room_key)
        keyprefRoomList = getString(R.string.pref_room_list_key)
        keyprefEnableDataChannel = getString(R.string.pref_enable_datachannel_key)
        keyprefOrdered = getString(R.string.pref_ordered_key)
        keyprefMaxRetransmitTimeMs = getString(R.string.pref_max_retransmit_time_ms_key)
        keyprefMaxRetransmits = getString(R.string.pref_max_retransmits_key)
        keyprefDataProtocol = getString(R.string.pref_data_protocol_key)
        keyprefNegotiated = getString(R.string.pref_negotiated_key)
        keyprefDataId = getString(R.string.pref_data_id_key)
    }

    private fun getString(resId: Int) = preferencesReader.getString(resId)

    var roomId: Int = 0

    fun getRoomConnectionIntent(roomIdP: String?, commandLineRun: Boolean, loopback: Boolean,
                                useValuesFromIntent: Boolean, runTimeMs: Int): Intent? {
        //todo separate to several methods
        //todo move variables to Map and use Sequences
        var roomId = roomIdP
        this.commandLineRun = commandLineRun

        // roomId is random for loopback.
        if (loopback) {
            roomId = Integer.toString(Random().nextInt(100000000))
        }

        val roomUrl = sharedPref.getString(
            keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default)
        )

        // Video call enabled flag.
        val videoCallEnabled = sharedPrefGetBoolean(
            R.string.pref_videocall_key,
            EXTRA_VIDEO_CALL, R.string.pref_videocall_default, useValuesFromIntent
        )

        // Use screencapture option.
        val useScreencapture = sharedPrefGetBoolean(
            R.string.pref_screencapture_key,
            EXTRA_SCREENCAPTURE,
            R.string.pref_screencapture_default,
            useValuesFromIntent
        )

        // Use Camera2 option.
        val useCamera2 = sharedPrefGetBoolean(
            R.string.pref_camera2_key, EXTRA_CAMERA2,
            R.string.pref_camera2_default, useValuesFromIntent
        )

        // Get default codecs.
        val videoCodec = sharedPrefGetString(
            R.string.pref_videocodec_key,
            EXTRA_VIDEOCODEC, R.string.pref_videocodec_default, useValuesFromIntent
        )
        val audioCodec = sharedPrefGetString(
            R.string.pref_audiocodec_key,
            EXTRA_AUDIOCODEC, R.string.pref_audiocodec_default, useValuesFromIntent
        )

        // Check HW codec flag.
        val hwCodec = sharedPrefGetBoolean(
            R.string.pref_hwcodec_key,
            EXTRA_HWCODEC_ENABLED, R.string.pref_hwcodec_default, useValuesFromIntent
        )

        // Check Capture to texture.
        val captureToTexture = sharedPrefGetBoolean(
            R.string.pref_capturetotexture_key,
            EXTRA_CAPTURETOTEXTURE_ENABLED, R.string.pref_capturetotexture_default,
            useValuesFromIntent
        )

        // Check FlexFEC.
        val flexfecEnabled = sharedPrefGetBoolean(
            R.string.pref_flexfec_key,
            EXTRA_FLEXFEC_ENABLED, R.string.pref_flexfec_default, useValuesFromIntent
        )

        // Check Disable Audio Processing flag.
        val noAudioProcessing = sharedPrefGetBoolean(
            R.string.pref_noaudioprocessing_key,
            EXTRA_NOAUDIOPROCESSING_ENABLED, R.string.pref_noaudioprocessing_default,
            useValuesFromIntent
        )

        // Check Disable Audio Processing flag.
        val aecDump = sharedPrefGetBoolean(
            R.string.pref_aecdump_key,
            EXTRA_AECDUMP_ENABLED, R.string.pref_aecdump_default, useValuesFromIntent
        )

        // Check OpenSL ES enabled flag.
        val useOpenSLES = sharedPrefGetBoolean(
            R.string.pref_opensles_key,
            EXTRA_OPENSLES_ENABLED, R.string.pref_opensles_default, useValuesFromIntent
        )

        // Check Disable built-in AEC flag.
        val disableBuiltInAEC = sharedPrefGetBoolean(
            R.string.pref_disable_built_in_aec_key,
            EXTRA_DISABLE_BUILT_IN_AEC, R.string.pref_disable_built_in_aec_default,
            useValuesFromIntent
        )

        // Check Disable built-in AGC flag.
        val disableBuiltInAGC = sharedPrefGetBoolean(
            R.string.pref_disable_built_in_agc_key,
            EXTRA_DISABLE_BUILT_IN_AGC, R.string.pref_disable_built_in_agc_default,
            useValuesFromIntent
        )

        // Check Disable built-in NS flag.
        val disableBuiltInNS = sharedPrefGetBoolean(
            R.string.pref_disable_built_in_ns_key,
            EXTRA_DISABLE_BUILT_IN_NS, R.string.pref_disable_built_in_ns_default,
            useValuesFromIntent
        )

        // Check Enable level control.
        val enableLevelControl = sharedPrefGetBoolean(
            R.string.pref_enable_level_control_key,
            EXTRA_ENABLE_LEVEL_CONTROL, R.string.pref_enable_level_control_key,
            useValuesFromIntent
        )

        // Check Disable gain control
        val disableWebRtcAGCAndHPF = sharedPrefGetBoolean(
            R.string.pref_disable_webrtc_agc_and_hpf_key,
            EXTRA_DISABLE_WEBRTC_AGC_AND_HPF,
            R.string.pref_disable_webrtc_agc_and_hpf_key,
            useValuesFromIntent
        )

        // Get video resolution from settings.
        var videoWidth = 0
        var videoHeight = 0
        if (useValuesFromIntent) {
            videoWidth = getIntent().getIntExtra(EXTRA_VIDEO_WIDTH, 0)
            videoHeight = getIntent().getIntExtra(EXTRA_VIDEO_HEIGHT, 0)
        }
        if (videoWidth == 0 && videoHeight == 0) {
            val resolution =
                sharedPref.getString(keyprefResolution, getString(R.string.pref_resolution_default))
            val dimensions =
                resolution!!.split("[ x]+".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            if (dimensions.size == 2) {
                try {
                    videoWidth = Integer.parseInt(dimensions[0])
                    videoHeight = Integer.parseInt(dimensions[1])
                } catch (e: NumberFormatException) {
                    videoWidth = 0
                    videoHeight = 0
                    Log.e(TAG, "Wrong video resolution setting: " + resolution!!)
                }

            }
        }

        // Get camera fps from settings.
        var cameraFps = 0
        if (useValuesFromIntent) {
            cameraFps = getIntent().getIntExtra(EXTRA_VIDEO_FPS, 0)
        }
        if (cameraFps == 0) {
            val fps = sharedPref.getString(keyprefFps, getString(R.string.pref_fps_default))
            val fpsValues =
                fps!!.split("[ x]+".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            if (fpsValues.size == 2) {
                try {
                    cameraFps = Integer.parseInt(fpsValues[0])
                } catch (e: NumberFormatException) {
                    cameraFps = 0
                    Log.e(TAG, "Wrong camera fps setting: " + fps!!)
                }

            }
        }

        // Check capture quality slider flag.
        val captureQualitySlider = sharedPrefGetBoolean(
            R.string.pref_capturequalityslider_key,
            EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
            R.string.pref_capturequalityslider_default, useValuesFromIntent
        )

        // Get video and audio start bitrate.
        var videoStartBitrate = 0
        if (useValuesFromIntent) {
            videoStartBitrate = getIntent().getIntExtra(EXTRA_VIDEO_BITRATE, 0)
        }
        if (videoStartBitrate == 0) {
            val bitrateTypeDefault = getString(R.string.pref_maxvideobitrate_default)
            val bitrateType = sharedPref.getString(keyprefVideoBitrateType, bitrateTypeDefault)
            if (bitrateType != bitrateTypeDefault) {
                val bitrateValue = sharedPref.getString(
                    keyprefVideoBitrateValue, getString(R.string.pref_maxvideobitratevalue_default)
                )
                videoStartBitrate = Integer.parseInt(bitrateValue!!)
            }
        }

        var audioStartBitrate = 0
        if (useValuesFromIntent) {
            audioStartBitrate = getIntent().getIntExtra(EXTRA_AUDIO_BITRATE, 0)
        }
        if (audioStartBitrate == 0) {
            val bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default)
            val bitrateType = sharedPref.getString(keyprefAudioBitrateType, bitrateTypeDefault)
            if (bitrateType != bitrateTypeDefault) {
                val bitrateValue = sharedPref.getString(
                    keyprefAudioBitrateValue,
                    getString(R.string.pref_startaudiobitratevalue_default)
                )
                audioStartBitrate = Integer.parseInt(bitrateValue!!)
            }
        }

        // Check statistics display option.
        val displayHud = sharedPrefGetBoolean(
            R.string.pref_displayhud_key,
            EXTRA_DISPLAY_HUD, R.string.pref_displayhud_default, useValuesFromIntent
        )

        val tracing = sharedPrefGetBoolean(
            R.string.pref_tracing_key, EXTRA_TRACING,
            R.string.pref_tracing_default, useValuesFromIntent
        )

        // Get datachannel options
        val dataChannelEnabled = sharedPrefGetBoolean(
            R.string.pref_enable_datachannel_key,
            EXTRA_DATA_CHANNEL_ENABLED, R.string.pref_enable_datachannel_default,
            useValuesFromIntent
        )
        val ordered = sharedPrefGetBoolean(
            R.string.pref_ordered_key, EXTRA_ORDERED,
            R.string.pref_ordered_default, useValuesFromIntent
        )
        val negotiated = sharedPrefGetBoolean(
            R.string.pref_negotiated_key,
            EXTRA_NEGOTIATED, R.string.pref_negotiated_default, useValuesFromIntent
        )
        val maxRetrMs = sharedPrefGetInteger(
            R.string.pref_max_retransmit_time_ms_key,
            EXTRA_MAX_RETRANSMITS_MS, R.string.pref_max_retransmit_time_ms_default,
            useValuesFromIntent
        )
        val maxRetr = sharedPrefGetInteger(
            R.string.pref_max_retransmits_key, EXTRA_MAX_RETRANSMITS,
            R.string.pref_max_retransmits_default, useValuesFromIntent
        )
        val id = sharedPrefGetInteger(
            R.string.pref_data_id_key, EXTRA_ID,
            R.string.pref_data_id_default, useValuesFromIntent
        )
        val protocol = sharedPrefGetString(
            R.string.pref_data_protocol_key,
            EXTRA_PROTOCOL, R.string.pref_data_protocol_default, useValuesFromIntent
        )

        // Start AppRTCMobile activity.
        Log.d(TAG, "Connecting to room $roomId at URL $roomUrl")
        if (preferencesReader.validateUrl(roomUrl)) {
            val uri = Uri.parse(roomUrl)
            val intent = preferencesReader.getEmptyCellActivityIntent()
            intent.setData(uri)
            intent.putExtra(EXTRA_ROOMID, roomId)
            intent.putExtra(EXTRA_LOOPBACK, loopback)
            intent.putExtra(EXTRA_VIDEO_CALL, videoCallEnabled)
            intent.putExtra(EXTRA_SCREENCAPTURE, useScreencapture)
            intent.putExtra(EXTRA_CAMERA2, useCamera2)
            intent.putExtra(EXTRA_VIDEO_WIDTH, videoWidth)
            intent.putExtra(EXTRA_VIDEO_HEIGHT, videoHeight)
            intent.putExtra(EXTRA_VIDEO_FPS, cameraFps)
            intent.putExtra(EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, captureQualitySlider)
            intent.putExtra(EXTRA_VIDEO_BITRATE, videoStartBitrate)
            intent.putExtra(EXTRA_VIDEOCODEC, videoCodec)
            intent.putExtra(EXTRA_HWCODEC_ENABLED, hwCodec)
            intent.putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture)
            intent.putExtra(EXTRA_FLEXFEC_ENABLED, flexfecEnabled)
            intent.putExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing)
            intent.putExtra(EXTRA_AECDUMP_ENABLED, aecDump)
            intent.putExtra(EXTRA_OPENSLES_ENABLED, useOpenSLES)
            intent.putExtra(EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC)
            intent.putExtra(EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC)
            intent.putExtra(EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS)
            intent.putExtra(EXTRA_ENABLE_LEVEL_CONTROL, enableLevelControl)
            intent.putExtra(EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, disableWebRtcAGCAndHPF)
            intent.putExtra(EXTRA_AUDIO_BITRATE, audioStartBitrate)
            intent.putExtra(EXTRA_AUDIOCODEC, audioCodec)
            intent.putExtra(EXTRA_DISPLAY_HUD, displayHud)
            intent.putExtra(EXTRA_TRACING, tracing)
            intent.putExtra(EXTRA_CMDLINE, commandLineRun)
            intent.putExtra(EXTRA_RUNTIME, runTimeMs)

            intent.putExtra(EXTRA_DATA_CHANNEL_ENABLED, dataChannelEnabled)

            if (dataChannelEnabled) {
                intent.putExtra(EXTRA_ORDERED, ordered)
                intent.putExtra(EXTRA_MAX_RETRANSMITS_MS, maxRetrMs)
                intent.putExtra(EXTRA_MAX_RETRANSMITS, maxRetr)
                intent.putExtra(EXTRA_PROTOCOL, protocol)
                intent.putExtra(EXTRA_NEGOTIATED, negotiated)
                intent.putExtra(EXTRA_ID, id)
            }

            if (useValuesFromIntent) {
                if (getIntent().hasExtra(EXTRA_VIDEO_FILE_AS_CAMERA)) {
                    val videoFileAsCamera =
                        getIntent().getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA)
                    intent.putExtra(EXTRA_VIDEO_FILE_AS_CAMERA, videoFileAsCamera)
                }

                if (getIntent().hasExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)) {
                    val saveRemoteVideoToFile =
                        getIntent().getStringExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)
                    intent.putExtra(
                        EXTRA_SAVE_REMOTE_VIDEO_TO_FILE,
                        saveRemoteVideoToFile
                    )
                }

                if (getIntent().hasExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH)) {
                    val videoOutWidth = getIntent().getIntExtra(
                        EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH,
                        0
                    )
                    intent.putExtra(
                        EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH,
                        videoOutWidth
                    )
                }

                if (getIntent().hasExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT)) {
                    val videoOutHeight = getIntent().getIntExtra(
                        EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT,
                        0
                    )
                    intent.putExtra(
                        EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT,
                        videoOutHeight
                    )
                }
            }
            //todo check this
            return intent
        }
        return null
    }

    private fun sharedPrefGetBoolean(
        attributeId: Int, intentName: String, defaultId: Int, useFromIntent: Boolean
    ): Boolean {
        val defaultValue = java.lang.Boolean.valueOf(getString(defaultId))
        if (useFromIntent) {
            return getIntent().getBooleanExtra(intentName, defaultValue)
        } else {
            val attributeName = getString(attributeId)
            return sharedPref.getBoolean(attributeName, defaultValue)
        }
    }

    private fun sharedPrefGetString(
        attributeId: Int, intentName: String, defaultId: Int, useFromIntent: Boolean
    ): String? {
        val defaultValue = getString(defaultId)
        if (useFromIntent) {
            val value = getIntent().getStringExtra(intentName)
            return value ?: defaultValue
        } else {
            val attributeName = getString(attributeId)
            return sharedPref.getString(attributeName, defaultValue)
        }
    }
    private fun sharedPrefGetInteger(
        attributeId: Int, intentName: String, defaultId: Int, useFromIntent: Boolean
    ): Int {
        val defaultString = getString(defaultId)
        val defaultValue = Integer.parseInt(defaultString)
        if (useFromIntent) {
            return getIntent().getIntExtra(intentName, defaultValue)
        } else {
            val attributeName = getString(attributeId)
            val value = sharedPref.getString(attributeName, defaultString)
            try {
                return Integer.parseInt(value!!)
            } catch (e: NumberFormatException) {
                Log.e(TAG, "Wrong setting for: $attributeName:$value")
                return defaultValue
            }

        }
    }

    private fun getIntent() = preferencesReader.getIntent()

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun disconnectListener() {

    }

    interface PreferencesReader{
        fun getString(resId: Int): String
        fun getIntent(): Intent
        fun validateUrl(url: String): Boolean
        fun getEmptyCellActivityIntent(): Intent
    }

    companion object{
        private val TAG = ContactsLifecycleDelegate::class.java.simpleName

    }
}