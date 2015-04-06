package ru.egor_d.vircitiesbot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.parse.ParseObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Context context;
    private Utils utils;
    private List<Integer> eatList = new ArrayList<Integer>() {{
//        add(306403);
        add(312424);
        add(309906);
        add(306408);
        add(310765);
        add(310766);
//        add(309905);
        add(312399);
    }};
    private Map<String, Integer> eatMap = new HashMap<String, Integer>() {{
        put("Молоко", 312424);
        put("Пицца \"Рыбацкая\"", 306408);
        put("Рыбный пирог", 309906);
        put("Мясная булка", 310765);
//        put("Зерно", 306403);
        put("Жаркое", 310766);
//        put("Рыба", 309905);
        put("Мясо", 312399);
    }};

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
                        work();
                        ws.getShortInfo(new Callback<JsonObject>() {
                            @Override
                            public void success(JsonObject jsonObject, Response response) {
                                JsonArray eatEffects = jsonObject.get("eatEffects").getAsJsonArray();
                                for (JsonElement e : eatEffects) {
                                    String name = e.getAsJsonObject().get("item").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
                                    eatList.remove(eatMap.get(name));
                                }
                                for (Integer i : eatList) {
                                    ws.eat(new TypedString("data%5BUserItem%5D%5Bitem_id%5D=" + i), new Callback<JsonObject>() {
                                        @Override
                                        public void success(JsonObject jsonObject, Response response) {
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                        }
                                    });
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });
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

    private void work() {
        ws.work(new TypedString("data%5BUser%5D%5Benergy%5D=100&data%5BCompany%5D%5Bid%5D=1123"),
                new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

    private void go1() {
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

    private void train() {
        ws.sport(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                JsonArray userSkills = jsonObject.get("user").getAsJsonObject().get("UserSportSkill").getAsJsonArray();
                List<UserSportSkill> skills = new ArrayList<>();
                for (JsonElement el : userSkills) {
                    skills.add(new Gson().fromJson(el, UserSportSkill.class));
                }
                UserSportSkill s = skills.get(0);
                for (int i = 1; i < skills.size(); ++i) {
                    if (skills.get(i).level < s.level || skills.get(i).level == s.level && Integer.parseInt(skills.get(i).experience) <= Integer.parseInt(s.experience)) {
                        s = skills.get(i);
                    }
                }
                TypedString ts = new TypedString("data%5BCoachProgram%5D%5Blicense_id%5D=80&data%5BCoachProgram%5D%5Bid%5D=94&data%5BSportActivity%5D%5Bsport_skill_type_id%5D=" + s.id + "&data%5BSportActivity%5D%5Benergy%5D=66");
                ws.train(ts, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    private void vote() {
        final ParseObject testObject = new ParseObject("Vote");
        ws.vote(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                testObject.put("status", "success");
                testObject.put("login", new Utils(context).loadLogin());
                testObject.saveInBackground();
                vote();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
