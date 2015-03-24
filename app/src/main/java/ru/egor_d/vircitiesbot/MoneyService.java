package ru.egor_d.vircitiesbot;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.mime.TypedString;

/**
 * Created by Egor Danilin on 24.03.2015.
 */
public class MoneyService extends Service {
    //    private ScheduledExecutorService scheduler;
    private WebService ws;
    private int money = 0;
    private Handler handler = new Handler();
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            go();
            handler.postDelayed(r, 25 * 60 * 1000);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_v)
                .setContentTitle("Vircities Bot")
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
        Notification notif;
        if (Build.VERSION.SDK_INT >= 16)
            notif = builder.build();
        else
            notif = builder.getNotification();
        startForeground(1, notif);

        ws = new RestAdapter.Builder().setEndpoint("http://api.vircities.com").setLogLevel(RestAdapter.LogLevel.FULL).build().create(WebService.class);

//        scheduler = Executors.newSingleThreadScheduledExecutor();
//        scheduler.scheduleAtFixedRate(new Runnable() {
//            public void run() {
//                go();
//            }
//        }, 0, 20, TimeUnit.SECONDS);
        handler.post(r);
        return super.onStartCommand(intent, flags, startId);
    }

    private void go() {
        TypedString s = new TypedString(String.format("data%%5BUser%%5D%%5Busername%%5D=%s&data%%5BUser%%5D%%5Bpassword%%5D=%s", loadLogin(), loadPassword()));
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
                        Toast.makeText(MoneyService.this, String.format("Error while check: %s", error.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MoneyService.this, String.format("Error while login: %s", error.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fight() {
        ws.fight(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                money += jsonObject.get("result").getAsJsonObject().get("vdEarned").getAsInt();
                fight();
            }

            @Override
            public void failure(RetrofitError error) {
//                Toast.makeText(MoneyService.this, String.format("Money earned: %d", money), Toast.LENGTH_SHORT).show();
                money = 0;
            }
        });
    }

    public String loadLogin() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString("login", "");
    }

    public String loadPassword() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString("password", "");
    }

    interface WebService {
        @Headers({"Content-type: application/x-www-form-urlencoded"})
        @POST("/users/app_auth.json")
        void auth(@Body TypedString s, Callback<JsonObject> callback);

        @POST("/users/check.json")
        void check(Callback<JsonObject> callback);

        @POST("/military/pve/gangs/1/fight")
        void fight(Callback<JsonObject> callback);
    }

}
