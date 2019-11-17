package com.example.myapplication6.di.modules

//import android.app.Application
//import android.content.Context
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.sqlite.db.SupportSQLiteDatabase
//import androidx.work.OneTimeWorkRequestBuilder
//import dagger.Module
//import dagger.Provides
//import javax.inject.Singleton
//
//@Module
//class DbModule {
//    @Provides
//    @Singleton
//    fun providesDatabase(application: Application): AppDatabase {
//        return Room.databaseBuilder(application, AppDatabase::class.java, AppDatabase.NAME)
//                .addCallback(object : RoomDatabase.Callback() {
//                    override fun onCreate(db: SupportSQLiteDatabase) {
//                        val workReq = OneTimeWorkRequestBuilder<CityListPopulateDbWorker>().build()
//                    }
//
//                    override fun onOpen(db: SupportSQLiteDatabase) {
//
//                    }
//                }).fallbackToDestructiveMigration()
//                .allowMainThreadQueries()
//                .build()
//    }
//
////    @Provides
////    @Singleton
////    fun provideAppDatabase(application: Application) = AppDatabase.build(application)
//
//    @Provides
//    @Singleton
//    fun provideMovieRequestDao(appDatabase: AppDatabase): CitiesDao = appDatabase.citiesDao()
//
//    @Provides
//    @Singleton
//    fun provideMovieDetailsRoomDao(appDatabase: AppDatabase) = appDatabase.movieDetailsDao()
//}