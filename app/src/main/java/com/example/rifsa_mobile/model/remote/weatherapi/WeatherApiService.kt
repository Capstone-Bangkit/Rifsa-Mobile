package com.example.rifsa_mobile.model.remote.weatherapi

import com.example.rifsa_mobile.model.entity.openweatherapi.WeatherDetailResponse
import com.example.rifsa_mobile.model.entity.openweatherapi.forecast.WeatherForecastResponse
import com.example.rifsa_mobile.model.remote.weatherapi.utils.Constant
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("weather")
    suspend fun getWeatherDataBySearch(
        @Query("q") location : String,
        @Query("units") units : String = Constant.weatherUnit,
        @Query("appid") apiKey : String = Constant.weatherApiKey
    ):WeatherDetailResponse

    @GET("forecast")
    fun getWeatherForecastData(
        @Query("q") location : String,
        @Query("units") units : String = Constant.weatherUnit,
        @Query("appid") apiKey : String = Constant.weatherApiKey
    ): WeatherForecastResponse

}