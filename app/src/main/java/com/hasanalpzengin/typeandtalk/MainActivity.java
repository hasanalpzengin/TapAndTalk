package com.hasanalpzengin.typeandtalk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button addButton, readButton;
    ArrayList<Category> categories;
    TextInputLayout textInputLayout;
    TextInputEditText textInputEditText;
    DBOperations dbOperations;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    private TextToSpeech textToSpeech;
    String lang;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //set selected language
        lang = sharedPreferences.getString("lang","en");
        LanguageActivity.changeLang(getApplicationContext(),lang);

        setContentView(R.layout.activity_main);

        //init layouts
        init();
        //init listeners
        initListener();

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    int result = textToSpeech.setLanguage(new Locale(Locale.getDefault().getLanguage()));

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("textToSpeech", "This LanguageActivity is not supported");
                    }
                } else {
                    Log.e("textToSpeech", "Initilization Failed!");
                }
            }
        });

        //Log.i("MainActivity","Item count ="+categories.get(0).recyclerView.getAdapter().getItemCount());
    }



    @Override
    protected void onResume() {
        super.onResume();

        lang = sharedPreferences.getString("lang","en");

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    int result = textToSpeech.setLanguage(new Locale(Locale.getDefault().getLanguage()));

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("textToSpeech", "This LanguageActivity is not supported");
                    }
                } else {
                    Log.e("textToSpeech", "Initilization Failed!");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.changeLanguage:{
                Intent language = new Intent(getApplicationContext(), LanguageActivity.class);
                startActivity(language);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech!=null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    private void initListener() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textInputEditText.getText().toString().length()>0) {
                    if (!categories.get(viewPager.getCurrentItem()).isExists(textInputEditText.getText().toString())){
                        dbOperations.open_writable();
                        dbOperations.addText(textInputEditText.getText().toString(), categories.get(viewPager.getCurrentItem()).title);
                        dbOperations.close_db();
                        categories.get(viewPager.getCurrentItem()).updateAdapter();
                    }else{
                        textInputLayout.setError(getResources().getString(R.string.isExist));
                    }
                }else{
                    textInputLayout.setError(getResources().getString(R.string.inputError));
                }
            }
        });

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(textInputEditText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        textInputEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                textInputLayout.setErrorEnabled(false);
                return false;
            }
        });
    }

    private void init(){
        addButton = findViewById(R.id.addButton);
        readButton = findViewById(R.id.readButton);
        textInputEditText = findViewById(R.id.inputText);
        textInputLayout = findViewById(R.id.inputLayout);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        dbOperations = new DBOperations(getApplicationContext());

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        String[] categoriesArray;
        if (lang.contentEquals("tr_TR")){
            categoriesArray = getApplicationContext().getResources().getStringArray(R.array.katagoriler);
        }else{
            categoriesArray = getApplicationContext().getResources().getStringArray(R.array.categories);
        }

        categories = new ArrayList<>();

        dbOperations.open_readable();
        for (String value: categoriesArray){
            Category category = new Category();
            category.setTitle(value);
            category.setFavorites(dbOperations.getCategoryList(value));
            categories.add(category);
            viewPagerAdapter.addCategory(category ,value);
        }
        dbOperations.close_db();

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }
}
