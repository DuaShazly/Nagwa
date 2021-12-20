package  com.example.nagwaassignment.datasource

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.nagwaassignment.restclient.Repository
import com.example.nagwaassignment.model.Download

import io.reactivex.disposables.CompositeDisposable




class FilesDataSourceClass(
    var repository: Repository,
    private var compositeDisposable: CompositeDisposable
) : PageKeyedDataSource<Int, Download>() {
    private var sourceIndex: Int = 1
    private var progressLiveStatus: MutableLiveData<String> = MutableLiveData()


    fun getProgressLiveStatus(): MutableLiveData<String> {
        return progressLiveStatus
    }

    @SuppressLint("CheckResult")
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Download>) {
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Download>) {
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Download>) {
    }
}