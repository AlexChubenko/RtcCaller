package com.rtccaller.di.components

import com.rtccaller.RTCApplication
import com.rtccaller.di.modules.CallActivity2Module
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ AndroidSupportInjectionModule::class, CallActivity2Module::class])
interface AppComponent: AndroidInjector<RTCApplication>{
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<RTCApplication>()
}