package com.inhelp.instagram.panorama.view.save

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.dialogs.main.DialogManager
import com.inhelp.extension.saveBitmap
import com.inhelp.instagram.panorama.data.TransferObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PanoramaSavePresenter(val context: Context, val transferObject: TransferObject): BaseMvpPresenterImpl<PanoramaSaveView>() {

    val panoramaImages: MutableList<Bitmap>
        get() = transferObject.images

    fun pressSave() = CoroutineScope(Main).launch {
        val dialog = DialogManager.createLoad{}
        loadImage()
        view?.setDownloadDone()
        dialog.closeDialog()
    }

    private suspend fun loadImage() = withContext(Dispatchers.IO) {
        transferObject.images.reversed().forEachIndexed { index, bitmap ->
            context.saveBitmap(bitmap, "${index}_")
        }
    }
}