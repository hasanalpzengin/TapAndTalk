package com.hasanalpzengin.typeandtalk;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button addButton, readButton, addCategoryButton;
    ArrayList<Category> categories;
    TextInputLayout textInputLayout, categoryInputLayout;
    TextInputEditText textInputEditText, categoryInputEditText;
    DBOperations dbOperations;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog categoryAlertDialog;
    View addCategoryDialog;
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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        tabLayout.setOnCreateContextMenuListener(this);

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
            case R.id.addCategory:{
                categoryAlertDialog.show();
                return true;
            }
            case R.id.deleteCategory:{
                deleteCategory();
                return true;
            }
        }
        return false;
    }

    private void deleteCategory() {
        int currentPage = viewPager.getCurrentItem();

        dbOperations.open_writable();
        dbOperations.deleteCategory(categories.get(currentPage).title);
        categories = dbOperations.getCategories(lang);
        dbOperations.close_db();

        Intent intent = getIntent();
        finish();
        startActivity(intent);
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
                        String text = textInputEditText.getText().toString();
                        dbOperations.addText(text, categories.get(viewPager.getCurrentItem()).title);
                        dbOperations.close_db();
                        categories.get(viewPager.getCurrentItem()).updateAdapter();
                        Snackbar.make(viewPager ,text+" "+getResources().getString(R.string.textAdded), Toast.LENGTH_SHORT).show();
                        textInputEditText.setText("");
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

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoryName = categoryInputEditText.getText().toString();
                if (categoryName.length()>0){
                    if (!isCategoryExists(categoryName)){
                        addCategory(categoryName);
                    }
                }else{
                    categoryInputLayout.setErrorEnabled(true);
                    categoryInputLayout.setError(getResources().getString(R.string.inputError));
                }
            }
        });

        categoryInputEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                categoryInputLayout.setErrorEnabled(false);
                return false;
            }
        });

    }

    private void addCategory(String categoryName){
        dbOperations.open_writable();
        dbOperations.addCategory(categoryName, lang);
        categories = dbOperations.getCategories(lang);
        dbOperations.close_db();
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.categoryAdded), Toast.LENGTH_SHORT).show();
        viewPagerAdapter.updateAdapter(dbOperations, lang);

        categoryAlertDialog.dismiss();
    }

    private void init(){
        addButton = findViewById(R.id.addButton);
        readButton = findViewById(R.id.readButton);
        textInputEditText = findViewById(R.id.inputText);
        textInputLayout = findViewById(R.id.inputLayout);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        addCategoryButton = findViewById(R.id.addCategory);
        dbOperations = new DBOperations(getApplicationContext());
        categories = new ArrayList<>();

        alertDialogBuilder = new AlertDialog.Builder(this);
        addCategoryDialog = getLayoutInflater().inflate(R.layout.add_category_dialog, null);
        addCategoryButton = addCategoryDialog.findViewById(R.id.addCategory);
        categoryInputLayout = addCategoryDialog.findViewById(R.id.categoryInputLayout);
        categoryInputEditText = addCategoryDialog.findViewById(R.id.categoryInputEditText);


        alertDialogBuilder.setView(addCategoryDialog);
        categoryAlertDialog = alertDialogBuilder.create();

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        dbOperations.open_readable();

        categories = dbOperations.getCategories(lang);
        for (Category category: categories){
            category.setFavorites(dbOperations.getCategoryList(category.title));
            viewPagerAdapter.addCategory(category ,category.title);
        }

        dbOperations.close_db();

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    private boolean isCategoryExists(String categoryName) {
        for (Category category: categories){
            Log.i("Title = ", category.title);
            if (categoryName.contentEquals(category.title)){
                categoryInputLayout.setErrorEnabled(true);
                categoryInputLayout.setError(getResources().getString(R.string.isExist));
                return true;
            }
        }
        return false;
    }
}
