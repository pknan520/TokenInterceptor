package com.midea.httpDemo.http

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private var okHttpClient: OkHttpClient
    private var retrofit: Retrofit

    private const val baseUrl = "http://10.0.2.2:8888/"

    init {
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
    }

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    fun <T> createApi(service: Class<T>, url: String = baseUrl): T {
        return retrofit.newBuilder().baseUrl(url).build().create(service)
    }

}
