package com.google.drive.adapter

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.api.services.drive.model.File
import com.google.drive.R
import com.google.drive.databinding.ItemListBinding
import com.google.drive.extension.showToast
import com.google.drive.helper.getDiffUtilCallBack
import com.google.drive.utils.Constants.DRIVE_FOLDERS
import com.google.drive.utils.Constants.archiveMimeTypes
import com.google.drive.utils.Constants.audioMimetypes
import com.google.drive.utils.Constants.documentMimetype
import com.google.drive.utils.Constants.getFilePlaceholderDrawables
import com.google.drive.utils.Constants.photosMimeTypes
import com.google.drive.utils.Constants.textMimeTypes
import com.google.drive.utils.Constants.videoMimeTypes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "DriveAdapterLogs"

class DriveAdapter(private val activity: Activity, val callback: (item: File, position: Int) -> Unit) : ListAdapter<File, DriveAdapter.ViewHolder>(getDiffUtilCallBack()) {

    private var fileDrawables = HashMap<String, Drawable>()

    init {
        initDrawable()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemListBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
       holder. bindViews(currentItem)
    }


    inner class ViewHolder(private val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root){

         fun bindViews(currentItem: File) {

            binding.apply {

                root.setOnClickListener {
                    activity.showToast("selectedItemType: ${currentItem.mimeType}")
                    if (currentItem.mimeType == DRIVE_FOLDERS) {
                        callback.invoke(currentItem, layoutPosition)
                    }
                }

                val drawable = when {
                    audioMimetypes.contains(currentItem.mimeType) -> R.drawable.ic_file_audio
                    photosMimeTypes.contains(currentItem.mimeType) -> R.drawable.ic_file_image
                    documentMimetype.contains(currentItem.mimeType) -> R.drawable.ic_file_docs
                    archiveMimeTypes.contains(currentItem.mimeType) -> R.drawable.ic_file_zip
                    videoMimeTypes.contains(currentItem.mimeType) -> R.drawable.ic_file_video
                    textMimeTypes.contains(currentItem.mimeType) -> R.drawable.ic_file_txt
                    else -> fileDrawables.getOrElse(currentItem.mimeType) { fileDrawables }
                }

                CoroutineScope(Dispatchers.IO).launch {
                    Glide.with(root.context)
                        .load(drawable)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .also { withContext(Dispatchers.Main) { it.into(image) } }
                }


                title.text = currentItem.name
                email.text = currentItem.mimeType
            }
        }
    }

    private fun initDrawable() {
        fileDrawables = getFilePlaceholderDrawables(activity)
    }
}
