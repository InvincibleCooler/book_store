package me.invin.bookstore.net

import me.invin.bookstore.net.res.BookRes
import me.invin.bookstore.net.res.SearchRes
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface ServiceApi {
    @GET("search/{query}")
    fun getSearch(@Path("query") query: String): Call<SearchRes>

    @GET("search/{query}/{page}")
    fun getSearchPartial(
        @Path("query") query: String,
        @Path("page") page: String
    ): Call<SearchRes>

    @GET("books/{isbn13}")
    fun getBook(@Path("isbn13") isbn13: String): Call<BookRes>
}