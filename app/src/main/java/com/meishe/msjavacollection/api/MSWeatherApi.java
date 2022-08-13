package com.meishe.msjavacollection.api;


import com.meishe.msjavacollection.msretrofit.annotation.Field;
import com.meishe.msjavacollection.msretrofit.annotation.GET;
import com.meishe.msjavacollection.msretrofit.annotation.POST;
import com.meishe.msjavacollection.msretrofit.annotation.Query;

import okhttp3.Call;
import retrofit2.http.FormUrlEncoded;

public interface MSWeatherApi {

    @GET("/v3/weather/weatherInfo")
    @FormUrlEncoded
    Call getWeather(@Query("city") String city, @Query("key") String key);

    @POST("/v3/weather/weatherInfo")
    Call postWeather(@Field("city") String city,@Field("key") String key);



}
