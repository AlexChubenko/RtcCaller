package com.rtccaller.displays.contacts

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.preference.PreferenceManager
import android.util.Log
import android.webkit.URLUtil
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.rtccaller.R
import com.rtccaller.displays.call.CallActivity2
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_AECDUMP_ENABLED
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_AUDIOCODEC
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_AUDIO_BITRATE
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_CAMERA2
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_CAPTURETOTEXTURE_ENABLED
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_CMDLINE
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_DATA_CHANNEL_ENABLED
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_DISABLE_BUILT_IN_AEC
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_DISABLE_BUILT_IN_AGC
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_DISABLE_BUILT_IN_NS
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_DISPLAY_HUD
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_ENABLE_LEVEL_CONTROL
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_FLEXFEC_ENABLED
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_HWCODEC_ENABLED
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_ID
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_LOOPBACK
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_MAX_RETRANSMITS
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_MAX_RETRANSMITS_MS
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_NEGOTIATED
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_NOAUDIOPROCESSING_ENABLED
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_OPENSLES_ENABLED
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_ORDERED
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_PROTOCOL
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_ROOMID
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_RUNTIME
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_SCREENCAPTURE
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_TRACING
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_VIDEOCODEC
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_VIDEO_BITRATE
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_VIDEO_CALL
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_VIDEO_FILE_AS_CAMERA
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_VIDEO_FPS
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_VIDEO_HEIGHT
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_VIDEO_WIDTH
import java.util.*

class ContactsLifecycleDelegate(val preferencesReader: PreferencesReader): LifecycleObserver {

    class IntentValues(val context: Context) {

//        val keyprefVideoCallEnabled: String = getString(R.string.pref_videocall_key)
//        val keyprefScreencapture: String = getString(R.string.pref_screencapture_key)
//        val keyprefCamera2: String = getString(R.string.pref_camera2_key)
        val keyprefResolution: String = getString(R.string.pref_resolution_key)
        val keyprefFps: String = getString(R.string.pref_fps_key)
//        val keyprefCaptureQualitySlider: String = getString(R.string.pref_capturequalityslider_key)
        val keyprefVideoBitrateType: String = getString(R.string.pref_maxvideobitrate_key)
        val keyprefVideoBitrateValue: String = getString(R.string.pref_maxvideobitratevalue_key)
        val keyprefVideoCodec: String = getString(R.string.pref_videocodec_key)
        val keyprefAudioBitrateType: String = getString(R.string.pref_startaudiobitrate_key)
        val keyprefAudioBitrateValue: String = getString(R.string.pref_startaudiobitratevalue_key)
        val keyprefAudioCodec: String = getString(R.string.pref_audiocodec_key)
        val keyprefHwCodecAcceleration: String = getString(R.string.pref_hwcodec_key)
        val keyprefCaptureToTexture: String = getString(R.string.pref_capturetotexture_key)
        val keyprefFlexfec: String = getString(R.string.pref_flexfec_key)
        val keyprefNoAudioProcessingPipeline: String = getString(R.string.pref_noaudioprocessing_key)
        val keyprefAecDump: String = getString(R.string.pref_aecdump_key)
        val keyprefOpenSLES: String = getString(R.string.pref_opensles_key)
        val keyprefDisableBuiltInAec: String = getString(R.string.pref_disable_built_in_aec_key)
        val keyprefDisableBuiltInAgc: String = getString(R.string.pref_disable_built_in_agc_key)
        val keyprefDisableBuiltInNs: String = getString(R.string.pref_disable_built_in_ns_key)
        val keyprefEnableLevelControl: String = getString(R.string.pref_enable_level_control_key)
        val keyprefDisableWebRtcAGCAndHPF: String = getString(R.string.pref_disable_webrtc_agc_and_hpf_key)
        val keyprefDisplayHud: String = getString(R.string.pref_displayhud_key)
        val keyprefTracing: String = getString(R.string.pref_tracing_key)
        val keyprefRoomServerUrl: String = getString(R.string.pref_room_server_url_key)
//        val keyprefRoom: String = getString(R.string.pref_room_key)
//        val keyprefRoomList: String = getString(R.string.pref_room_list_key)
        val keyprefEnableDataChannel: String = getString(R.string.pref_enable_datachannel_key)
        val keyprefOrdered: String = getString(R.string.pref_ordered_key)
        val keyprefMaxRetransmitTimeMs: String = getString(R.string.pref_max_retransmit_time_ms_key)
        val keyprefMaxRetransmits: String = getString(R.string.pref_max_retransmits_key)
        val keyprefDataProtocol: String = getString(R.string.pref_data_protocol_key)
        val keyprefNegotiated: String = getString(R.string.pref_negotiated_key)
        val keyprefDataId: String = getString(R.string.pref_data_id_key)


