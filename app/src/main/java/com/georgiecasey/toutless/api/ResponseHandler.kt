package com.georgiecasey.toutless.api

import com.squareup.moshi.JsonDataException
import retrofit2.HttpException
import timber.log.Timber
import java.net.SocketTimeoutException
import java.net.UnknownHostException

enum class ErrorCodes(val code: Int) {
    SocketTimeOut(-1),
    UnknownHostException(2),
    JsonDataException(3)
}

open class ResponseHandler {
    fun <T : Any> handleSuccess(data: T): Resource<T> {
        return Resource.success(data)
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        Timber.e(e)
        return when (e) {
            is HttpException -> Resource.error(getErrorMessage(e.code()), null)
            is SocketTimeoutException -> Resource.error(getErrorMessage(ErrorCodes.SocketTimeOut.code), null)
            is UnknownHostException -> Resource.error(getErrorMessage(ErrorCodes.UnknownHostException.code), null)
            is JsonDataException -> Resource.error(getErrorMessage(ErrorCodes.JsonDataException.code), null)
            else -> Resource.error(getErrorMessage(Int.MAX_VALUE), null)
        }
    }

    private fun getErrorMessage(code: Int): String {
        return when (code) {
            ErrorCodes.SocketTimeOut.code -> "Timeout"
            ErrorCodes.UnknownHostException.code -> "Unknown host. Do you have an Internet connection?"
            ErrorCodes.JsonDataException.code -> "JSON exception in data from the server. This is my fault, not yours"
            401 -> "Unauthorised"
            404 -> "Not found"
            else -> "Something went wrong (code $code)"
        }
    }
}