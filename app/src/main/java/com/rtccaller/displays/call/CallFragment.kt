package com.rtccaller.displays.call

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rtccaller.R
import org.webrtc.RendererCommon.ScalingType
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_ROOMID
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_VIDEO_CALL
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED

/**
 * Fragment for call control.
 */
class CallFragment : Fragment() {
    private var controlView: View? = null
    private var contactView: TextView? = null
    private var disconnectButton: ImageButton? = null
    private var cameraSwitchButton: ImageButton? = null
    private var videoScalingButton: ImageButton? = null
    private var toggleMuteButton: ImageButton? = null
    private var captureFormatText: TextView? = null
    private var captureFormatSlider: SeekBar? = null
    private var callEvents: OnCallEvents? = null
    private var scalingType: ScalingType? = null
    private var videoCallEnabled = true

    /**
     * Call control interface for container activity.
     */
    interface OnCallEvents {
        fun onCallHangUp()
        fun onCameraSwitch()
        fun onVideoScalingSwitch(scalingType: ScalingType?)
        fun onCaptureFormatChange(width: Int, height: Int, framerate: Int)
        fun onToggleMic(): Boolean
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        controlView = inflater.inflate(R.layout.fragment_call, container, false)

        // Create UI controls.
        contactView = controlView!!.findViewById<View>(R.id.contact_name_call) as TextView
        disconnectButton =
            controlView!!.findViewById<View>(R.id.button_call_disconnect) as ImageButton
        cameraSwitchButton =
            controlView!!.findViewById<View>(R.id.button_call_switch_camera) as ImageButton
        videoScalingButton =
            controlView!!.findViewById<View>(R.id.button_call_scaling_mode) as ImageButton
        toggleMuteButton =
            controlView!!.findViewById<View>(R.id.button_call_toggle_mic) as ImageButton
        captureFormatText =
            controlView!!.findViewById<View>(R.id.capture_format_text_call) as TextView
        captureFormatSlider =
            controlView!!.findViewById<View>(R.id.capture_format_slider_call) as SeekBar

        // Add buttons click events.
        disconnectButton!!.setOnClickListener { callEvents!!.onCallHangUp() }

        cameraSwitchButton!!.setOnClickListener { callEvents!!.onCameraSwitch() }

        videoScalingButton!!.setOnClickListener {
            if (scalingType == ScalingType.SCALE_ASPECT_FILL) {
                videoScalingButton!!.setBackgroundResource(R.drawable.ic_action_full_screen)
                scalingType = ScalingType.SCALE_ASPECT_FIT
            } else {
                videoScalingButton!!.setBackgroundResource(R.drawable.ic_action_return_from_full_screen)
                scalingType = ScalingType.SCALE_ASPECT_FILL
            }
            callEvents!!.onVideoScalingSwitch(scalingType)
        }
        scalingType = ScalingType.SCALE_ASPECT_FILL

        toggleMuteButton!!.setOnClickListener {
            val enabled = callEvents!!.onToggleMic()
            toggleMuteButton!!.alpha = if (enabled) 1.0f else 0.3f
        }

        return controlView
    }

    override fun onStart() {
        super.onStart()

        var captureSliderEnabled = false
        val args = arguments
        if (args != null) {
            val contactName = args.getString(EXTRA_ROOMID)
            contactView!!.text = contactName
            videoCallEnabled = args.getBoolean(EXTRA_VIDEO_CALL, true)
            captureSliderEnabled =
                videoCallEnabled && args.getBoolean(EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, false)
        }
        if (!videoCallEnabled) {
            cameraSwitchButton!!.visibility = View.INVISIBLE
        }
        if (captureSliderEnabled) {
            captureFormatSlider!!.setOnSeekBarChangeListener(
                CaptureQualityController(captureFormatText, callEvents)
            )
        } else {
            captureFormatText!!.visibility = View.GONE
            captureFormatSlider!!.visibility = View.GONE
        }
    }

    // TODO(sakal): Replace with onAttach(Context) once we only support API level 23+.
    override fun onAttach(activity: Activity) {
        Log.d(TAG, "aChub onAttach before")
        super.onAttach(activity)
        callEvents = activity as OnCallEvents
        Log.d(TAG, "aChub onAttach after")
    }

    companion object{
        val TAG = CallFragment::class.java.simpleName
    }
}
