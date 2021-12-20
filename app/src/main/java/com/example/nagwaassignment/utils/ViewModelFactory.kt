package com.example.nagwaassignment.utils

import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nagwaassignment.restclient.Repository
import com.example.nagwaassignment.viewmodel.FilesLoadListViewModel

import javax.inject.Inject


open class ViewModelFactory

@Inject
constructor(private val repository: Repository) : ViewModelProvider.Factory {

    @NonNull
    override fun <T : ViewModel> create(@NonNull modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilesLoadListViewModel::class.java!!)) {
            return FilesLoadListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}
