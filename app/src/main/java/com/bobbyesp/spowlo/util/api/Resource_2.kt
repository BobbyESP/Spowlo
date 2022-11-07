package com.bobbyesp.spowlo.util.api

data class Resource_2<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {

        fun <T> success(data: T?): Resource_2<T> {
            return Resource_2(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Resource_2<T> {
            return Resource_2(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T?): Resource_2<T> {
            return Resource_2(Status.LOADING, data, null)
        }

    }

}