package com.newlifepartner.utils


import retrofit2.Response

suspend fun <T : Any> safeApiCall(apiCall: suspend () -> Response<T>): ResultType<T> {
    return try {
        val response = apiCall()
        return when {
            response.isSuccessful ->
                response.body()?.let {
                    ResultType.Success(it)
                } ?: ResultType.Error("Empty body in response.")
            else -> ResultType.Error(response.body().toString())
        }
    } catch (throwable: Throwable) {
        ResultType.Error("$throwable Something went wrong")
    }
}
