package me.invin.bookstore.net.res

import com.google.gson.annotations.SerializedName


class BookRes {
    @SerializedName("error")
    var error: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("subtitle")
    var subtitle: String? = null
    @SerializedName("authors")
    var authors: String? = null
    @SerializedName("publisher")
    var publisher: String? = null
    @SerializedName("language")
    var language: String? = null
    @SerializedName("isbn10")
    var isbn10: String? = null
    @SerializedName("isbn13")
    var isbn13: String? = null
    @SerializedName("pages")
    var pages: String? = null
    @SerializedName("year")
    var year: String? = null
    @SerializedName("rating")
    var rating: String? = null
    @SerializedName("desc")
    var desc: String? = null
    @SerializedName("price")
    var price: String? = null
    @SerializedName("image")
    var image: String? = null
    @SerializedName("url")
    var url: String? = null
    @SerializedName("pdf")
    var pdf: Pdf? = null

    class Pdf {
        @SerializedName("Chapter 2")
        var chapter2: String? = null
        @SerializedName("Chapter 5")
        var chapter5: String? = null
    }
}