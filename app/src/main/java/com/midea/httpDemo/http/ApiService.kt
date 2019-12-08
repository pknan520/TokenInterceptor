package com.midea.httpDemo.http

import com.midea.httpDemo.http.bean.BaseRsp
import com.midea.httpDemo.http.bean.ResultBean
import com.midea.httpDemo.http.bean.TokenBean
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {

    @GET("request")
    fun request(@Header("accessToken") token: String?): Observable<BaseRsp<ResultBean>>

    @GET("refresh_token")
    fun refresh(): Call<BaseRsp<TokenBean>>
}