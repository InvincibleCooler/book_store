package me.invin.bookstore.net.res

import com.google.gson.annotations.SerializedName


class SearchRes {
    @SerializedName("total")
    var total: String? = null

    @SerializedName("page")
    var page: String? = null

    @SerializedName("books")
    var books: ArrayList<BookInfo>? = null
}