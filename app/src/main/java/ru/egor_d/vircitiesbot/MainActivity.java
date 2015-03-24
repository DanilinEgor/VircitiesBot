package ru.egor_d.vircitiesbot;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;


public class MainActivity extends ActionBarActivity {

    private Button startButton;
    private Button stopButton;
    private EditText loginET;
    private EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.start);
        stopButton = (Button) findViewById(R.id.stop);
        loginET = (EditText) findViewById(R.id.login);
        passwordET = (EditText) findViewById(R.id.password);

        loginET.setText(loadLogin());
        passwordET.setText(loadPassword());

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        stopButton.setEnabled(loadStarted());
        startButton.setEnabled(!loadStarted());

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                saveLogin(loginET.getText().toString());
                savePassword(passwordET.getText().toString());
                saveStarted(true);
                Intent intent = new Intent(MainActivity.this, MoneyService.class);
                intent.putExtra("start", true);
                startService(intent);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                saveStarted(false);
                Intent intent = new Intent(MainActivity.this, MoneyService.class);
                intent.putExtra("start", false);
                startService(intent);
            }
        });

    }

    public String loadLogin() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString("login", "");
    }

    public void saveLogin(String s) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("login", s).commit();
    }

    public String loadPassword() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString("password", "");
    }

    public void savePassword(String s) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("password", s).commit();
    }

    public boolean loadStarted() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("started", false);
    }

    public void saveStarted(boolean b) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("started", b).commit();
    }
}
