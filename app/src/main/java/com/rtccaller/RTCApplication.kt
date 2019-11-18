package com.rtccaller

import android.app.Activity
import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import javax.inject.Inject
import androidx.core.content.ContextCompat.getSystemService
import com.example.myapplication6.di.components.DaggerAppComponent
import dagger.android.*


class RTCApplication :Application(), HasAndroidInjector {

//    private val appComponent: AndroidInjector<RTCApplication> by lazy {
//        DaggerAppComponent
//            .builder()
//            .create(this)
//    }
//
//    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
//        return appComponent
//    }


    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector() = activityDispatchingAndroidInjector


    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.create().inject(this)
    }

//
//    @Inject
//    var activityDispatchingAndroidInjector: DispatchingAndroidInjector<AppCompatActivity>? = null




}