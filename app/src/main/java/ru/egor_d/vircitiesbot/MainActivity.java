package ru.egor_d.vircitiesbot;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Egor Danilin on 25.03.2015.
 */
public class MainActivity extends Activity {
    @InjectView(R.id.start)
    protected Button mStartButton;
    @InjectView(R.id.stop)
    protected Button mStopButton;
    @InjectView(R.id.avatar)
    protected ImageView mAvatarIV;
    @InjectView(R.id.username)
    protected TextView mUsernameTV;
    @InjectView(R.id.vd)
    protected TextView mVdTV;
    @InjectView(R.id.energy)
    protected TextView mEnergyTV;
    @InjectView(R.id.prestige)
    protected TextView mPrestigeTV;
    @InjectView(R.id.city_name)
    protected TextView mCityNameTV;
    @InjectView(R.id.delta)
    protected TextView mDeltaTV;

    private List<Integer> eatList = new ArrayList<Integer>() {{
//        add(306403);
        add(310970);
        add(309906);
        add(306408);
        add(310765);
        add(310766);
//        add(309905);
        add(310791);
    }};
    private Map<String, Integer> eatMap = new HashMap<String, Integer>() {{
        put("Молоко", 310970);
        put("Пицца \"Рыбацкая\"", 306408);
        put("Рыбный пирог", 309906);
        put("Мясная булка", 310765);
//        put("Зерно", 306403);
        put("Жаркое", 310766);
//        put("Рыба", 309905);
        put("Мясо", 310791);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        final Utils utils = new Utils(this);
        mStopButton.setEnabled(utils.loadStarted());
        mStartButton.setEnabled(!utils.loadStarted());

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartButton.setEnabled(false);
                mStopButton.setEnabled(true);
                utils.saveStarted(true);
                utils.registerAlarm(MainActivity.this, 21 * Utils.MINUTE);
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartButton.setEnabled(true);
                mStopButton.setEnabled(false);
                utils.saveStarted(false);
                utils.unregisterAlarm(MainActivity.this);
            }
        });

        final WebService ws = new RestAdapter.Builder().setEndpoint(Utils.API).setLogLevel(RestAdapter.LogLevel.FULL).build().create(WebService.class);
        ws.getShortInfo(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                String avatar = jsonObject.get("user").getAsJsonObject().get("User").getAsJsonObject().get("avatar").getAsString();
                Picasso.with(MainActivity.this).load("http://api.vircities.com/img/avatars/" + avatar + ".png").into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(getResources(), Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getWidth()));
                        d.setCornerRadius(bitmap.getWidth());
                        mAvatarIV.setImageDrawable(d);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }


}
