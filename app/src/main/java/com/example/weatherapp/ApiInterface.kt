package com.example.weatherapp

import com.example.weatherapp.model.ForecastResponseApi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
    fun getWeatherData(
        @Query("q") city: String,
        @Query("appid") appid:String,
        @Query("units") units: String
    ) : Call<WeatherApp>

    @GET("forecast")
    fun getForecast(
        @Query("q") city: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Call<ForecastResponseApi>
}
