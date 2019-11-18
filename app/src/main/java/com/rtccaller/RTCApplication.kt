package com.rtccaller

import android.app.Application
import com.rtccaller.di.components.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject


class RTCApplication :DaggerApplication() {
//    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }


    private val appComponent: AndroidInjector<RTCApplication> by lazy {
        DaggerAppComponent
            .builder()
            .create(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return appComponent
    }


//    @Inject
//    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Any>
//
//    override fun androidInjector() = activityDispatchingAndroidInjector
//
//
//    override fun onCreate() {
//        super.onCreate()
//        DaggerAppComponent.create().inject(this)
//    }

//
//    @Inject
//    var activityDispatchingAndroidInjector: DispatchingAndroidInjector<AppCompatActivity>? = null




}