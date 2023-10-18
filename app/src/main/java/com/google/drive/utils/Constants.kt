package com.google.drive.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.google.drive.R

object Constants {

    var DRIVE_FOLDERS = "application/vnd.google-apps.folder"

    val archiveMimeTypes = arrayListOf(
        "application/zip",
        "application/x-zip",
        "multipart/x-zip",
        "application/x-zip-compressed",

        "application/rar",
        "application/x-rar",
        "application/x-rar-compressed",

        "application/tar",
        "application/x-tar",
        "application/x-tar-compressed",

        "application/7z",
        "application/x-7z",
        "application/x-7z-compressed",

        "application/x-compressed",
        "application/x-gzip",
    )

    val photosMimeTypes = arrayListOf(
        "application/vnd.google-apps.photo",
        "image/png",
        "image/jpeg",
        "image/gif",
        "image/bmp"
    )
    var documentMimetype = arrayListOf(
        "application/vnd.google-apps.document",
        "application/vnd.google-apps.writer",
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-excel",
        "application/javascript"
    )

    val audioMimetypes = arrayListOf(
        "application/vnd.google-apps.audio",
        "audio/mpeg",
        "audio/mp3"
    )

    val videoMimeTypes = arrayListOf(
        "application/vnd.google-apps.video",
        "application/vnd.google-apps.youtube",
        "video/mp4",
        "video/mpeg",
        "video/ogg",
        "video/quicktime",
        "video/webm"
    )

    val textMimeTypes = arrayListOf(
        "application/vnd.oasis.opendocument.text",
        "application/rtf",
        "text/plain",
        "text/html",
        "text/csv"
    )


    fun getFilePlaceholderDrawables(context: Context): HashMap<String, Drawable> {
        val fileDrawables = HashMap<String, Drawable>()
        hashMapOf<String, Int>().apply {

            //wallpaper
            put("application/vnd.google-apps.wallpaper", R.drawable.ic_file_video)         //video

            put("application/vnd.google-apps.unknown", R.drawable.ic_file_apk)       //apk bundles
            put("application/vnd.android.package-archive", R.drawable.ic_file_apk)       //apk bundles

            put("application/vnd.google-apps.presentation", R.drawable.ic_file_ppt)  //slides
            put("application/vnd.openxmlformats-officedocument.presentationml.presentation", R.drawable.ic_file_ppt)  //slides

            put("application/vnd.google-apps.spreadsheet", R.drawable.ic_file_excel)   //excel
            put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", R.drawable.ic_file_excel)   //excel

            put("application/vnd.google-apps.file", R.drawable.ic_file_csv)          //file
            put("application/vnd.google-apps.folder", R.drawable.ic_folder_normal)        //folder
            put("application/vnd.google-apps.drive-sdk", R.drawable.ic_file_generic)
            put("application/vnd.google-apps.drawing", R.drawable.ic_file_generic)
            put("application/vnd.google-apps.form", R.drawable.ic_file_generic)
            put("application/vnd.google-apps.fusiontable", R.drawable.ic_file_generic)
            put("application/vnd.google-apps.jam", R.drawable.ic_file_generic)
            put("application/vnd.google-apps.map", R.drawable.ic_file_generic)
            put("application/vnd.google-apps.script", R.drawable.ic_file_generic)
            put("application/vnd.google-apps.shortcut", R.drawable.ic_file_generic)
            put("application/vnd.google-apps.site", R.drawable.ic_file_generic)
        }.forEach { (key, value) ->
            fileDrawables[key] = (ContextCompat.getDrawable(context, value) ?: R.drawable.ic_file_generic) as Drawable
        }
        return fileDrawables
    }


    /*
   fun getFilePlaceholderDrawables(context: Context): HashMap<String, Drawable> {
        val fileDrawables = HashMap<String, Drawable>()
        hashMapOf<String, Int>().apply {


            put("application/vnd.google-apps.document", R.drawable.ic_file_docs)      //documents
            put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", R.drawable.ic_file_docs)      //documents
            put("application/vnd.google-apps.writer", R.drawable.ic_file_docs)      //documents

            put("application/vnd.google-apps.audio", R.drawable.ic_file_audio)         //audio
            put("audio/mpeg", R.drawable.ic_file_audio)         //audio
            put("audio/mp3", R.drawable.ic_file_audio)         //audio

            //wallpaper
            put("application/vnd.google-apps.wallpaper", R.drawable.ic_file_video)         //video


            put("application/vnd.google-apps.video", R.drawable.ic_file_video)         //video
            put("application/vnd.google-apps.youtube", R.drawable.ic_file_video)         //video
            put("video/mp4", R.drawable.ic_file_video)         //video
            put("video/mpeg", R.drawable.ic_file_video)         //video
            put("video/ogg", R.drawable.ic_file_video)         //video
            put("video/quicktime", R.drawable.ic_file_video)         //video
            put("video/webm", R.drawable.ic_file_video)         //video

            //text
            put("application/vnd.oasis.opendocument.text", R.drawable.ic_file_apk)       //apk bundles
            put("application/rtf", R.drawable.ic_file_apk)       //apk bundles
            put("text/plain", R.drawable.ic_file_apk)       //apk bundles
            put("text/html", R.drawable.ic_file_apk)       //apk bundles
            put("text/csv", R.drawable.ic_file_apk)       //apk bundles


            put("application/vnd.google-apps.unknown", R.drawable.ic_file_apk)       //apk bundles
            put("application/vnd.android.package-archive", R.drawable.ic_file_apk)       //apk bundles


            put("application/zip", R.drawable.ic_file_apk)       //zip bundles
            put("application/x-zip", R.drawable.ic_file_apk)       //zip bundles
            put("application/x-gzip", R.drawable.ic_file_apk)       //zip bundles


            put("application/pdf", R.drawable.ic_file_apk)       //pdf



            put("application/vnd.google-apps.presentation", R.drawable.ic_file_ppt)  //slides
            put("application/vnd.openxmlformats-officedocument.presentationml.presentation", R.drawable.ic_file_ppt)  //slides

            put("application/vnd.google-apps.spreadsheet", R.drawable.ic_file_excel)   //excel
            put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", R.drawable.ic_file_excel)   //excel


            put("application/vnd.google-apps.file", R.drawable.ic_file_csv)          //file
            put("application/vnd.google-apps.folder", R.drawable.ic_folder_normal)        //folder
            put("application/vnd.google-apps.drive-sdk", R.drawable.ic_file_generic)
            put("application/vnd.google-apps.drawing", R.drawable.ic_file_generic)
            put("application/vnd.google-apps.form", R.drawable.ic_file_generic)
            put("application/vnd.google-apps.fusiontable", R.drawable.ic_file_generic)
            put("application/vnd.google-apps.jam", R.drawable.ic_file_generic)
            put("application/vnd.google-apps.map", R.drawable.ic_file_generic)
            put("application/vnd.google-apps.script", R.drawable.ic_file_generic)
            put("application/vnd.google-apps.shortcut", R.drawable.ic_file_generic)
            put("application/vnd.google-apps.site", R.drawable.ic_file_generic)
        }.forEach { (key, value) ->
            fileDrawables[key] = (ContextCompat.getDrawable(context, value)) as Drawable
        }
        return fileDrawables
    }
*/


}