        private fun getString(resId: Int) = context.getString(resId)
    }

    companion object{

        private val TAG = ContactsLifecycleDelegate::class.java.simpleName

        //todo убрать все костыли
        fun getRoomConnectionIntent(
            roomIdP: String?, commandLineRun: Boolean, loopback: Boolean,
            useValuesFromIntent: Boolean, runTimeMs: Int, context: Context
        ): Intent? {
            val iv = IntentValues(context)
            PreferenceManager.setDefaultValues(context, R.xml.preferences, false)
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

            //todo separate to several methods
            //todo move variables to Map and use Sequences
            var roomId = roomIdP

            // roomId is random for loopback.
            if (loopback) {
                roomId = Integer.toString(Random().nextInt(100000000))
            }

            val roomUrl = sharedPref.getString(
                iv.keyprefRoomServerUrl, context.getString(R.string.pref_room_server_url_default)
            )

            // Video call enabled flag.
            val videoCallEnabled =
                sharedPrefGetBoolean(
                    R.string.pref_videocall_key,
                    EXTRA_VIDEO_CALL,
                    R.string.pref_videocall_default,
                    useValuesFromIntent,
                    context,
                    sharedPref
                )

            // Use screencapture option.
            val useScreencapture =
                sharedPrefGetBoolean(
                    R.string.pref_screencapture_key,
                    EXTRA_SCREENCAPTURE,
                    R.string.pref_screencapture_default,
                    useValuesFromIntent, context, sharedPref
                )

            // Use Camera2 option.
            val useCamera2 =
                sharedPrefGetBoolean(
                    R.string.pref_camera2_key, EXTRA_CAMERA2,
                    R.string.pref_camera2_default, useValuesFromIntent, context, sharedPref
                )

            // Get default codecs.
            val videoCodec =
                sharedPrefGetString(
                    R.string.pref_videocodec_key,
                    EXTRA_VIDEOCODEC,
                    R.string.pref_videocodec_default,
                    useValuesFromIntent,
                    context,
                    sharedPref
                )
            val audioCodec =
                sharedPrefGetString(
                    R.string.pref_audiocodec_key,
                    EXTRA_AUDIOCODEC,
                    R.string.pref_audiocodec_default,
                    useValuesFromIntent,
                    context,
                    sharedPref
                )

            // Check HW codec flag.
            val hwCodec =
                sharedPrefGetBoolean(
                    R.string.pref_hwcodec_key,
                    EXTRA_HWCODEC_ENABLED,
                    R.string.pref_hwcodec_default,
                    useValuesFromIntent,
                    context,
                    sharedPref
                )

            // Check Capture to texture.
            val captureToTexture =
                sharedPrefGetBoolean(
                    R.string.pref_capturetotexture_key,
                    EXTRA_CAPTURETOTEXTURE_ENABLED, R.string.pref_capturetotexture_default,
                    useValuesFromIntent, context, sharedPref
                )

            // Check FlexFEC.
            val flexfecEnabled =
                sharedPrefGetBoolean(
                    R.string.pref_flexfec_key,
                    EXTRA_FLEXFEC_ENABLED,
                    R.string.pref_flexfec_default,
                    useValuesFromIntent,
                    context,
                    sharedPref
                )

            // Check Disable Audio Processing flag.
            val noAudioProcessing =
                sharedPrefGetBoolean(
                    R.string.pref_noaudioprocessing_key,
                    EXTRA_NOAUDIOPROCESSING_ENABLED, R.string.pref_noaudioprocessing_default,
                    useValuesFromIntent, context, sharedPref
                )

            // Check Disable Audio Processing flag.
            val aecDump =
                sharedPrefGetBoolean(
                    R.string.pref_aecdump_key,
                    EXTRA_AECDUMP_ENABLED,
                    R.string.pref_aecdump_default,
                    useValuesFromIntent,
                    context,
                    sharedPref
                )

            // Check OpenSL ES enabled flag.
            val useOpenSLES =
                sharedPrefGetBoolean(
                    R.string.pref_opensles_key,
                    EXTRA_OPENSLES_ENABLED,
                    R.string.pref_opensles_default,
                    useValuesFromIntent,
                    context,
                    sharedPref
                )

            // Check Disable built-in AEC flag.
            val disableBuiltInAEC =
                sharedPrefGetBoolean(
                    R.string.pref_disable_built_in_aec_key,
                    EXTRA_DISABLE_BUILT_IN_AEC, R.string.pref_disable_built_in_aec_default,
                    useValuesFromIntent, context, sharedPref
                )

            // Check Disable built-in AGC flag.
            val disableBuiltInAGC =
                sharedPrefGetBoolean(
                    R.string.pref_disable_built_in_agc_key,
                    EXTRA_DISABLE_BUILT_IN_AGC, R.string.pref_disable_built_in_agc_default,
                    useValuesFromIntent, context, sharedPref
                )

            // Check Disable built-in NS flag.
            val disableBuiltInNS =
                sharedPrefGetBoolean(
                    R.string.pref_disable_built_in_ns_key,
                    EXTRA_DISABLE_BUILT_IN_NS, R.string.pref_disable_built_in_ns_default,
                    useValuesFromIntent, context, sharedPref
                )

            // Check Enable level control.
            val enableLevelControl =
                sharedPrefGetBoolean(
                    R.string.pref_enable_level_control_key,
                    EXTRA_ENABLE_LEVEL_CONTROL, R.string.pref_enable_level_control_key,
                    useValuesFromIntent, context, sharedPref
                )

            // Check Disable gain control
            val disableWebRtcAGCAndHPF =
                sharedPrefGetBoolean(
                    R.string.pref_disable_webrtc_agc_and_hpf_key,
                    EXTRA_DISABLE_WEBRTC_AGC_AND_HPF,
                    R.string.pref_disable_webrtc_agc_and_hpf_key,
                    useValuesFromIntent, context, sharedPref
                )

            // Get video resolution from settings.
            var videoWidth = 0
            var videoHeight = 0
            if (useValuesFromIntent) {
                videoWidth = (context as AppCompatActivity).getIntent().getIntExtra(EXTRA_VIDEO_WIDTH, 0)
                videoHeight = (context as AppCompatActivity).getIntent().getIntExtra(EXTRA_VIDEO_HEIGHT, 0)
            }
            if (videoWidth == 0 && videoHeight == 0) {
                val resolution =
                    sharedPref.getString(
                        iv.keyprefResolution,
                        context.getString(R.string.pref_resolution_default)
                    )
                val dimensions =
                    resolution!!.split("[ x]+".toRegex()).dropLastWhile({ it.isEmpty() })
                        .toTypedArray()
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
                cameraFps = (context as AppCompatActivity).getIntent().getIntExtra(EXTRA_VIDEO_FPS, 0)
            }
            if (cameraFps == 0) {
                val fps = sharedPref.getString(iv.keyprefFps, context.getString(R.string.pref_fps_default))
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
            val captureQualitySlider =
                sharedPrefGetBoolean(
                    R.string.pref_capturequalityslider_key,
                    EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
                    R.string.pref_capturequalityslider_default,
                    useValuesFromIntent,
                    context,
                    sharedPref
                )

            // Get video and audio start bitrate.
            var videoStartBitrate = 0
            if (useValuesFromIntent) {
                videoStartBitrate = (context as AppCompatActivity).getIntent().getIntExtra(EXTRA_VIDEO_BITRATE, 0)
            }
            if (videoStartBitrate == 0) {
                val bitrateTypeDefault = context.getString(R.string.pref_maxvideobitrate_default)
                val bitrateType = sharedPref.getString(iv.keyprefVideoBitrateType, bitrateTypeDefault)
                if (bitrateType != bitrateTypeDefault) {
                    val bitrateValue = sharedPref.getString(
                        iv.keyprefVideoBitrateValue,
                        context.getString(R.string.pref_maxvideobitratevalue_default)
                    )
                    videoStartBitrate = Integer.parseInt(bitrateValue!!)
                }
            }

            var audioStartBitrate = 0
            if (useValuesFromIntent) {
                audioStartBitrate = (context as AppCompatActivity).getIntent().getIntExtra(EXTRA_AUDIO_BITRATE, 0)
            }
            if (audioStartBitrate == 0) {
                val bitrateTypeDefault = context.getString(R.string.pref_startaudiobitrate_default)
                val bitrateType = sharedPref.getString(iv.keyprefAudioBitrateType, bitrateTypeDefault)
                if (bitrateType != bitrateTypeDefault) {
                    val bitrateValue = sharedPref.getString(
                        iv.keyprefAudioBitrateValue,
                        context.getString(R.string.pref_startaudiobitratevalue_default)
                    )
                    audioStartBitrate = Integer.parseInt(bitrateValue!!)
                }
            }

            // Check statistics display option.
            val displayHud =
                sharedPrefGetBoolean(
                    R.string.pref_displayhud_key,
                    EXTRA_DISPLAY_HUD,
                    R.string.pref_displayhud_default,
                    useValuesFromIntent,
                    context,
                    sharedPref
                )

            val tracing =
                sharedPrefGetBoolean(
                    R.string.pref_tracing_key, EXTRA_TRACING,
                    R.string.pref_tracing_default, useValuesFromIntent, context, sharedPref
                )

            // Get datachannel options
            val dataChannelEnabled =
                sharedPrefGetBoolean(
                    R.string.pref_enable_datachannel_key,
                    EXTRA_DATA_CHANNEL_ENABLED, R.string.pref_enable_datachannel_default,
                    useValuesFromIntent, context, sharedPref
                )
            val ordered =
                sharedPrefGetBoolean(
                    R.string.pref_ordered_key, EXTRA_ORDERED,
                    R.string.pref_ordered_default, useValuesFromIntent, context, sharedPref
                )
            val negotiated =
                sharedPrefGetBoolean(
                    R.string.pref_negotiated_key,
                    EXTRA_NEGOTIATED,
                    R.string.pref_negotiated_default,
                    useValuesFromIntent,
                    context,
                    sharedPref
                )
            val maxRetrMs =
                sharedPrefGetInteger(
                    R.string.pref_max_retransmit_time_ms_key,
                    EXTRA_MAX_RETRANSMITS_MS, R.string.pref_max_retransmit_time_ms_default,
                    useValuesFromIntent, context, sharedPref
                )
            val maxRetr =
                sharedPrefGetInteger(
                    R.string.pref_max_retransmits_key, EXTRA_MAX_RETRANSMITS,
                    R.string.pref_max_retransmits_default, useValuesFromIntent, context, sharedPref
                )
            val id =
                sharedPrefGetInteger(
                    R.string.pref_data_id_key, EXTRA_ID,
                    R.string.pref_data_id_default, useValuesFromIntent, context, sharedPref
                )
            val protocol =
                sharedPrefGetString(
                    R.string.pref_data_protocol_key,
                    EXTRA_PROTOCOL,
                    R.string.pref_data_protocol_default,
                    useValuesFromIntent,
                    context,
                    sharedPref
                )

            // Start AppRTCMobile activity.
            Log.d(TAG, "Connecting to room $roomId at URL $roomUrl")
            if (validateUrl(
                    roomUrl,
                    context
                )
            ) {
                val uri = Uri.parse(roomUrl)
                val intent = Intent(context, CallActivity2::class.java)
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
                    if ((context as AppCompatActivity).getIntent().hasExtra(EXTRA_VIDEO_FILE_AS_CAMERA)) {
                        val videoFileAsCamera =
                            (context as AppCompatActivity).getIntent().getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA)
                        intent.putExtra(EXTRA_VIDEO_FILE_AS_CAMERA, videoFileAsCamera)
                    }

                    if ((context as AppCompatActivity).getIntent().hasExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)) {
                        val saveRemoteVideoToFile =
                            (context as AppCompatActivity).getIntent().getStringExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)
                        intent.putExtra(
                            EXTRA_SAVE_REMOTE_VIDEO_TO_FILE,
                            saveRemoteVideoToFile
                        )
                    }

                    if ((context as AppCompatActivity).getIntent().hasExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH)) {
                        val videoOutWidth = (context as AppCompatActivity).getIntent().getIntExtra(
                            EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH,
                            0
                        )
                        intent.putExtra(
                            EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH,
                            videoOutWidth
                        )
                    }

