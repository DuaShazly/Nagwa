package com.example.nagwaassignment.restclient

import io.reactivex.Observable
import okhttp3.ResponseBody
import javax.inject.Inject


class Repository  constructor (var apiCallInterface: ApiCallInterface) {



    fun downloadFile(url: String): Observable<ResponseBody> {
        return apiCallInterface.downloadFileRX(url)
    }

}