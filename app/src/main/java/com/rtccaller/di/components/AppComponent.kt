package com.example.myapplication6.di.components

import com.example.myapplication6.di.modules.CallModule
import com.rtccaller.RTCApplication
import com.rtccaller.call.CallActivity2
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ AndroidSupportInjectionModule::class, CallModule::class])
interface AppComponent: AndroidInjector<RTCApplication>
//{
//    @Component.Builder
//    interface Builder {
//        @BindsInstance
//        fun rtcApplication(rtcApplication: RTCApplication): AppComponent.Builder
//
//        fun build(): AppComponent
//    }
//}