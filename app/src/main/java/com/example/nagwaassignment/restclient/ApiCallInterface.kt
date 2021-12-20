package com.example.nagwaassignment.restclient

import android.util.Log
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url


interface ApiCallInterface {

    @GET
    fun downloadFileRX(@Url fullUrl: String): Observable<ResponseBody>


    companion object Factory {
        fun create(): ApiCallInterface {

            val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(ApiCallInterface::class.java)
        }



         var BASE_URL = "http://google.com/"
    }

}