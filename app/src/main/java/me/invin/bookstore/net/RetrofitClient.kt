package me.invin.bookstore.net

import me.invin.bookstore.constants.BookProperty
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    // For Singleton instantiation.
    @Volatile
    private var instance: Retrofit? = null

    fun getInstance(okHttpClient: OkHttpClient): Retrofit =
        instance ?: synchronized(this) {
            instance ?: Retrofit.Builder()
                .baseUrl(BookProperty.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }
}