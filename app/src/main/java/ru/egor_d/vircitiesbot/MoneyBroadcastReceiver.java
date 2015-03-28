package ru.egor_d.vircitiesbot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.parse.ParseObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private int i = 0;
    private Context context;
    private Utils utils;
    private PowerManager.WakeLock wl;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        utils = new Utils(context);

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        ws = new RestAdapter.Builder().setEndpoint(Utils.API).setLogLevel(RestAdapter.LogLevel.FULL).build().create(WebService.class);
        i = 0;

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

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
                        train();
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
//                money = 0;
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

    private void train() {
        List<String> types = new ArrayList<String>(3) {{
            add("1");
            add("3");
            add("4");
        }};
        String type = types.get(new Random().nextInt(3));
        TypedString s = new TypedString("data%5BCoachProgram%5D%5Blicense_id%5D=105&data%5BCoachProgram%5D%5Bid%5D=101&data%5BSportActivity%5D%5Bsport_skill_type_id%5D=" + type + "&data%5BSportActivity%5D%5Benergy%5D=100");
        final ParseObject testObject = new ParseObject("Time");
        ws.train(s, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                i = 0;
                testObject.put("status", "success");
                testObject.put("login", new Utils(context).loadLogin());
                testObject.saveInBackground();
                wl.release();
            }

            @Override
            public void failure(RetrofitError error) {
                testObject.put("status", "fail");
                testObject.put("login", new Utils(context).loadLogin());
                testObject.saveInBackground();
                if (i != 2) {
                    train();
                    i++;
                } else {
                    i = 0;
                    wl.release();
                }
            }
        });
    }

}
