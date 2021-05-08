package com.inhelp.instagram.view.main

import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.instagram.data.*


class InstagramCropPresenter: BaseMvpPresenterImpl<InstagramCropView>() {

    private var currentUri: Uri? = null
    internal val images = mutableListOf<Bitmap>()
    var eNoCrop = ENoCrop.ONE_TO_ONE

    fun pressCrop(bitmaps: List<Bitmap>){
        images.clear()
        images.addAll(bitmaps)
        view?.navigateToCropSave()
    }

    fun onAddImage(uris: List<Uri>){
        val currentUri = uris.firstOrNull() ?: this.currentUri
        when{
            (currentUri != null) -> {
                this.currentUri = currentUri
                view?.setImage(currentUri)
            }
            else -> {
                view?.backToMain()
                return
            }
        }
    }

    fun onCreate(){
        view?.initNoCrop()
        val currentUri = this.currentUri
        if(currentUri != null) {
            view?.setImage(currentUri)
        }else{
            view?.openGallery()
        }
    }

    fun onResourceLoad() {
        view?.setSelectedTab(position = eNoCrop.ordinal)
        view?.createCropOverlay(eNoCrop.ratio, isGrid = false)
    }

    fun onTabSelect(position: Int) {
        eNoCrop = ENoCrop.values()[position]
        view?.createCropOverlay(eNoCrop.ratio, isGrid = false)
    }

}