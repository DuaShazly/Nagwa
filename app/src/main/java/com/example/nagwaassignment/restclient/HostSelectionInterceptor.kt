package com.example.nagwaassignment.restclient

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

class HostSelectionInterceptor: Interceptor {

    @Volatile
    private var host: String? = null
    fun setHost(host: String?) {
        this.host = host
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()

//        val rehost: String = host

        val newUrl = request.url().newBuilder()
            .host(host)
            .build()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HostSelectionInterceptor())
            .cache(null)
            .build()


        request = request.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(request)
    }

}