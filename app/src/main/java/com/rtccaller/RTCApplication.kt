package com.rtccaller

import com.rtccaller.di.components.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class RTCApplication :DaggerApplication() {

    private val appComponent: AndroidInjector<RTCApplication> by lazy {
        DaggerAppComponent.builder().create(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return appComponent
    }

}