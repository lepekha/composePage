package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterSepiaTone: ImageFilter() {
    override val id: Int = 2
    override val nameResId: Int = R.string.module_image_filter_sepia
    override val iconResId: Int = R.drawable.module_image_filter_ic_sepia

    override val filter by lazy { GPUImageSepiaToneFilter() }

    override val params by lazy {
        mutableListOf(
            FilterParam(R.string.module_image_filter_intensity, 0.0f, 1.0f, 0.0f) {
                filter.setIntensity(it)
            }
        )
    }

    override fun create(params: Array<Float>) = GPUImageSepiaToneFilter(params[0])
}
