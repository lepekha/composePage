package com.inhelpimport android.app.Applicationimport android.os.StrictModeimport com.dali.instagram.planer.di.instagramPlanerModuleimport com.dali.rotate.di.rotateModuleimport com.inhelp.crop.di.cropModuleimport com.inhelp.di.appModuleimport com.inhelp.dialogs.di.tagsModuleimport com.inhelp.gallery.di.galleryModuleimport com.inhelp.text_style.di.textStyleModuleimport org.koin.android.ext.koin.androidContextimport org.koin.core.context.startKoinclass MainApp : Application() {    override fun onCreate() {        super.onCreate()        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())        startKoin{            androidContext(this@MainApp)            modules(listOf(                    appModule,                    cropModule,                    rotateModule,                    textStyleModule,                    tagsModule,                    galleryModule,//                    noCropModule,                    instagramPlanerModule,//                    gridModule,//                    panoramaModule            ))        }    }}