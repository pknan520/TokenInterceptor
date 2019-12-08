package com.midea.httpDemo.http.bean

data class BaseRsp<T>(
    val success: Boolean,
    val error_code: Int,
    val data: T
)