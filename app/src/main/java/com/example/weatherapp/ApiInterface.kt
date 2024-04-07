package com.example.weatherapp

import com.example.weatherapp.model.CityResponseApi
import com.example.weatherapp.model.ForecastResponseApi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
    fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("q") city: String,
        @Query("appid") appid:String,
        @Query("units") units: String
    ) : Call<WeatherApp>

    @GET("forecast")
    fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("q") city: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Call<ForecastResponseApi>

    @GET("geo/1.0/direct")
    fun getCitiesList(
        @Query("q") q:String,
        @Query("limit") limit: Int,
        @Query("appid") appid: String,
    ): Call<CityResponseApi>
}
