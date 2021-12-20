package com.example.nagwaassignment.app

import android.app.Application
import android.content.Context

import com.example.nagwaassignment.di.AppComponent
import com.example.nagwaassignment.di.AppUtilsModules
import com.example.nagwaassignment.di.DaggerAppComponent


open class MyApplication : Application() {

    private var appComponent: AppComponent? = null
    private var mContext: Context? = null

    override fun onCreate() {
        super.onCreate()
        mContext = this
        appComponent = DaggerAppComponent.builder().appUtilsModules(AppUtilsModules()).build()

    }

    public fun getAppComponent(): AppComponent {
        return appComponent!!
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}