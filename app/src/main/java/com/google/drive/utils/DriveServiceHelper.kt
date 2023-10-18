package com.google.drive.utils

import android.util.Log
import com.google.android.gms.tasks.Tasks.call
import com.google.api.client.http.FileContent
import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private const val TAG = "DriveServiceHelper"

/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
@Suppress("DEPRECATION")
class DriveServiceHelper(private val mDriveService: Drive) {

    private val mExecutor: Executor = Executors.newSingleThreadExecutor()


    /**
     * Returns a [FileList] containing all the visible files in the user's My Drive.
     *
     *
     * The returned list will only contain files visible to this app, i.e. those which were
     * created by this app. To perform operations on files not created by the app, the project must
     * request Drive Full Scope in the [Google
 * Developer's Console](https://play.google.com/apps/publish) and be submitted to Google for verification.
     */
    fun queryFiles(): List<File> {
        var result: FileList? = null
        val dbFile = ArrayList<File>()
        var pageToken: String? = null
        do {
            result = mDriveService.files()
                .list() /*.setQ("mimeType='image/png' or mimeType='text/plain'") This si to list both image and text files. Mind the type of image(png or jpeg).setQ("mimeType='image/png' or mimeType='text/plain'") */
                .setSpaces("drive")
                .setFields("nextPageToken, files(id,name,modifiedTime,mimeType,size,webViewLink,fileExtension)")
                .setPageToken(pageToken)
                .execute()

            pageToken = result.nextPageToken
            Log.d(TAG, "queryFiles: res")
        } while (pageToken != null)

        if (!result.isNullOrEmpty() && !result.files.isNullOrEmpty()) {
            dbFile.addAll(result.files!!)
            Log.d(TAG, "queryFiles: $dbFile")
            Log.d(TAG, "queryFiles: ${dbFile.size}")
        }

        return dbFile
    }

    /**
     * Get all the file present in the user's My Drive Folder.
    \   */
    fun getFolderFileList(folderId: String): List<File> {


        var result: FileList
        val dbFile = ArrayList<File>()
        var pageToken: String? = null
        do {
            result = mDriveService.files()
                .list() /*.setQ("mimeType='image/png' or mimeType='text/plain'")This si to list both image and text files. Mind the type of image(png or jpeg).setQ("mimeType='image/png' or mimeType='text/plain'") */
                .setSpaces("drive")
                .setQ("parents = '$folderId' ")
                .setFields("nextPageToken, files(id,name,modifiedTime,mimeType,size,webViewLink,fileExtension)")
                .setPageToken(pageToken)
                .execute()
            pageToken = result.nextPageToken
        } while (pageToken != null)

        dbFile.addAll(result.files)
        return dbFile
    }

    /**
     * Rename the folder and file if you have permission to rename
     */

    fun renameFile(fileId: String, file: File) = call(
        mExecutor
    ) {
        mDriveService.files().update(fileId, file).execute()
    }

    /**
     * Delete the folder and file if you have permission to rename
     */

    fun delete(fileId: String) = call(
        mExecutor
    ) {
        mDriveService.files().delete(fileId).execute()
    }

    /**
     * Return the Input Stream of file
     */

    suspend fun download(fileId: String): InputStream? =
        mDriveService.files().get(fileId).executeMediaAsInputStream()


    /**
     * @param fileId   Id of file to be moved.
     * @param folderId Id of folder where the fill will be moved.
     */

    fun move(fileId: String, folderId: String) = call(mExecutor) {
        val file: File = mDriveService.files().get(fileId)
            .setFields("parents")
            .execute()
        val previousParents = java.lang.StringBuilder()
        for (parent in file.parents) {
            previousParents.append(parent)
            previousParents.append(',')
        }
        mDriveService.files().update(fileId, null)
            .setAddParents(folderId)
            .setRemoveParents(previousParents.toString())
            .execute()
    }

    /**
     * Create new folder.
     *
     * @return Inserted folder id if successful, `null` otherwise.
     * @throws IOException if service account credentials file not found.
     */
    @Throws(IOException::class)
    suspend fun createFolder(folderName: String, folderId: String? = null): File? {
        Log.d("TAG", "createFolder: $folderName $folderId")
        val fileMetadata = File()
        fileMetadata.name = folderName
        fileMetadata.mimeType = "application/vnd.google-apps.folder"
        if (folderId != null)
            fileMetadata.parents = arrayListOf(folderId)
        return mDriveService.files()
            .create(fileMetadata)
            .setFields("id")
            .execute()
    }


    /**
     * Upload new file.
     *
     * @return Inserted file metadata if successful, `null` otherwise.
     */

    suspend fun upload(file: java.io.File, folderId: String? = null): File? {
        val fileMetadata = File()
        fileMetadata.name = file.name
        if (folderId != null)
            fileMetadata.parents = arrayListOf(folderId)
        val mediaContent = FileContent(file.absolutePath.getMimeType(), file)
        Log.d("TAG", "upload: ${file.name}")
        return mDriveService.files().create(fileMetadata, mediaContent)
            .execute()
    }

    /**
     * Upload new Input Stream.
     *
     * @return Inserted file metadata if successful, `null` otherwise.
     */

    suspend fun upload(
        inputStream: InputStream,
        name: String,
        folderId: String? = null
    ): File? {
        val fileMetadata = File()
        fileMetadata.name = name
        if (folderId != null)
            fileMetadata.parents = arrayListOf(folderId)

        return mDriveService.files().create(fileMetadata, InputStreamContent(null, inputStream))
            .execute()
    }

    /**
     * Copy file to new path.
     *
     * @return Inserted file metadata if successful, `null` otherwise.
     */

    suspend fun copy(oldPath: String, name: String, folderId: String): File? {

        val fileMetadata = File()
        fileMetadata.name = name
        fileMetadata.parents = arrayListOf(folderId)
        return mDriveService.files().copy(oldPath, fileMetadata).execute()
    }
}