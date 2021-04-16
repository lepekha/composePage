package com.inhelp.gallery.main.content

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.ColorFilter
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.inhelp.extension.EVibrate
import com.inhelp.extension.dp
import com.inhelp.extension.getColorFromAttr
import com.inhelp.extension.vibrate
import com.inhelp.gallery.R
import kotlinx.android.synthetic.main.element_gallery_images.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GalleryRvAdapter(val context: Context, val images: MutableList<Uri>, val selectedImage: MutableList<Uri>, val onPress: (value: Uri, isLongPress: Boolean) -> Unit, val onUpdateBadge: () -> Unit) : RecyclerView.Adapter<GalleryRvAdapter.ViewHolder>() {

    companion object {
        const val CHANGE_ITEM = 0
        const val CHANGE_BADGE = 1
        const val CHANGE_CLEAR_SELECT = 2
    }


    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_gallery_images, parent, false)).apply {
            this.imgView.setOnClickListener {
                this.imgView.context.vibrate(type = EVibrate.BUTTON)
                onPress(images[adapterPosition], false)
                animateScale(this, selectedImage.contains(images[adapterPosition]))
            }

            this.imgView.setOnLongClickListener {
                this.imgView.context.vibrate(type = EVibrate.BUTTON_LONG)
                onPress(images[adapterPosition], true)
                animateScale(this, selectedImage.contains(images[adapterPosition]))
                true
            }
        }
    }

    private fun animateScale(holder: ViewHolder, isSelected: Boolean){
        val scale = if(isSelected){
            holder.badge.text = (selectedImage.indexOf(images[holder.adapterPosition]) + 1).toString()
            scaleAnimation(holder, 1f)
            0.8f
        }else{
            scaleAnimation(holder, 0f)
            1f
        }

        val scaleDownX = ObjectAnimator.ofFloat(holder.imgView, "scaleX", scale)
        val scaleDownY = ObjectAnimator.ofFloat(holder.imgView, "scaleY", scale)
        scaleDownX.duration = 200
        scaleDownY.duration = 200

        val scaleDown = AnimatorSet()
        scaleDown.play(scaleDownX).with(scaleDownY)
        scaleDown.start()
    }

    private fun scaleAnimation(holder: ViewHolder, scale: Float){
        val scaleDownX = ObjectAnimator.ofFloat(holder.badge, "scaleX", scale)
        val scaleDownY = ObjectAnimator.ofFloat(holder.badge, "scaleY", scale)
        scaleDownX.duration = 200
        scaleDownY.duration = 200

        val scaleDown = AnimatorSet()
        scaleDown.play(scaleDownX).with(scaleDownY)
        scaleDown.doOnEnd {
            onUpdateBadge()
        }
        scaleDown.start()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.imgView.context).load(images[position]).centerInside().thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imgView)
        holder.badge.text = (selectedImage.indexOf(images[holder.adapterPosition]) + 1).toString()
        if(selectedImage.contains(images[position])){
            holder.imgView.scaleX = 0.7f
            holder.imgView.scaleY = 0.7f
            holder.badge.scaleX = 1f
            holder.badge.scaleY = 1f
        }else{
            holder.imgView.scaleX = 1f
            holder.imgView.scaleY = 1f
            holder.badge.scaleX = 0f
            holder.badge.scaleY = 0f
        }

//        holder.imgView.load(images[position]) {
//            memoryCachePolicy(CachePolicy.READ_ONLY)
//            placeholderMemoryCacheKey(images[position].toString())
//            crossfade(true)
//            scale(Scale.FIT)
//        }
    }

    private fun loadImage(imageView: ImageView, position: Int) = CoroutineScope(Dispatchers.Main).launch {
        val request = ImageRequest.Builder(imageView.context)
                .data(images[position])
                .allowRgb565(true)
                .scale(Scale.FIT)
                .size(50.dp.toInt())
                .memoryCachePolicy(CachePolicy.DISABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
//                .placeholderMemoryCacheKey(images[position].toString())
                .target(
                        onSuccess = {
                            imageView.setImageDrawable(it)
                        }
                )
                .build()

        withContext(Dispatchers.IO) {
            imageView.context.imageLoader.execute(request)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            when (payloads[0]) {
                CHANGE_BADGE -> {
                    holder.badge.text = (selectedImage.indexOf(images[holder.adapterPosition]) + 1).toString()
                }
                CHANGE_CLEAR_SELECT -> {
                    animateScale(holder, selectedImage.contains(images[position]))
                }
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgView: ImageView = view.imgView
        val badge: TextView = view.badge
    }
}
