package ua.com.compose.image_compress.main

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.core.view.drawToBitmap
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.slider.Slider
import kotlinx.android.synthetic.main.module_image_compress_fragment_compress_main.*
import ua.com.compose.extension.*
import ua.com.compose.image_compress.di.Scope
import ua.com.compose.mvp.BaseMvpFragment
import ua.com.compose.gallery.main.FragmentGallery
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.image_compress.R
import ua.com.compose.image_maker.FrameImageView


class ImageCompressFragment : BaseMvpFragment<ImageCompressView, ImageCompressPresenter>(), ImageCompressView {

    companion object {

        const val REQUEST_KEY = "REQUEST_KEY_IMAGE"

        const val BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI"

        fun newInstance(uri: Uri?): ImageCompressFragment {
            return ImageCompressFragment().apply {
                arguments = bundleOf(
                    BUNDLE_KEY_IMAGE_URI to uri
                )
            }
        }
    }

    override val presenter: ImageCompressPresenter by lazy { Scope.IMAGE_COMPRESS.get() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_image_compress_fragment_compress_main, container, false)
    }

    private val btnDone by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_done) {
            presenter.pressDone()
        }
    }

    private val btnGallery by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_gallery) {
            openGallery()
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>().apply {
            this.add(btnGallery)
            this.add(btnDone)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.module_image_compress_title))

        setFragmentResultListener(FragmentGallery.REQUEST_KEY) { _, bundle ->
            presenter.onAddImage((bundle.getSerializable(FragmentGallery.BUNDLE_KEY_IMAGES) as List<*>).filterIsInstance<Uri>())
        }

        sbSize.addOnChangeListener(Slider.OnChangeListener { slider, value, fromUser ->
            txtSize.context.vibrate(EVibrate.SLIDER)
            presenter.onSizeChange(progress = value.toInt())
        })

        sbQuality.addOnChangeListener(Slider.OnChangeListener { slider, value, fromUser ->
            txtSize.context.vibrate(EVibrate.SLIDER)
            presenter.onQualityChange(progress = value.toInt())
        })

        val inputUri = arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri

        presenter.onCreate(uri = inputUri)
    }

    override fun setQualityValue(value: String){
        txtQuality.text = buildString {
            append(requireContext().getString(R.string.module_image_compress_quality))
            append(" ")
            append(value)
        }

        txtQuality.setColorOfSubstring(value, requireContext().getColorFromAttr(R.attr.color_6))
    }

    override fun setSizeValue(value: String){
        txtSize.text = buildString {
            append(requireContext().getString(R.string.module_image_compress_size))
            append(" ")
            append(value)
        }

        txtSize.setColorOfSubstring(value, requireContext().getColorFromAttr(R.attr.color_6))
    }

    override fun openGallery() {
        FragmentGallery.show(fm = getCurrentActivity().supportFragmentManager, isMultiSelect = false)
    }

    override fun saveToResult(uri: Uri) {
        setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY_IMAGE_URI to uri))
    }

    override fun setImage(uri: Uri) {
        Glide.with(requireContext().applicationContext).load(uri).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).thumbnail(0.1f).into(imgView)

        Glide.with(requireContext().applicationContext)
                .asBitmap()
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(object: CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        presenter.onResourceLoad(resource)
                    }
                })

    }

    override fun setImage(bmp: Bitmap) {
        imgView.setImageBitmap(bmp)
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.IMAGE_COMPRESS.close()
    }
}