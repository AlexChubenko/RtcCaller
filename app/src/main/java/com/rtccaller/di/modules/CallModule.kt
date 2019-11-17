package com.example.myapplication6.di.modules

import android.content.Intent
import android.util.Log
import com.example.myapplication6.di.components.ActivityScope
import com.rtccaller.call.CallActivity2
import com.rtccaller.call.CallIntentParameters
import com.rtccaller.utils.PeerConnectionClient
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.webrtc.PeerConnectionFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class CallModule {

    companion object {
        private val TAG = CallModule::class.java.simpleName
        private val BASE_URL = "https://openweathermap.org/"
    }

    @Provides
    @ActivityScope
    fun getPeerConnectionParameters(intentParameters: CallIntentParameters, peerConnectionParameters: PeerConnectionClient.DataChannelParameters)
            = PeerConnectionClient.PeerConnectionParameters(intentParameters.videoCall,
        intentParameters.loopback, intentParameters.tracing, intentParameters.videoWidth,
        intentParameters.videoHeight, intentParameters.videoFps, intentParameters.videoBitrate,
        intentParameters.videocodec, intentParameters.hwCodecEnbled,
        intentParameters.flexfecEnabled, intentParameters.audioBitrade,
        intentParameters.audiocodec, intentParameters.noAudioProcessingEnabled,
        intentParameters.aecdumpEnbled, intentParameters.openslesEnabled,
        intentParameters.disableBuiltInAec, intentParameters.disableBuiltInAgc,
        intentParameters.disableBuiltInNc, intentParameters.enableLevelControl,
        intentParameters.disableWebrtcAgcAndHpf, peerConnectionParameters)

    @Provides
    @ActivityScope
    fun getIntentParameters(activity: CallActivity2): CallIntentParameters {
        Log.d(TAG, "getDataChannelParameters()")
        return CallIntentParameters(activity.intent)
    }

    @Provides
    @ActivityScope
    fun getDataChannelParameters(intentParameters: CallIntentParameters): PeerConnectionClient.DataChannelParameters {
        Log.d(TAG, "getDataChannelParameters()")
        return PeerConnectionClient.DataChannelParameters(intentParameters.ordered,
            intentParameters.maxRentransmitsMs, intentParameters.maxRentransmits,
            intentParameters.protocol, intentParameters.negotiated, intentParameters.id
        )
    }

    @Provides
    @ActivityScope
    fun getPeerConnectionClient(
        activity: CallActivity2,
        peerConnectionParameters: PeerConnectionClient.PeerConnectionParameters,
        intentParameters: CallIntentParameters
    ): PeerConnectionClient {

        val peerConnectionClient = PeerConnectionClient.getInstance()
        if (intentParameters.loopback) {
            val options = PeerConnectionFactory.Options()
            options.networkIgnoreMask = 0
            peerConnectionClient.setPeerConnectionFactoryOptions(options)
        }
        peerConnectionClient.createPeerConnectionFactory(
            activity.getApplicationContext(), peerConnectionParameters, activity
        )
        return peerConnectionClient
    }


}
