package com.chunkuploadfiles
import android.annotation.SuppressLint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File

class ProgressRequestBody(
  private val file: File,
  private val contentType: String,
  private val onProgress: (percent: Double) -> Unit
) : RequestBody() {

  override fun contentType() = contentType.toMediaTypeOrNull()

  override fun contentLength() = file.length()

  @SuppressLint("DefaultLocale")
  override fun writeTo(sink: BufferedSink) {
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    val fileLength = file.length()
    var uploaded: Long = 0

    file.inputStream().use { input ->
      var read: Int
      while (input.read(buffer).also { read = it } != -1) {
        uploaded += read
        sink.write(buffer, 0, read)
        val progressFraction = (uploaded.toDouble() / fileLength.toDouble())
        val progress = String.format("%.6f", progressFraction).toDouble()
        onProgress(progress) // ✅ Send progress %
      }
    }
  }
}
