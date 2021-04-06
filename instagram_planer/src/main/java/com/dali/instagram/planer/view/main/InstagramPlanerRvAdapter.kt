package com.dali.instagram.planer.view.main

import android.content.ClipData
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.view.*
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dali.instagram.planer.R
import com.inhelp.extension.animateScale
import kotlinx.android.synthetic.main.element_instagram_planer_image.view.*


class InstagramPlanerRvAdapter(val images: MutableList<Uri>, val onPress: (position: Int) -> Unit, val onChange: (oldPosition: Int, newPosition: Int) -> Unit) : RecyclerView.Adapter<InstagramPlanerRvAdapter.ViewHolder>() {

    companion object {
        private const val SCALE_DRAG_ENTERED = 0.75f
        private const val SCALE_DRAG_EXITED = 1f

        const val CHANGE_ITEM_POSITION = 0
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_instagram_planer_image, parent, false)).apply {
            this.imgView.setOnLongClickListener {
                val data = ClipData.newPlainText("NAME", adapterPosition.toString())
                val shadowBuilder = View.DragShadowBuilder(it)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    it.startDragAndDrop(data, shadowBuilder, it, 0)
                } else {
                    it.startDrag(data, shadowBuilder, it, 0)
                }
            }


            this.imgView.setOnDragListener { _, dragEvent ->
                when (dragEvent.action) {
                    DragEvent.ACTION_DRAG_ENTERED -> {
                        this.imgView.animateScale(toScale = SCALE_DRAG_ENTERED)
                    }
                    DragEvent.ACTION_DRAG_EXITED -> {
                        this.imgView.animateScale(toScale = SCALE_DRAG_EXITED)
                    }
                    DragEvent.ACTION_DROP -> {
                        val oldPosition = dragEvent.clipData.getItemAt(0).text.toString().toInt()
                        val newPosition = adapterPosition
                        if(oldPosition == newPosition){
                            this.imgView.animateScale(toScale = SCALE_DRAG_EXITED)
                        }else{
                            onChange(oldPosition, newPosition)
                        }
                    }
                }
                true
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.imgView.context).load(images[position]).centerInside().thumbnail(0.1f).into(holder.imgView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            when (payloads[0]) {
                CHANGE_ITEM_POSITION -> {
                    holder.imgView.animateScale(
                            toScale = 0f,
                            onEnd = {
                                Glide.with(holder.imgView.context).load(images[position]).centerInside().thumbnail(0.1f).into(holder.imgView)
                                holder.imgView.animateScale(toScale = 1f)
                            })
                }
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgView: ImageView = view.imgView
    }
}
