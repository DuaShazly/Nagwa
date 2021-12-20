package com.example.nagwaassignment.di

import androidx.lifecycle.ViewModelProvider
import com.example.nagwaassignment.restclient.HostSelectionInterceptor
import com.example.nagwaassignment.restclient.ApiCallInterface
import com.example.nagwaassignment.restclient.Repository
import com.example.nagwaassignment.utils.ViewModelFactory
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class AppUtilsModules {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder =
            GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.setLenient().create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient, url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun getAllApiCallInterface(retrofit: Retrofit): ApiCallInterface {
        return retrofit.create(ApiCallInterface::class.java)
    }


    @Provides
    @Singleton
    internal fun getRequestHeader(): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HostSelectionInterceptor())
            .cache(null)
            .connectTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
        return okHttpClient.build()
    }

    @Provides
    @Singleton
    fun getRepository(apiCallInterface: ApiCallInterface): Repository {
        return Repository(apiCallInterface)
    }

    @Provides
    @Singleton
    fun getViewModelFactory(repository: Repository): ViewModelProvider.Factory {
        return ViewModelFactory(repository)
    }
}