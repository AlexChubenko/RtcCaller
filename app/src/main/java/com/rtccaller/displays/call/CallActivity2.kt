package com.rtccaller.displays.call

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


import androidx.fragment.app.FragmentTransaction
import com.rtccaller.R
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_CAMERA2
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_CAPTURETOTEXTURE_ENABLED
import com.rtccaller.displays.call.CallIntentParameters.Companion.EXTRA_VIDEO_FILE_AS_CAMERA
import com.rtccaller.services.*
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity

import org.webrtc.*
import java.io.IOException
import java.util.ArrayList
import javax.inject.Inject


class CallActivity2: DaggerAppCompatActivity()/*, HasSupportFragmentInjector*/, AppRTCClient.SignalingEvents,
    PeerConnectionClient.PeerConnectionEvents,
    CallFragment.OnCallEvents {

//    @set:Inject
//    internal var fragmentAndroidInjector: DispatchingAndroidInjector<Fragment>? = null
//
//    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
//        return fragmentAndroidInjector
//    }

    companion object{
        internal val TAG = CallActivity2::class.java.simpleName

        internal const val CAPTURE_PERMISSION_REQUEST_CODE = 1
        private const val CAMERA_AUDIO_PERMISSION_REQUEST = 11

        internal val MANDATORY_PERMISSIONS = arrayOf(
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO",
            "android.permission.INTERNET"
        )

        internal val STAT_CALLBACK_PERIOD = 1000

    }

    private inner class ProxyRenderer : VideoRenderer.Callbacks {
        private var target: VideoRenderer.Callbacks? = null

        @Synchronized
        override fun renderFrame(frame: VideoRenderer.I420Frame) {
            if (target == null) {
                Logging.d(TAG, "Dropping frame in proxy because target is null.")
                VideoRenderer.renderFrameDone(frame)
                return
            }

            target!!.renderFrame(frame)
        }

        @Synchronized
        fun setTarget(target: VideoRenderer.Callbacks?) {
            this.target = target
        }
    }

    private val remoteProxyRenderer = ProxyRenderer()
    private val localProxyRenderer = ProxyRenderer()

    @Inject
    lateinit var peerConnectionClient: PeerConnectionClient

    @Inject
    lateinit var intentParameters: CallIntentParameters
    @Inject
    lateinit var peerConnectionParameters: PeerConnectionClient.PeerConnectionParameters

    private var appRtcClient: AppRTCClient? = null
    private var signalingParameters: AppRTCClient.SignalingParameters? = null
    private var audioManager: AppRTCAudioManager? = null
    private var rootEglBase: EglBase? = null
    private lateinit var pipRenderer: SurfaceViewRenderer
    private var fullscreenRenderer: SurfaceViewRenderer? = null
    private var videoFileRenderer: VideoFileRenderer? = null
    private val remoteRenderers = ArrayList<VideoRenderer.Callbacks>()
    private var logToast: Toast? = null
    private val commandLineRun: Boolean = false
    private val runTimeMs: Int = 0
    private var activityRunning: Boolean = false
    private var roomConnectionParameters: AppRTCClient.RoomConnectionParameters? = null

    private var iceConnected: Boolean = false
    private var isError: Boolean = false
    private var callControlFragmentVisible = true
    private var callStartedTimeMs: Long = 0
    private var micEnabled = true
    private val screencaptureEnabled = false
    private var mediaProjectionPermissionResultData: Intent? = null
    private var mediaProjectionPermissionResultCode: Int = 0
    // True if local view is in the fullscreen renderer.
    private var isSwappedFeeds: Boolean = false

    // Controls
    private var callFragment: CallFragment? = null
    private var hudFragment: HudFragment? = null
    private var cpuMonitor: CpuMonitor? = null

    private var arePermissionsGranted = false

      public override fun onCreate(savedInstanceState: Bundle?) {
//          AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
          Log.d(TAG, "aChub onCreate 1")
         //    Thread.setDefaultUncaughtExceptionHandler(new UnhandledExceptionHandler(this));

            // Set window styles for fullscreen-window size. Needs to be done before
            // adding content.
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//            or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//            or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
          Log.d(TAG, "aChub onCreate 2")
         //todo keep this for checking CallActivity visibility
         //    getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());
            setContentView(R.layout.activity_call)

        iceConnected = false
        signalingParameters = null

         // Create UI controls.
            pipRenderer = findViewById<View>(R.id.pip_video_view) as SurfaceViewRenderer
        fullscreenRenderer = findViewById<View>(R.id.fullscreen_video_view) as SurfaceViewRenderer
//        callFragment = CallFragment()
//        hudFragment = HudFragment()

         // Show/hide call control fragment on view click.
            val listener = View.OnClickListener { toggleCallControlFragmentVisibility() }

         // Swap feeds on pip view click.
            pipRenderer.setOnClickListener { setSwappedFeeds(!isSwappedFeeds) }

          fullscreenRenderer!!.setOnClickListener(listener)
        remoteRenderers.add(remoteProxyRenderer)

          Log.d(TAG, "aChub onCreate 3")

        val intent = intent

         // Create video renderers.
            rootEglBase = EglBase.create()
        pipRenderer.init(rootEglBase!!.eglBaseContext, null)
        pipRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)

         // When saveRemoteVideoToFile is set we save the video from the remote to a file.
        if (intentParameters.saveRemoteVideoToFile != null) {
            try {
                videoFileRenderer = VideoFileRenderer(
                intentParameters.saveRemoteVideoToFile, intentParameters.videoOutWidth, intentParameters.videoOutHeight, rootEglBase!!.getEglBaseContext())
                remoteRenderers.add(videoFileRenderer!!)
            }
            catch (e: IOException) {
                throw RuntimeException(
                    "Failed to open video file for output: " + intentParameters.saveRemoteVideoToFile, e)
            }
        }

          Log.d(TAG, "aChub onCreate 4")

        fullscreenRenderer!!.init(rootEglBase!!.eglBaseContext, null)
        fullscreenRenderer!!.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)

        pipRenderer.setZOrderMediaOverlay(true)
        pipRenderer.setEnableHardwareScaler(true /* enabled */)
        fullscreenRenderer!!.setEnableHardwareScaler(true /* enabled */)
         // Start with local feed in fullscreen and swap it to the pip when the call is connected.
            setSwappedFeeds(true /* isSwappedFeeds */)

          Log.d(TAG, "aChub onCreate 5")

         // Check for mandatory permissions.
