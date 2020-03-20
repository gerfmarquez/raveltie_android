package com.maxor.raveltie

import com.maxor.raveltie.location.LocationResponse
import com.maxor.raveltie.score.ScoreResponse
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface RaveltieWebService {
    @PUT("place-location")
    fun pushLocation(
        @Query("imei") imei: String,
        @Query("timestamp") timestamp: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("accuracy") accuracy: Float) : Single<LocationResponse>

    @POST("retrieve-score")
    fun pullScore(
        @Query("imei") imei: String) : Single<ScoreResponse>

    @GET("config")
    fun pullConfig() : Single<RaveltieConfig>

    companion object  {
        val baseUrl: String = "https://i2zef7dx71.execute-api.us-east-1.amazonaws.com/dev/"
        fun create(): RaveltieWebService {
            val httpClient = OkHttpClient.Builder()
//            httpClient.addInterceptor(ParametersInterceptor())
            return  Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build().create(RaveltieWebService::class.java)
        }
    }
}