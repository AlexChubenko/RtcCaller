package com.example.myapplication6.di.modules

import android.util.Log
import com.rtccaller.di.modules.CallModule
import javax.inject.Singleton
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
@Module(includes = [ViewModelModule::class, CallModule::class])
class APPModule {

    companion object {
        private val TAG = APPModule::class.java.simpleName
        private val BASE_URL = "https://openweathermap.org/"
    }

    @Provides
    @Singleton
    fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        Log.d(TAG, "getPeerConnectionParameters()")
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }


    @Provides
    @Singleton
    fun getRetrofit(okHttpClient: OkHttpClient): Retrofit {
        Log.d(TAG, "getRetrofit()")
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @Provides
    @Singleton
    fun getOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        Log.d(TAG, "getOkHttpClient()")
        return OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor).build()
    }

}
