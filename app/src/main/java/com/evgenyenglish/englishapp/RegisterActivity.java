package com.evgenyenglish.englishapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RegisterActivity extends AppCompatActivity {

    private Button RegisterApp;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";
    public static final String LESSON = "lessonKey";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RegisterApp = (Button) findViewById(R.id.register_btn);

        final EditText InputName = (EditText) findViewById(R.id.register_name);

        final EditText InputEmail= (EditText) findViewById(R.id.register_email);

        final EditText InputPassword = (EditText) findViewById(R.id.register_password);

        final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        );


        final String link = "http://kurchanovenglish.ru/data/";

        RegisterApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterApp.setEnabled(false);
                final String name = InputName.getText().toString();
                final String email = InputEmail.getText().toString();
                final String password = InputPassword.getText().toString();

                Boolean bool = EMAIL_ADDRESS_PATTERN.matcher(email).matches();
                if (!bool){
                    Toast.makeText(RegisterActivity.this, "Почта не верная!", Toast.LENGTH_SHORT).show();
                    RegisterApp.setEnabled(true);
                }else if (password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Пароль слишком короткий!", Toast.LENGTH_SHORT).show();
                    RegisterApp.setEnabled(true);
                }else {




                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(link)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                JsonRegister jsonRegister = retrofit.create(JsonRegister.class);


                String token = FirebaseInstanceId.getInstance().getToken();

                Call<List<Register>> call = jsonRegister.getRegister(name,email,password, token);


                call.enqueue(new Callback<List<Register>>() {


                    @Override
                    public void onResponse(Call<List<Register>> call, Response<List<Register>> response) {


                        if (!response.isSuccessful()) {

                            return;
                        }


                        List<Register> registers = response.body();



                        int i = 0;
                        for (Register register : registers) {

                            String vars = "yes";
                            String resp = register.getMes();
                            if (vars.equals(resp)){


                                sharedpreferences = getSharedPreferences(mypreference,
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor_pref = sharedpreferences.edit();
                                editor_pref.putString(Name, register.getName());
                                editor_pref.putString(Email, register.getEmail());
                                editor_pref.putInt(LESSON, register.getLesson());
                                editor_pref.apply();

                                Toast.makeText(RegisterActivity.this, "Text loaded", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LessonsActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(RegisterActivity.this, "Почта занята, попробуйте другую!", Toast.LENGTH_SHORT).show();
                                RegisterApp.setEnabled(true);
                            }


                        }
                    }


                    @Override
                    public void onFailure(Call<List<Register>> call, Throwable t) {
                        RegisterApp.setEnabled(true);

                    }
                });



            }
        }

        });

    }

}


