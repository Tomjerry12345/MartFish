package com.martfish.services

import com.martfish.model.ModelTransaksi
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface WebServices {
    @GET("/v2/{idTransaction}/status")
    suspend fun getStatusTransaction(@Path("idTransaction") idTransaction: String): Response<ModelTransaksi>
}

private val client = OkHttpClient.Builder()
    .addInterceptor(BasicAuthInterceptor("SB-Mid-server-v3SOA6yd2pYXsymdalJpmW2d", "")).build()

val webServices: WebServices by lazy {
    Retrofit.Builder()
        .baseUrl("https://api.sandbox.midtrans.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
        .create(WebServices::class.java)
}