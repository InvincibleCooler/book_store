package me.invin.bookstore.model


class Resource<out T> private constructor(val status: Status, val data: T?, private val exception: Exception?) {
    enum class Status {
        SUCCESS, ERROR
    }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(
                Status.SUCCESS,
                data,
                null
            )
        }

        fun <T> error(exception: Exception? = null): Resource<T> {
            return Resource(
                Status.ERROR,
                null,
                exception
            )
        }
    }

    fun getErrorMessage() = exception?.message
}