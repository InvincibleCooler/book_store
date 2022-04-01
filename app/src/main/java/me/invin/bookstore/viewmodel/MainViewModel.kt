package me.invin.bookstore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.invin.bookstore.model.Resource
import me.invin.bookstore.net.RequestManager
import me.invin.bookstore.net.res.SearchRes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainViewModel : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _searchResponse = MutableLiveData<Resource<SearchRes>>()
    val searchResponse: LiveData<Resource<SearchRes>> get() = _searchResponse

    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean> get() = _showProgress
    var isRequesting = false // 서버 요청에 대한 응답값 도착 유무 판별, 더보기시만 사용함

    fun getSearch(keyword: String) {
        _showProgress.postValue(true)
        RequestManager.getServiceApi(null).getSearch(keyword).enqueue(object : Callback<SearchRes> {
            override fun onResponse(call: Call<SearchRes>, response: Response<SearchRes>) {
                _showProgress.postValue(false)
                _searchResponse.postValue(Resource.success(response.body()))
            }

            override fun onFailure(call: Call<SearchRes>, t: Throwable) {
                _showProgress.postValue(false)
                _searchResponse.postValue(Resource.error(Exception(t)))
            }
        })
    }

    fun getSearchPartial(keyword: String, page: String) {
        isRequesting = true
        _showProgress.postValue(true)
        RequestManager.getServiceApi(null).getSearchPartial(keyword, page).enqueue(object : Callback<SearchRes> {
            override fun onResponse(call: Call<SearchRes>, response: Response<SearchRes>) {
                isRequesting = false
                _showProgress.postValue(false)
                _searchResponse.postValue(Resource.success(response.body()))
            }

            override fun onFailure(call: Call<SearchRes>, t: Throwable) {
                isRequesting = false
                _showProgress.postValue(false)
                _searchResponse.postValue(Resource.error(Exception(t)))
            }
        })
    }

    class Factory : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel() as T
        }
    }
}