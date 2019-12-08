package com.midea.httpDemo.http

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.midea.httpDemo.http.bean.BaseRsp
import okhttp3.Interceptor
import okhttp3.Response
import java.nio.charset.Charset
import android.os.ConditionVariable
import com.midea.httpDemo.LoginInfo
import okhttp3.Request
import java.util.concurrent.atomic.AtomicBoolean

class TokenInterceptor: Interceptor {

    private val LOCK = ConditionVariable(true)
    private val mIsRefreshing = AtomicBoolean(false)

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (originalRequest.url.toString().contains("/refresh_token")) {
            return chain.proceed(originalRequest)
        }

        if (mIsRefreshing.get()) {
            LOCK.block(10000)
            return reRequest(originalRequest, chain)
        } else {
            val response = chain.proceed(originalRequest)

            val responseBody = response.body
            responseBody?.let { body ->
                val source = body.source()
                source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
                val buffer = source.buffer()
                val UTF8 = Charset.forName("UTF-8")
                val string = buffer.clone().readString(UTF8)

                val baseRsp = Gson().fromJson(string, BaseRsp::class.java)
                if (baseRsp != null) {
                    if (baseRsp.error_code == 1001 || baseRsp.error_code == 1000) {
                        //token过期
                        //根据RefreshToken同步请求，获取最新的Token
                        if (mIsRefreshing.compareAndSet(false, true)) {
                            LOCK.close()

                            LoginInfo.token = getNewToken()

                            LOCK.open()
                            mIsRefreshing.set(false)

                            return reRequest(originalRequest, chain)
                        } else {
                            if (LOCK.block(10000)) {
                                return reRequest(originalRequest, chain)
                            }
                        }
                    }
                }
            }

            return response
        }
    }

    private fun reRequest(originalRequest: Request, chain: Interceptor.Chain): Response {
//        val url = originalRequest.url
//            .newBuilder()
//            .removeAllQueryParameters("token")
//            .addQueryParameter("token", newToken)
//            .build()
        val newRequest = originalRequest
            .newBuilder()
            .removeHeader("accessToken")
            .addHeader("accessToken", LoginInfo.token)
//            .url(url)
            .build()
        return chain.proceed(newRequest)
    }

    @SuppressLint("CheckResult")
    private fun getNewToken(): String {
        val api = RetrofitClient.createApi(ApiService::class.java, "http://10.0.2.2:8888/")
        val call = api.refresh()
        val rsp = call.execute().body()
        return rsp?.data?.token ?: ""
    }

}