package com.evgenyenglish.englishapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private Button LoginBtn;
        SharedPreferences sharedpreferences;
        public static final String mypreference = "mypref";
        public static final String Name = "nameKey";
        public static final String Email = "emailKey";
        public static final String LESSON = "lessonKey";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(Name)) {
            String name_pref = (sharedpreferences.getString(Name, ""));
            if (sharedpreferences.contains(Email)) {
                String email_pref = (sharedpreferences.getString(Email, ""));

                Intent intent = new Intent(LoginActivity.this, LessonsActivity.class);
                startActivity(intent);

            }
        }

        LoginBtn = (Button) findViewById(R.id.login_btn);

        final EditText InputEmail= (EditText) findViewById(R.id.login_email);
        final EditText InputPassword = (EditText) findViewById(R.id.login_password);


        final String link = "http://kurchanovenglish.ru/data/";

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginBtn.setEnabled(false);
                final String password = InputPassword.getText().toString();
                final String email = InputEmail.getText().toString();

                if (InputEmail.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "Неверно введена почта", Toast.LENGTH_SHORT).show();
                    LoginBtn.setEnabled(true);
                } else if (InputPassword.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "Неверно введен пароль", Toast.LENGTH_SHORT).show();
                    LoginBtn.setEnabled(true);
                } else {


                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(link)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    JsonLogin jsonLogin = retrofit.create(JsonLogin.class);

                    Call<List<Login>> call = ((JsonLogin) jsonLogin).getLogin(email, password);


                    call.enqueue(new Callback<List<Login>>() {


                        @Override
                        public void onResponse(Call<List<Login>> call, Response<List<Login>> response) {


                            if (!response.isSuccessful()) {

                                return;
                            }


                            List<Login> logins = response.body();


                            int i = 0;
                            for (Login login : logins) {

                                String vars = "yes";
                                String resp = login.getMes();
                                if (vars.equals(resp)) {


                                    sharedpreferences = getSharedPreferences(mypreference,
                                            Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor_pref = sharedpreferences.edit();
                                    editor_pref.putString(Name, login.getName());
                                    editor_pref.putString(Email, login.getEmail());
                                    editor_pref.putInt(LESSON, login.getLesson());
                                    editor_pref.apply();


                                    Toast.makeText(LoginActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, LessonsActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Неверный пароль или email", Toast.LENGTH_SHORT).show();
                                    LoginBtn.setEnabled(true);
                                }


                            }
                        }


                        @Override
                        public void onFailure(Call<List<Login>> call, Throwable t) {
                            LoginBtn.setEnabled(true);
                        }
                    });


                }
            }
        });







    }
}
