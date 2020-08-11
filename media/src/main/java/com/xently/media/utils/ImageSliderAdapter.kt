package com.xently.media.utils

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import com.xently.media.R

class ImageSliderAdapter : SliderViewAdapter<ImageSliderAdapter.ImageSliderViewHolder>() {

    private var imageUrls = listOf<Uri>()
    fun submitList(imageUrls: List<Uri>) {
        this.imageUrls = imageUrls
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup) = ImageSliderViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.image, parent, false)
    )

    override fun onBindViewHolder(viewHolder: ImageSliderViewHolder, position: Int) {
        viewHolder.bind(imageUrls[position])
    }

    override fun getCount(): Int = imageUrls.size

    inner class ImageSliderViewHolder(itemView: View) : ViewHolder(itemView) {
        private val imageView = (itemView.findViewById(R.id.media_image_view) as? ImageView)
            ?: ImageView(itemView.context).apply {
                id = R.id.media_image_view
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                adjustViewBounds = true
                contentDescription = context.getString(R.string.media_content_desc_image)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

        fun bind(imageUri: Uri) {
            Glide.with(itemView.context)
                .load(imageUri)
                .fitCenter()
                .into(imageView)
        }
    }
}