package com.example.nagwaassignment.di

import com.example.nagwaassignment.ui.MainActivity
import dagger.Component
import javax.inject.Singleton



@Component(modules = [AppUtilsModules::class])
@Singleton
interface AppComponent {

    fun doInjectInApp(activity: MainActivity)

//    fun inject(fragmentOne: FilesLoadListViewModel)
//    fun inject(fragmentOne: ViewModelFactory)


}