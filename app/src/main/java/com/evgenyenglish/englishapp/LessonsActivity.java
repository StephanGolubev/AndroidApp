package com.evgenyenglish.englishapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LessonsActivity extends AppCompatActivity {

    private TextView textViewResult;
    String URL = "http://kurchanovenglish.ru/data/";
    LinearLayout line_lesson;

    private String[] name = new String[18];
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";
    public static final String LESSON = "lessonKey";



    public  boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                return true;
            } else {

                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error","You already have the permission");
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        haveStoragePermission();

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);


        if (sharedpreferences.contains(Name)) {
            String name_login = (sharedpreferences.getString(Name, ""));
            Toast.makeText(LessonsActivity.this, "Вошли как " + name_login, Toast.LENGTH_SHORT).show();
        }


        final LinearLayout liner_all = new LinearLayout(this);
        final LinearLayout layout = new LinearLayout(this);
        final ScrollView scrollView = new ScrollView(this);

        liner_all.addView(layout);

        scrollView.addView(liner_all);

        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        params.height = 120;
        params.setMargins(200, 10, 200, 50);

        layout.setOrientation(LinearLayout.VERTICAL);
        liner_all.setHorizontalGravity(Gravity.CENTER);
        int i;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<List<Lesson>> call = jsonPlaceHolderApi.getPosts();



        call.enqueue(new Callback<List<Lesson>>() {


            @Override
            public void onResponse(Call<List<Lesson>> call, Response<List<Lesson>> response) {


                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                List<Lesson> lessons = response.body();

                int i = 0;
                for (final Lesson lesson : lessons) {
                    LinearLayout row = new LinearLayout(LessonsActivity.this);

                    for (int j = 0; j < 1; j++) {
                        Button button = new Button(LessonsActivity.this);
                        int free_lesson = 0;
                        int free = lesson.getFree();

                        if (free_lesson == free) {
                            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_lesson, 0, 0, 0);
                            button.setPadding(30, 0, 0, 0);
                            button.setCompoundDrawablePadding(-30 - getApplicationContext().getResources().getDrawable(R.drawable.lock_lesson).getIntrinsicWidth());
                        }

                        int int_pref = 0;
                        if (sharedpreferences.contains(LESSON)) {
                            int_pref = (sharedpreferences.getInt(LESSON, 0));
                        }


                        if (lesson.getNumber() > int_pref && free_lesson == free){
                            button.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("ShowToast")
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(LessonsActivity.this, PayActivity.class);
                                    startActivity(intent);
                                }
                            });
                            button.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.buttons_disabled));
                            button.setTextColor(Color.parseColor("#cacaca"));



                        }else if (lesson.getNumber() > int_pref){

                            button.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("ShowToast")
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(LessonsActivity.this, "Этот урок не доступен.", Toast.LENGTH_SHORT).show();
                                }
                            });

                            button.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.buttons_disabled));
                            button.setTextColor(Color.parseColor("#cacaca"));


                        }else if (lesson.getNumber() <= int_pref && free_lesson != free){
                            button.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("ShowToast")
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(LessonsActivity.this, TaskActivity.class);
                                    intent.putExtra("LESSON_NUMBER", lesson.getNumber());
                                    startActivity(intent);
                                }
                            });
                            button.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.button_active));

                        }else if (lesson.getNumber() <= int_pref && free_lesson == free){
                            button.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("ShowToast")
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(LessonsActivity.this, PayActivity.class);
                                    startActivity(intent);
                                }
                            });
                            button.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.button_active));

                        }

                        button.setText(lesson.getName());
                        button.setId(lesson.getNumber());
                        button.setLayoutParams(params);
                        button.setTextSize(20);
                        button.setPadding(0, 5, 0, 5);
                        Typeface tf= Typeface.createFromAsset(getAssets(),"font/Roboto/RobotoSlab-Light.ttf");
                        button.setTypeface(tf);


                        row.addView(button);
                    }
                    layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    layout.addView(row);

                }
//                setContentView(layout);
                LinearLayout linearLayout1 = findViewById(R.id.RootContainer);
                if (linearLayout1 != null) {
                    linearLayout1.addView(scrollView);


                    final String link = "http://kurchanovenglish.ru/data/";


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(link)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    JsonUpdate jsonUpdate = retrofit.create(JsonUpdate.class);

                    String email_update = (sharedpreferences.getString(Email, ""));
                    int int_pre = (sharedpreferences.getInt(LESSON, 0));
                    Call<List<Update>> call2 = jsonUpdate.getUpdate(email_update,int_pre);


                    call2.enqueue(new Callback<List<Update>>() {


                        @Override
                        public void onResponse(@NonNull Call<List<Update>> call2, @NonNull Response<List<Update>> response) {


                            if (!response.isSuccessful()) {
                                Log.d("yes", "The code" + response.code());

                                return;
                            }


                            Log.d("yes", "The code" + response.body());
                            final List<Update> updates = response.body();


                            assert updates != null;
                            for (final Update update : updates) {
                                Log.d("yes", "Response update" + update.getStatus());

                            }
                        }

                        @Override
                        public void onFailure( @NonNull Call<List<Update>> call2,@NonNull Throwable t) {
                            Log.d("yes", "The error" + t.getMessage());

                        }
                    });





                }

            }


            @Override
            public void onFailure(Call<List<Lesson>> call, Throwable t) {
                textViewResult.setText("Упс... Ошибка! Проверьте интернет соединение.");
            }
        });



    }
    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

}


