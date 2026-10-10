package com.example.data.local

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

data class SavedAttachment(
    val localPath: String,
    val displayName: String,
    val type: String, // "IMAGE" or "DOCUMENT"
    val sizeBytes: Long,
    val extractedText: String? = null
)

object AttachmentManager {

    private fun getAttachmentsDir(context: Context): File {
        val dir = File(context.filesDir, "attachments")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun saveBitmap(context: Context, bitmap: Bitmap, prefix: String = "kamera"): SavedAttachment? {
        return try {
            val dir = getAttachmentsDir(context)
            val fileName = "${prefix}_${System.currentTimeMillis()}.jpg"
            val file = File(dir, fileName)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            SavedAttachment(
                localPath = file.absolutePath,
                displayName = fileName,
                type = "IMAGE",
                sizeBytes = file.length(),
                extractedText = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveUriAttachment(context: Context, uri: Uri, isImage: Boolean): SavedAttachment? {
        return try {
            val contentResolver = context.contentResolver
            var displayName = if (isImage) "foto_${System.currentTimeMillis()}.jpg" else "dokumen_${System.currentTimeMillis()}"

            // Query original filename if possible
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    val name = cursor.getString(nameIndex)
                    if (!name.isNullOrBlank()) {
                        displayName = name
                    }
                }
            }

            val dir = getAttachmentsDir(context)
            val safeFileName = "${System.currentTimeMillis()}_${displayName.replace("\\s+".toRegex(), "_")}"
            val destinationFile = File(dir, safeFileName)

            var bytesCopied: Long = 0
            val extractedTextBuilder = StringBuilder()

            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    val buffer = ByteArray(8192)
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                        bytesCopied += read
                    }
                }
            }

            // If it's a plain text or markdown document, extract readable text
            val lower = displayName.lowercase()
            if (lower.endsWith(".txt") || lower.endsWith(".md") || lower.endsWith(".text")) {
                try {
                    val content = destinationFile.readText(Charsets.UTF_8)
                    extractedTextBuilder.append(content)
                } catch (e: Exception) {
                    // Ignore text read error
                }
            }

            val extracted = if (extractedTextBuilder.isNotEmpty()) extractedTextBuilder.toString() else null

            SavedAttachment(
                localPath = destinationFile.absolutePath,
                displayName = displayName,
                type = if (isImage) "IMAGE" else "DOCUMENT",
                sizeBytes = destinationFile.length(),
                extractedText = extracted
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteAttachment(localPath: String): Boolean {
        return try {
            val file = File(localPath)
            if (file.exists()) file.delete() else false
        } catch (e: Exception) {
            false
        }
    }
}
