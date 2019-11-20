package com.example.myapplication6.di.modules

import com.rtccaller.di.modules.CallModule
import dagger.Module

@Module(includes = [CallModule::class])
class APPModule {

    companion object {
        private val TAG = APPModule::class.java.simpleName
    }

}
