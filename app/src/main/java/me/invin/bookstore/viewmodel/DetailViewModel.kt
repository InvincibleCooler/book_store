package me.invin.bookstore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.invin.bookstore.model.Resource
import me.invin.bookstore.net.RequestManager
import me.invin.bookstore.net.res.BookRes
import me.invin.bookstore.net.res.SearchRes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailViewModel : ViewModel() {
    companion object {
        private const val TAG = "DetailViewModel"
    }

    private val _bookResponse = MutableLiveData<Resource<BookRes>>()
    val bookResponse: LiveData<Resource<BookRes>> get() = _bookResponse

    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean> get() = _showProgress

    fun getBook(isbn13: String) {
        _showProgress.postValue(true)
        RequestManager.getServiceApi(null).getBook(isbn13).enqueue(object : Callback<BookRes> {
            override fun onResponse(call: Call<BookRes>, response: Response<BookRes>) {
                _showProgress.postValue(false)
                _bookResponse.postValue(Resource.success(response.body()))
            }

            override fun onFailure(call: Call<BookRes>, t: Throwable) {
                _showProgress.postValue(false)
                _bookResponse.postValue(Resource.error(Exception(t)))
            }
        })
    }

    class Factory : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel() as T
        }
    }
}