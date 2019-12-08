package com.midea.httpDemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.midea.httpDemo.http.ApiService
import com.midea.httpDemo.http.RetrofitClient
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn.setOnClickListener {
            for (i in 1..5) {
                Observable.timer(i * 600L, TimeUnit.MILLISECONDS)
                    .flatMap {
                        RetrofitClient.createApi(ApiService::class.java, "http://10.0.2.2:8888/")
                            .request(LoginInfo.token)
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.d("PK", it.success.toString())
                    }, {
                        Log.d("PK", it.message)
                    })
            }
//            Observable.just(1, 2, 3, 4, 5)
//                .delay(20, TimeUnit.MILLISECONDS)
//                .flatMap {
//                    RetrofitClient.createApi(ApiService::class.java, "http://10.0.2.2:8888/")
//                        .request(LoginInfo.token)
//                }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    Log.d("PK", it.success.toString())
//                }, {
//                    Log.d("PK", it.message)
//                })
        }
    }
}
