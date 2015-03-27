package ru.egor_d.vircitiesbot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

/**
 * Created by Egor Danilin on 26.03.2015.
 */
public class MoneyBroadcastReceiver extends BroadcastReceiver {
    private WebService ws;
    private int money = 0;
    private Context context;
    private Utils utils;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        utils = new Utils(context);

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        ws = new RestAdapter.Builder().setEndpoint(Utils.API).setLogLevel(RestAdapter.LogLevel.FULL).build().create(WebService.class);
        go();
    }

    private void go() {
        TypedString s = new TypedString(String.format("data%%5BUser%%5D%%5Busername%%5D=%s&data%%5BUser%%5D%%5Bpassword%%5D=%s", utils.loadLogin(), utils.loadPassword()));
        ws.auth(s, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject s, Response response) {
                ws.check(new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject s, Response response) {
                        fight();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(context, String.format("Error while check: %s", error.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(context, String.format("Error while login: %s", error.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fight() {
        ws.fight(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
//                money += jsonObject.get("result").getAsJsonObject().get("vdEarned").getAsInt();
                fight();
            }

            @Override
            public void failure(RetrofitError error) {
//                Toast.makeText(MoneyService.this, String.format("Money earned: %d", money), Toast.LENGTH_SHORT).show();
                money = 0;
            }
        });
    }

    private void vote() {
        ws.vote(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                vote();
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }
}
