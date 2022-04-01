package me.invin.bookstore.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import me.invin.bookstore.model.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Loading 등 뷰와 연동 부분이 있으니 사용하지 말자.
 */
fun <T> Call<T>.asLiveData(): LiveData<Resource<T>> {
    val result = MutableLiveData<Resource<T>>()

    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            result.postValue(Resource.success(response.body()))
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            result.postValue(Resource.error(Exception(t)))
        }
    })
    return result
}