package ru.egor_d.vircitiesbot;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.mime.TypedString;

/**
 * Created by Egor Danilin on 26.03.2015.
 */
public interface WebService {
    @Headers({"Content-type: application/x-www-form-urlencoded"})
    @POST("/users/app_auth.json")
    void auth(@Body TypedString s, Callback<JsonObject> callback);

    @GET("/users/check.json")
    void check(Callback<JsonObject> callback);

    @GET("/users/short_infos.json")
    void getShortInfo(Callback<JsonObject> callback);

    @POST("/military/pve/gangs/1/fight")
    void fight(Callback<JsonObject> callback);

    @GET("/big_fights/kick/113.json")
    void vote(Callback<JsonObject> callback);

    @Headers({"Content-type: application/x-www-form-urlencoded"})
    @POST("/sport_activities/use_program.json")
    void train(@Body TypedString s, Callback<JsonObject> callback);

    @Headers({"Content-type: application/x-www-form-urlencoded"})
    @POST("/user_items/eat_user_item.json")
    void eat(@Body TypedString s, Callback<JsonObject> callback);

    @Headers({"Content-type: application/x-www-form-urlencoded"})
    @POST("/users/begin_work_master.json")
    void work(@Body TypedString s, Callback<JsonObject> callback);

    @GET("/users/sport.json")
    void sport(Callback<JsonObject> callback);
}
