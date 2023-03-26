package com.example.sampleqiitaapp

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class APIManager {
    companion object {
        val baseUrl = "https://qiita.com/api/v2/"

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        inline fun <reified T> get(
            api: String,
            crossinline success: (List<T>) -> Unit,
            crossinline failure: () -> Unit
        ) {
            val request = Request.Builder()
                .url("$baseUrl$api")
                .build()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = client.newCall(request).execute()
                    val json = Json {
                        ignoreUnknownKeys = true
                    }

                    response.body?.let {
                        val list: List<T> = json.decodeFromString(it.string())
                        CoroutineScope(Dispatchers.Main).launch {
                            success(list)
                        }

                    }


                } catch (e: Exception) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Log.e("API", e.toString())
                        failure()
                    }
                }
            }
        }
    }


}
