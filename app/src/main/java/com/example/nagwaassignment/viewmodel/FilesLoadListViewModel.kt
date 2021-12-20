package com.example.nagwaassignment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.nagwaassignment.datasource.FilesDataSourceFactory
import com.example.nagwaassignment.restclient.Repository
import com.example.nagwaassignment.model.Download
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class FilesLoadListViewModel   @Inject constructor(var repository: Repository) : ViewModel() {

    private var newsDataSourceFactory: FilesDataSourceFactory
    private lateinit var listLiveData: LiveData<PagedList<Download>>
    private var progressLoadStatus: LiveData<Download> = MutableLiveData()
    private var compositeDisposable = CompositeDisposable()
    private val TAG = FilesLoadListViewModel::class.java.canonicalName

    init {
        newsDataSourceFactory = FilesDataSourceFactory(repository, compositeDisposable)
        initializePaging()
    }

    private fun initializePaging() {
        Log.i(TAG, ":--------initializePaging-----: ")

        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(10)
            .setPageSize(10).build()

        listLiveData = LivePagedListBuilder(newsDataSourceFactory, pagedListConfig).build()

//        progressLoadStatus = Transformations.switchMap(
//            newsDataSourceFactory.getMutableLiveData(),
//            FilesDataSourceClass::getProgressLiveStatus
//        )
    }

//    fun getListLiveData(): LiveData<PagedList<Attachments>> {
//        Log.i(TAG, ":--------getListLiveData-----: ")
//        return listLiveData
//    }

    fun getProgressLoadStatus(): LiveData<Download> {
        Log.i(TAG, ":--------getProgressLoadStatus-----: $progressLoadStatus")
        return progressLoadStatus
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, ":--------onCleared-----: ")

        compositeDisposable.clear()
    }
}