                    if ((context as AppCompatActivity).getIntent().hasExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT)) {
                        val videoOutHeight = (context as AppCompatActivity).getIntent().getIntExtra(
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
            attributeId: Int, intentName: String, defaultId: Int, useFromIntent: Boolean, context: Context, sharedPref: SharedPreferences
        ): Boolean {
            val defaultValue = java.lang.Boolean.valueOf(context.getString(defaultId))
            if (useFromIntent || context is AppCompatActivity) {
                return (context as AppCompatActivity).getIntent().getBooleanExtra(intentName, defaultValue)
            } else {
                val attributeName = context.getString(attributeId)
                return sharedPref.getBoolean(attributeName, defaultValue)
            }
        }

        private fun sharedPrefGetString(
            attributeId: Int, intentName: String, defaultId: Int, useFromIntent: Boolean,
            context: Context, sharedPref: SharedPreferences): String? {
            val defaultValue = context.getString(defaultId)
            if (useFromIntent|| context is AppCompatActivity) {
                val value = (context as AppCompatActivity).getIntent().getStringExtra(intentName)
                return value ?: defaultValue
            } else {
                val attributeName = context.getString(attributeId)
                return sharedPref.getString(attributeName, defaultValue)
            }
        }

        private fun sharedPrefGetInteger(
            attributeId: Int, intentName: String, defaultId: Int, useFromIntent: Boolean, context: Context, sharedPref: SharedPreferences
        ): Int {
            val defaultString = context.getString(defaultId)
            val defaultValue = Integer.parseInt(defaultString)
            if (useFromIntent || context is AppCompatActivity) {
                return (context as AppCompatActivity).getIntent().getIntExtra(intentName, defaultValue)
            } else {
                val attributeName = context.getString(attributeId)
                val value = sharedPref.getString(attributeName, defaultString)
                try {
                    return Integer.parseInt(value!!)
                } catch (e: NumberFormatException) {
                    Log.e(TAG, "Wrong setting for: $attributeName:$value")
                    return defaultValue
                }

            }
        }

        fun validateUrl(url: String?, context: Context): Boolean {
            if (url != null || URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
                return true
            }

            AlertDialog.Builder(context)
                .setTitle(context.getText(R.string.invalid_url_title))
                .setMessage(context.getString(R.string.invalid_url_text, url))
                .setCancelable(false)
                .setNeutralButton(
                    R.string.ok
                ) { dialog, id -> dialog.cancel() }
                .create()
                .show()
            return false
        }

    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun disconnectListener() {

    }

    interface PreferencesReader{
        fun getString(resId: Int): String
        fun getIntent(): Intent
    }
}