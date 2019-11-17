package com.example.myapplication6.di.components

//import com.example.myapplication6.di.modules.APPModule
//import com.example.myapplication6.di.builder.ActivityBuilderModule
//
//import android.app.Application
//
//import javax.inject.Singleton
//
//import dagger.BindsInstance
//import dagger.Component
//import dagger.android.AndroidInjectionModule
//import dagger.android.AndroidInjector
//
//@Singleton
//@Component(modules = [APPModule::class, AndroidInjectionModule::class, ActivityBuilderModule::class])
//interface AppComponent : AndroidInjector<WeatherApp> {
//
//    @Component.Builder
//    interface Builder {
//        @BindsInstance
//        fun application(application: Application): Builder
//
//        fun build(): AppComponent
//
////        fun factory(): MyWorkerFactory
//    }
//
//    override fun inject(weatherApp: WeatherApp)
//}