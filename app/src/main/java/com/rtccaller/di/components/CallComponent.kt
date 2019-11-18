package com.rtccaller.di.components


import com.rtccaller.di.modules.CallModule
import com.rtccaller.call.CallActivity2
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import java.lang.annotation.Documented
import javax.inject.Scope
//
@Scope
@Documented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ActivityScope

//@ActivityScope
//@Component(modules = [CallModule::class])
//interface CallComponent : AndroidInjector<CallActivity2>{
//
//    @Component.Builder
//    interface Builder {
//        @BindsInstance
//        fun callActivity2(callActivity2: CallActivity2): Builder
//
//        fun build(): CallComponent
//
////        fun factory(): MyWorkerFactory
//    }
//
//    override fun inject(callActivity2: CallActivity2)
//}