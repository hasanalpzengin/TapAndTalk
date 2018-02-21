package com.hasanalpzengin.typeandtalk;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {

    ImageView turkishButton, englishButton;
    Button keepDefault;
    String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        turkishButton = findViewById(R.id.turkishFlag);
        englishButton = findViewById(R.id.englishFlag);
        keepDefault = findViewById(R.id.defaultButton);

        initListeners();
    }

    private void initListeners() {

        turkishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lang = new String("tr");
                changeLang(getApplicationContext(),lang);
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);

            }
        });

        englishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lang = new String("en");
                changeLang(getApplicationContext(), lang);
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
            }
        });

        keepDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lang = new String("en");
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
            }
        });
    }

    public static Context changeLang(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("lang",lang).apply();

        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.N) {
            return context.createConfigurationContext(configuration);
        }else{
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            return context;
        }
    }
}
