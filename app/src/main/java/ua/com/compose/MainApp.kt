package ua.com.composeimport android.app.Applicationimport android.os.StrictModeimport ua.com.compose.instagram_planer.di.instagramPlanerModuleimport ua.com.compose.image_crop.di.imageCropModuleimport ua.com.compose.other_tags.di.tagsModuleimport ua.com.compose.gallery.di.galleryModuleimport ua.com.compose.instagram_no_crop.di.instagramCropModuleimport ua.com.compose.instagram_grid.di.instagramGridModuleimport ua.com.compose.instagram_panorama.di.instagamPanoramaModuleimport ua.com.compose.other_text_style.di.textStyleModuleimport org.koin.android.ext.koin.androidContextimport org.koin.core.context.startKoinimport ua.com.compose.di.appModuleimport ua.com.compose.image_compress.di.imageCompressModuleimport ua.com.compose.image_rotate.di.rotateModuleclass MainApp : Application() {    override fun onCreate() {        super.onCreate()        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())        startKoin{            androidContext(this@MainApp)            modules(listOf(                    appModule,                    imageCropModule,                    rotateModule,                    imageCompressModule,                    textStyleModule,                    tagsModule,                    galleryModule,                    instagamPanoramaModule,                    instagramGridModule,                    instagramCropModule,                    instagramPlanerModule            ))        }    }}