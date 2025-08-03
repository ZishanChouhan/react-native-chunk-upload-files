package com.chunkuploadfiles

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.zishan.chunkuploadfiles.NativeChunkUploadFilesSpec
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException


@ReactModule(name = ChunkUploadFiles.NAME)
class ChunkUploadFiles(private val reactContext: ReactApplicationContext) :
  NativeChunkUploadFilesSpec(reactContext) {

  override fun getName(): String {
    return NAME
  }

  override fun post(to: String, key: String, fileURI: String, token: String?, otherParams: ReadableMap?, promise: Promise) {
    if (to.isBlank()) {
      promise.reject("-1", "No URL provided for upload or URL is not a string")
      return
    }
    if (key.isBlank()) {
      promise.reject("-1", "No key provided for upload or key is not a string")
      return
    }
    if (fileURI.isBlank()) {
      promise.reject("-1", "No file URI provided for upload or URI is not a string")
      return
    }

    val client = OkHttpClient()
    val myUri: Uri = fileURI.toUri()
    val pathToOurFile: String
    if (fileURI.contains("content")) {
      pathToOurFile = getRealPathFromUri(reactContext, myUri);
    } else {
      pathToOurFile = myUri.path.toString();
    }

    val file = File(pathToOurFile)
    val requestBody = ProgressRequestBody(
      file = File(pathToOurFile),
      contentType = "video/mp4"
    ) { progress ->
      sendEvent(reactContext, "progress", progress);
    }

    val builder = MultipartBody.Builder()
      .setType(MultipartBody.FORM)

    if (otherParams != null) {
      val map = otherParams.toHashMap()
      for ((key, value) in map) {
        val stringValue = when (value) {
          is String -> value
          is Number -> value.toString()
          is Boolean -> value.toString()
          is Map<*, *> -> JSONObject(value).toString()
          is List<*> -> JSONArray(value).toString()
          null -> ""
          else -> value.toString()  // fallback
        }
        builder.addFormDataPart(key, stringValue)
      }
    }

    builder.addFormDataPart(key, file.name, requestBody)

    val multipartBody = builder.build()

    val requestBuilder = Request.Builder()
      .url(to)
      .post(multipartBody)

    if (!token.isNullOrBlank()) {
      requestBuilder.addHeader("Authorization", "Bearer $token")
    }

    val request = requestBuilder.build()

    client.newCall(request).enqueue(object : Callback {
      override fun onFailure(call: Call, e: IOException) {
        e.printStackTrace()
        promise.reject("-1", e.message)
      }

      override fun onResponse(call: Call, response: Response) {
        response.use {
          if (!it.isSuccessful) {
            promise.reject("-1", it.message)
          } else {
            val responseString = it.body?.string()
            val jsonObject = JSONObject(responseString ?: "{}")
            val map: WritableMap = Arguments.createMap()
            val keys = jsonObject.keys()

            while (keys.hasNext()) {
              val key = keys.next()
              val value = jsonObject.get(key)

              when (value) {
                is Boolean -> map.putBoolean(key, value)
                is Int, is Long, is Double, is Float -> map.putDouble(key, (value as Number).toDouble())
                is String -> map.putString(key, value)
                JSONObject.NULL -> map.putNull(key)
                else -> map.putString(key, value.toString()) // fallback
              }
            }
            promise.resolve(map)
          }
        }
      }
    })
  }

  private fun getRealPathFromUri(context: Context, contentUri: Uri): String {
    var cursor: Cursor? = null
    try {
      val proj = arrayOf(MediaStore.Images.Media.DATA)
      cursor = context.contentResolver.query(contentUri, proj, null, null, null)
      val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
      cursor.moveToFirst()
      return cursor.getString(columnIndex)
    } finally {
      cursor?.close()
    }
  }

  private fun sendEvent(
    reactContext: ReactContext,
    eventName: String,
    progress: Double
  ) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(eventName, progress)
  }

  var listenerCount = 0
  @ReactMethod
  fun addListener(eventName: String?) {
    if (listenerCount == 0) {
      // Set up any upstream listeners or background tasks as necessary
    }

    listenerCount += 1
  }

  @ReactMethod
  fun removeListeners(count: Int) {
    listenerCount -= count
    if (listenerCount == 0) {
      // Remove upstream listeners, stop unnecessary background tasks
    }
  }

  companion object {
    const val NAME = "ChunkUploadFiles"
  }
}
