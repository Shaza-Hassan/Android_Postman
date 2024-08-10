package com.shaza.androidpostman.shared.netowrk

import android.content.ContentResolver
import android.net.Uri
import com.shaza.androidpostman.home.model.RequestType
import java.io.DataOutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */
object RequestHandler {


    private const val LINE_END = "\r\n"
    private const val TWO_HYPHENS = "--"

    fun applyHeaders(connection: HttpURLConnection, headers: Map<String, String>) {
        headers.forEach { (key, value) ->
            connection.setRequestProperty(key, value)
        }
    }

    fun appendBodyIfPost(requestType: RequestType, body: String?, connection: HttpURLConnection) {
        if (requestType == RequestType.POST && body != null) {
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json; utf-8")
            OutputStreamWriter(connection.outputStream).use {
                it.write(body)
                it.flush()
            }
        }
    }

    fun uploadFile(requestType: RequestType, uri: Uri?, connection: HttpURLConnection, contentResolver: ContentResolver) {
        if (requestType == RequestType.POST && uri != null) {
            val boundary = "Boundary-${System.currentTimeMillis()}"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

            val outputStream = DataOutputStream(connection.outputStream)

            try {
                // Write multipart headers
                outputStream.writeBytes("$TWO_HYPHENS$boundary$LINE_END")
                outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"${uri.lastPathSegment}\"$LINE_END")
                outputStream.writeBytes("Content-Type: application/octet-stream$LINE_END")
                outputStream.writeBytes(LINE_END)

                // Write file data
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val buffer = ByteArray(1024)
                    var bytesRead: Int

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                }

                // End of multipart/form-data
                outputStream.writeBytes(LINE_END)
                outputStream.writeBytes("$TWO_HYPHENS$boundary$TWO_HYPHENS$LINE_END")
                outputStream.flush()

            } catch (e: Exception) {
                throw e
            } finally {
                outputStream.close()
            }
        }
    }
}