//        for (permission in MANDATORY_PERMISSIONS) {
//            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "aChub() checkCallingOrSelfPermission $permission")
//                logAndToast("Permission $permission is not granted")
//                setResult(RESULT_CANCELED)
//                finish()
//                return
//            }
//        }
          initFragments(intent)
          Log.d(TAG, "aChub onCreate 6")
          handlePermissions()

         //todo move to delegate

    }

    private fun prepareCall(){
        val roomUri = intent.data
        if (roomUri == null) {
            logAndToast(getString(R.string.missing_url))
            Log.e(TAG, "Didn't get any URL in intent!")
            setResult(RESULT_CANCELED)
            finish()
            return }

        // Get Intent parameters.

        Log.d(TAG, "Room ID: " + intentParameters.roomId)
        if (intentParameters.roomId == null || intentParameters.roomId.isEmpty()) {
            logAndToast(getString(R.string.missing_url))
            Log.e(TAG, "Incorrect room ID in intent!")
            setResult(RESULT_CANCELED)
            finish()
            return
        }


        // If capturing format is not specified for screencapture, use screen resolution.
        if (screencaptureEnabled && intentParameters.videoWidth == 0 && intentParameters.videoHeight == 0)
        {
            val displayMetrics = getDisplayMetrics()
            intentParameters.videoWidth = displayMetrics.widthPixels
            intentParameters.videoHeight = displayMetrics.heightPixels
        }

        Log.d(TAG, "VIDEO_FILE: '" + intent.getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA) + "'")

        // Create connection client. Use DirectRTCClient if room name is an IP otherwise use the
        // standard WebSocketRTCClient.
        if (intentParameters.loopback || !DirectRTCClient.IP_PATTERN.matcher(intentParameters.roomId).matches())
        {
            appRtcClient = WebSocketRTCClient(this)
        }
        else
        {
            Log.i(TAG, "Using DirectRTCClient because room name looks like an IP.")
            appRtcClient = DirectRTCClient(this)
        }
        // Create connection parameters.
        roomConnectionParameters = AppRTCClient.RoomConnectionParameters(
            roomUri.toString(), intentParameters.roomId,
            intentParameters.loopback, intentParameters.urlParameters)

        // For command line execution run connection for <runTimeMs> and exit.
        if (commandLineRun && runTimeMs > 0)
        {
            (Handler()).postDelayed({ disconnect() }, runTimeMs.toLong())
        }

        if (screencaptureEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startScreenCapture() }
        else {
            startCall()
        }
    }

    private fun handlePermissions() {
        val canAccessCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val canRecordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if (!canAccessCamera || !canRecordAudio) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), CAMERA_AUDIO_PERMISSION_REQUEST)
        } else {
            prepareCall()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.w(TAG, "onRequestPermissionsResult: $requestCode $permissions $grantResults")
        when (requestCode) {
            CAMERA_AUDIO_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    arePermissionsGranted = true
//                    startVideoSession()
                    prepareCall()
                } else {
                    finish()
                }
                return
            }
        }
    }

    private fun initFragments(intent: Intent) {
        callFragment = CallFragment()
        hudFragment = HudFragment()
        // Create CPU monitor
        cpuMonitor = CpuMonitor(this)
        hudFragment!!.setCpuMonitor(cpuMonitor!!)

        // Send intent arguments to fragments.
        callFragment!!.arguments = intent.extras
        hudFragment!!.arguments = intent.extras
        // Activate call and HUD fragments and start the call.
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.call_fragment_container, callFragment!!)
        ft.add(R.id.hud_fragment_container, hudFragment!!)
        ft.commit()
    }

    @TargetApi(17)
    private fun getDisplayMetrics(): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        val windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        return displayMetrics
    }

    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(19)
    private fun getSystemUiVisibility(): Int {
        var flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags = flags or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
        return flags
    }

    @TargetApi(21)
    private fun startScreenCapture() {
        val mediaProjectionManager = application.getSystemService(
            Context.MEDIA_PROJECTION_SERVICE
        ) as MediaProjectionManager
        startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode != CAPTURE_PERMISSION_REQUEST_CODE)
            return
        mediaProjectionPermissionResultCode = resultCode
        mediaProjectionPermissionResultData = data
        startCall()
    }

    private fun useCamera2(): Boolean {
        return Camera2Enumerator.isSupported(this) && intent.getBooleanExtra(EXTRA_CAMERA2, true)
    }

    private fun captureToTexture(): Boolean {
        return intent.getBooleanExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, false)
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.")
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.")
                val videoCapturer = enumerator.createCapturer(deviceName, null)

                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.")
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.")
                val videoCapturer = enumerator.createCapturer(deviceName, null)

                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        return null
    }

    @TargetApi(21)
    private fun createScreenCapturer(): VideoCapturer? {
        if (mediaProjectionPermissionResultCode != RESULT_OK) {
            reportError("User didn't give permission to capture the screen.")
            return null
        }
        return ScreenCapturerAndroid(
            mediaProjectionPermissionResultData, object : MediaProjection.Callback() {
                override fun onStop() {
                    reportError("User revoked permission to capture the screen.")
                }
            })
    }

    public override fun onStop() {
        super.onStop()
        Log.d(TAG, "aChub onStop()")
        activityRunning = false
        // Don't stop the video when using screencapture to allow user to show other apps to the remote
        // end.
        peerConnectionClient.stopVideoSource()
        cpuMonitor!!.pause()
    }

    public override fun onStart() {
        super.onStart()
        activityRunning = true
        // Video is not paused for screencapture. See onPause.
        peerConnectionClient.startVideoSource()
        cpuMonitor!!.resume()
    }

    override fun onDestroy() {
        Thread.setDefaultUncaughtExceptionHandler(null)
        disconnect()
        if (logToast != null) {
            logToast!!.cancel()
        }
        activityRunning = false
        rootEglBase!!.release()
        super.onDestroy()
    }

    // CallFragment.OnCallEvents interface implementation.
    override fun onCallHangUp() {
        disconnect()
    }

    override fun onCameraSwitch() {
        peerConnectionClient.switchCamera()
    }


    override fun onVideoScalingSwitch(scalingType: RendererCommon.ScalingType?) {
        fullscreenRenderer!!.setScalingType(scalingType)
    }

    override fun onCaptureFormatChange(width: Int, height: Int, framerate: Int) {
        peerConnectionClient.changeCaptureFormat(width, height, framerate)
    }

    override fun onToggleMic(): Boolean {
        micEnabled = !micEnabled
        peerConnectionClient.setAudioEnabled(micEnabled)
        return micEnabled
    }

    private fun toggleCallControlFragmentVisibility() {
        if (!iceConnected || !callFragment!!.isAdded()) {
            return
        }
        // Show/hide call control fragment
        callControlFragmentVisible = !callControlFragmentVisible
        val ft = supportFragmentManager.beginTransaction()
        if (callControlFragmentVisible) {
            ft.show(callFragment!!)
            ft.show(hudFragment!!)
        } else {
            ft.hide(callFragment!!)
            ft.hide(hudFragment!!)
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.commit()
    }

    private fun startCall() {
        Log.d(TAG, "aChub startCall 1")
        if (appRtcClient == null) {
            Log.e(TAG, "AppRTC client is not allocated for a call.")
            return
        }
        Log.d(TAG, "aChub startCall 2")
        callStartedTimeMs = System.currentTimeMillis()
        Log.d(TAG, "aChub startCall 3")
        // Start room connection.
        logAndToast(getString(R.string.connecting_to, roomConnectionParameters!!.roomUrl))
        Log.d(TAG, "aChub startCall 4")
        appRtcClient!!.connectToRoom(roomConnectionParameters)
        Log.d(TAG, "aChub startCall 5")
        // Create and audio manager that will take care of audio routing,
        // audio modes, audio device enumeration etc.
        Log.d(TAG, "aChub startCall 6")
        audioManager = AppRTCAudioManager.create(applicationContext)
        // Store existing audio settings and change audio mode to
        // MODE_IN_COMMUNICATION for best possible VoIP performance.
        Log.d(TAG, "Starting the audio manager...")
        Log.d(TAG, "aChub startCall 7")
        audioManager!!.start { audioDevice, availableAudioDevices ->
            // This method will be called each time the number of available audio
            // devices has changed.
            onAudioManagerDevicesChanged(audioDevice, availableAudioDevices)
        }
        Log.d(TAG, "aChub startCall 7")
    }

    private fun callConnected() {
        val delta = System.currentTimeMillis() - callStartedTimeMs
        Log.i(TAG, "Call connected: delay=" + delta + "ms")
        if (isError) {
            Log.w(TAG, "Call is connected in closed or error state")
            return
        }
         // Enable statistics callback.
        peerConnectionClient.enableStatsEvents(true, STAT_CALLBACK_PERIOD)
        setSwappedFeeds(false /* isSwappedFeeds */)
    }

  // This method is called when the audio manager reports audio device change,
  // e.g. from wired headset to speakerphone.
  private fun onAudioManagerDevicesChanged(
      device:AppRTCAudioManager.AudioDevice,
      availableDevices:Set<AppRTCAudioManager.AudioDevice>) {
        Log.d(TAG, ("onAudioManagerDevicesChanged: " + availableDevices + ", "
        + "selected: " + device))
         // TODO(henrika): add callback handler.
  }

  // Disconnect from remote resources, dispose of local resources, and exit.
  private fun disconnect() {
    activityRunning = false
    remoteProxyRenderer.setTarget(null)
    localProxyRenderer.setTarget(null)
    if (appRtcClient != null) { appRtcClient!!.disconnectFromRoom()
    appRtcClient = null }
      peerConnectionClient.close()
      pipRenderer.release()
    if (videoFileRenderer != null) { videoFileRenderer!!.release()
    videoFileRenderer = null }
    if (fullscreenRenderer != null) { fullscreenRenderer!!.release()
    fullscreenRenderer = null }
    if (audioManager != null) { audioManager!!.stop()
    audioManager = null }
    if (iceConnected && !isError) { setResult(RESULT_OK) }
    else {setResult(RESULT_CANCELED) }
    finish()
  }

  private fun disconnectWithErrorMessage(errorMessage:String) {
    if (commandLineRun || !activityRunning) {
        Log.e(TAG, "Critical error: $errorMessage")
        disconnect() }
    else {
        AlertDialog.Builder(this)
        .setTitle(getText(R.string.channel_error_title))
        .setMessage(errorMessage)
        .setCancelable(false)
        .setNeutralButton(R.string.ok) {
                dialog, id -> dialog.cancel()
            disconnect() }
        .create().show() }
  }

  // Log |msg| and Toast about it.
  private fun logAndToast(msg:String) {
    Log.d(TAG, msg)
    if (logToast != null)
    {
        logToast!!.cancel()
    }
    logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
    logToast!!.show()
  }

  private fun reportError(description:String) {
    runOnUiThread {
        if (!isError) { isError = true
            disconnectWithErrorMessage(description)
        }
    }
  }

  @SuppressLint("ObsoleteSdkInt")
  private fun createVideoCapturer():VideoCapturer? {
    val videoCapturer:VideoCapturer?
    if (intentParameters.videoFileAsCamera != null) {
        try {
            videoCapturer = FileVideoCapturer(intentParameters.videoFileAsCamera)
        }
        catch (e:IOException) {
        reportError("Failed to open video file for emulated camera")
        return null } }
    else if (screencaptureEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        return createScreenCapturer()
    }
    else if (useCamera2()) {
        if (!captureToTexture()) {
            reportError(getString(R.string.camera2_texture_only_error))
            return null }

    Logging.d(TAG, "Creating capturer using camera2 API.")
    videoCapturer = createCameraCapturer(Camera2Enumerator(this)) }
    else {
        Logging.d(TAG, "Creating capturer using camera1 API.")
        videoCapturer = createCameraCapturer(Camera1Enumerator(captureToTexture())) }
    if (videoCapturer == null) {
        reportError("Failed to open camera")
        return null
    }
    return videoCapturer
  }

  private fun setSwappedFeeds(isSwappedFeeds:Boolean) {
    Logging.d(TAG, "setSwappedFeeds: $isSwappedFeeds")
    this.isSwappedFeeds = isSwappedFeeds
    localProxyRenderer.setTarget(if (isSwappedFeeds) fullscreenRenderer else pipRenderer)
    remoteProxyRenderer.setTarget(if (isSwappedFeeds) pipRenderer else fullscreenRenderer)
    fullscreenRenderer!!.setMirror(isSwappedFeeds)
    pipRenderer.setMirror(!isSwappedFeeds)
  }

  // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
  // All callbacks are invoked from websocket signaling looper thread and
  // are routed to UI thread.
  private fun onConnectedToRoomInternal(params:AppRTCClient.SignalingParameters) {
    val delta = System.currentTimeMillis() - callStartedTimeMs

    signalingParameters = params
    logAndToast("Creating peer connection, delay=" + delta + "ms")
    var videoCapturer:VideoCapturer? = null
    if (peerConnectionParameters.videoCallEnabled) {
        videoCapturer = createVideoCapturer() }
    peerConnectionClient.createPeerConnection(
        rootEglBase!!.eglBaseContext, localProxyRenderer,
    remoteRenderers, videoCapturer, signalingParameters)

    if (signalingParameters!!.initiator) {
        logAndToast("Creating OFFER...")
         // Create offer. Offer SDP will be sent to answering client in
              // PeerConnectionEvents.onLocalDescription event.
              peerConnectionClient.createOffer()
    }
    else{
        if (params.offerSdp != null) {
            peerConnectionClient.setRemoteDescription(params.offerSdp)
            logAndToast("Creating ANSWER...")
             // Create answer. Answer SDP will be sent to offering client in
                    // PeerConnectionEvents.onLocalDescription event.
                    peerConnectionClient.createAnswer()
        }
        if (params.iceCandidates != null) {
         // Add remote ICE candidates from room.
                for (iceCandidate in params.iceCandidates) {
                    peerConnectionClient.addRemoteIceCandidate(iceCandidate)
                }
        }
    }
  }

  override fun onConnectedToRoom(params:AppRTCClient.SignalingParameters) {
    runOnUiThread { onConnectedToRoomInternal(params) } }

  override fun onRemoteDescription(sdp:SessionDescription) {
    val delta = System.currentTimeMillis() - callStartedTimeMs
    runOnUiThread {
        logAndToast("Received remote " + sdp.type + ", delay=" + delta + "ms")
        peerConnectionClient.setRemoteDescription(sdp)
        if (!signalingParameters!!.initiator) {
            logAndToast("Creating ANSWER...")
            // Create answer. Answer SDP will be sent to offering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient.createAnswer()
        }
    }
  }

  override fun onRemoteIceCandidate(candidate:IceCandidate) {
    runOnUiThread {
        peerConnectionClient.addRemoteIceCandidate(candidate)
    }
  }

  override fun onRemoteIceCandidatesRemoved(candidates:Array<IceCandidate>) {
    runOnUiThread {
        peerConnectionClient.removeRemoteIceCandidates(candidates)
    }
  }

  override fun onChannelClose() {
    runOnUiThread {
        logAndToast("Remote end hung up; dropping PeerConnection")
        disconnect()
    }
  }

  override fun onChannelError(description:String) {
      reportError(description)
  }

  // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
  // Send local peer connection SDP and ICE candidates to remote party.
  // All callbacks are invoked from peer connection client looper thread and
  // are routed to UI thread.
  override fun onLocalDescription(sdp:SessionDescription) {
    val delta = System.currentTimeMillis() - callStartedTimeMs
    runOnUiThread {
        if (appRtcClient != null) {
            logAndToast("Sending " + sdp.type + ", delay=" + delta + "ms")
            if (signalingParameters!!.initiator) {
                appRtcClient!!.sendOfferSdp(sdp)
            } else {
                appRtcClient!!.sendAnswerSdp(sdp)
            }
        }
        if (peerConnectionParameters.videoMaxBitrate > 0) {
            Log.d(TAG, "Set video maximum bitrate: " + peerConnectionParameters.videoMaxBitrate)
            peerConnectionClient.setVideoMaxBitrate(peerConnectionParameters.videoMaxBitrate)
        }
    }
  }

  override fun onIceCandidate(candidate:IceCandidate) {
    runOnUiThread {
        if (appRtcClient != null) {
            appRtcClient!!.sendLocalIceCandidate(candidate)
        }
    }
  }

  override fun onIceCandidatesRemoved(candidates:Array<IceCandidate>) {
    runOnUiThread {
        if (appRtcClient != null) {
            appRtcClient!!.sendLocalIceCandidateRemovals(candidates)
        }
    }
  }

  override fun onIceConnected() {
    val delta = System.currentTimeMillis() - callStartedTimeMs
    runOnUiThread {
        logAndToast("ICE connected, delay=" + delta + "ms")
        iceConnected = true
        callConnected()
    }
  }

  override fun onIceDisconnected() {
    runOnUiThread {
        logAndToast("ICE disconnected")
        iceConnected = false
        disconnect()
    }
  }

  override fun onPeerConnectionClosed() {}

  override fun onPeerConnectionStatsReady(reports:Array<StatsReport>) {
    runOnUiThread {
    if (!isError && iceConnected) {
        hudFragment!!.updateEncoderStatistics(reports)
    }
    }
  }

  override fun onPeerConnectionError(description:String) {
    reportError(description)
  }

}