package ru.egor_d.vircitiesbot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;


public class LoginActivity extends Activity {
    @InjectView(R.id.login_button)
    protected Button loginButton;
    @InjectView(R.id.login)
    protected EditText loginET;
    @InjectView(R.id.password)
    protected EditText passwordET;
    @InjectView(R.id.waiting_screen)
    protected View waitingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);

        final Utils utils = new Utils(this);
        loginET.setText(utils.loadLogin());
        passwordET.setText(utils.loadPassword());
        waitingScreen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        final WebService ws = new RestAdapter.Builder().setEndpoint(Utils.API).setLogLevel(RestAdapter.LogLevel.FULL).build().create(WebService.class);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitingScreen.setVisibility(View.VISIBLE);
                utils.saveLogin(loginET.getText().toString());
                utils.savePassword(passwordET.getText().toString());
                TypedString s = new TypedString(String.format("data%%5BUser%%5D%%5Busername%%5D=%s&data%%5BUser%%5D%%5Bpassword%%5D=%s", utils.loadLogin(), utils.loadPassword()));
                ws.auth(s, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject s, Response response) {
                        ws.check(new Callback<JsonObject>() {
                            @Override
                            public void success(JsonObject s, Response response) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(LoginActivity.this, String.format("Error while check: %s", error.getMessage()), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(LoginActivity.this, String.format("Error while login: %s", error.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
