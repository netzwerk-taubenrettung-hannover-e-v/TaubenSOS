package de.unihannover.se.tauben2.model.network

class Resource<out T>(var status: Status, val data: T? = null, var message: String? = null) {
    enum class Status {
        SUCCESS, LOADING, ERROR;

        fun isLoading() = this == Status.LOADING

        fun hasError() = this == Status.ERROR

        fun isSuccessful() = this == Status.SUCCESS

    }

    companion object {
        fun <T> success(data: T?): Resource<T> = Resource(Status.SUCCESS, data)

        fun <T> loading(data: T? = null): Resource<T> = Resource(Status.LOADING, data)

        fun <T> error(message: String?): Resource<T> = Resource(Status.ERROR, message = message)

    }

    fun hasError() = status.hasError() || (this.status.isSuccessful() && this.data == null)



}