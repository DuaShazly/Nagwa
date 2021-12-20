package com.example.nagwaassignment.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.nagwaassignment.restclient.Repository
import com.example.nagwaassignment.model.Download
import io.reactivex.disposables.CompositeDisposable




class FilesDataSourceFactory(
    var repository: Repository,
    private var compositeDisposable: CompositeDisposable
) : DataSource.Factory<Int, Download>() {

    private val liveData: MutableLiveData<FilesDataSourceClass> = MutableLiveData()


    override fun create(): DataSource<Int, Download> {
        val dataSourceClass = FilesDataSourceClass(repository, compositeDisposable)
        liveData.postValue(dataSourceClass)
        return dataSourceClass
    }

    fun getMutableLiveData(): MutableLiveData<FilesDataSourceClass> {
        return liveData
    }
}