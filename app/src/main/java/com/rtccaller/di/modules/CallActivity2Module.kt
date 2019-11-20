package com.rtccaller.di.modules

import com.rtccaller.displays.call.CallActivity2
import com.rtccaller.di.scopes.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CallActivity2Module {

    companion object {
        private val TAG = CallActivity2Module::class.java.simpleName
    }

    @ActivityScope
    @ContributesAndroidInjector(modules = [CallModule::class])
    abstract fun callActivity2(): CallActivity2

}
