package com.evgenyenglish.englishapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.eyalbira.loadingdots.LoadingDots;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.ohoussein.playpause.PlayPauseView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.DIRECTORY_MUSIC;
import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class TaskActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int i_recording = 1;
    private int i_image = 1;
    public static final String mypreference = "mypref";
    final int[] cash_mp3 = {1};
    final boolean[] next_itter = {true};
    final boolean[] status_repeat = {true};

    private String say_text_check = "";
    private boolean check_open = true;
    private List<Uri> uriList = new ArrayList<>();
    public static final String LESSON = "lessonKey";
    private StorageReference storageReference;
    volatile int i = 0; // define as a global variable
    Timer timer; // define as a global variable
    MessageAdapter adapter2;


    SpeechRecognizer mSpeechRecogniser;
    Intent mSpeechRecogniserIntent;

    class Loader extends AsyncTask<Void, Void, Void> {
        private AlertDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                status_repeat[0] = false;
                while (cash_mp3[0] != -1) {
                    if (next_itter[0]) {
                        next_itter[0] = false;

                        Log.d("Check download", "Get " + String.valueOf(cash_mp3[0]) + "r.mp3");

                        storageReference.child(cash_mp3[0] + "r.mp3").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                try {
                                    DownloadManager.Request request=new DownloadManager.Request(uri)// Description of the Download Notification
                                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                                            .setDestinationInExternalPublicDir(DIRECTORY_MUSIC, cash_mp3[0] + "r.mp3");// Uri of the destination file
                                    DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                                    cash_mp3[0]++;

                                    long downloadID = downloadManager.enqueue(request);

                                    Log.d("Download", String.valueOf(cash_mp3[0] - 1));
                                    next_itter[0] = true;
                                } catch (IllegalStateException ex) {
                                    Log.d("Error1", "BUGGGGGGG");
                                    ex.printStackTrace();
                                } catch (Exception ex) {
                                    // just in case, it should never be called anyway
                                    Log.d("Error2", "BUGGGGGGG");
                                    ex.printStackTrace();
                                }

                                uriList.add(uri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                cash_mp3[0] = -1;
                                status_repeat[0] = true;
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final SharedPreferences sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        final int int_lesson_global = (sharedpreferences.getInt(LESSON, 0));

        mSpeechRecogniser = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecogniserIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecogniserIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        mSpeechRecogniserIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        final View view = factory.inflate(R.layout.spiner, null);

        final AlertDialog dialog = new AlertDialog.Builder(TaskActivity.this, R.style.CustomDialog).setView(view).setCancelable(false).create();
        TextView textView = view.findViewById(R.id.textView);
        textView.setText("Загрузка урока...");
        dialog.setView(view);
        dialog.show();

        mSpeechRecogniser.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {


            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null) {
                    say_text_check = matches.get(0);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });


        final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        final int[] old_i = {-1}; // Отвечает за задежрку в показывании сообщений
        final boolean[] status_voice = {true};
        TextView name_lesson = (TextView) findViewById(R.id.name_lesson);
        final Handler handler = new Handler();

        name_lesson.setTypeface(Typeface.createFromAsset(getAssets(), "font/Roboto/RobotoSlab-Light.ttf"));

        final ListView listV = findViewById(R.id.list);
        listV.setSmoothScrollbarEnabled(true);

        listV.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);


        final ArrayList<Task> tasks_arr = new ArrayList<>();


        adapter2 = new MessageAdapter(getApplicationContext());

        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter2);
        swingBottomInAnimationAdapter.setAbsListView(listV);
        listV.setAdapter(swingBottomInAnimationAdapter);

        timer = new Timer();

        final int number = Objects.requireNonNull(getIntent().getExtras()).getInt("LESSON_NUMBER");

        storageReference = firebaseStorage.getReferenceFromUrl("gs://englishapp-3c0ab.appspot.com/Lesson " + String.valueOf(number) + "/");

        name_lesson.setText("Урок " + number + ". Тренер по английскому");


        final String link = "http://kurchanovenglish.ru/data/";


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(link)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonTask jsonTask = retrofit.create(JsonTask.class);

        Call<List<Task>> call = jsonTask.getTask(number);


        call.enqueue(new Callback<List<Task>>() {


            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {


                if (!response.isSuccessful()) {

                    return;
                }


                final List<Task> tasks = response.body();


                int i = 0;
                for (final Task task : tasks) {


                    tasks_arr.add(task);

                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {

            }
        });

        final int the_time_bigger[] = {0};

        int delay = 1000; // delay for 5 sec.
        int period = 1000; // repeat every 5 secs.
        final int[] check_repeat = {0}; // Количество раз сколько можно повторить прослушивание (229 строка)
        final int[] check_think = {0};
        final boolean[] check_down_list = {false};

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {
                try {
                    if (adapter2.messages.get(position).check_recording != -1) {
                        if (adapter2.messages.get(position).check_repeat == 4) {
                            mediaPlayer.stop();

                            for (int i1 = 0; i1 < listV.getCount(); i1++) {
                                try {
                                    if (adapter2.messages.get(i1).getType().equalsIgnoreCase("recording"))
                                        adapter2.messages.get(i1).check_repeat = 4;

                                    listV.getChildAt(i1).findViewById(R.id.repeat_list).setVisibility(View.VISIBLE);
                                    listV.getChildAt(i1).findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                                } catch (Exception ignore) {
                                }
                            }

                            String filePath = getExternalStoragePublicDirectory(DIRECTORY_MUSIC).toString() + "/" + String.valueOf(adapter2.messages.get(position).check_recording) + "r.mp3";

                            mediaPlayer = new MediaPlayer();

                            try {
                                mediaPlayer.setDataSource(filePath);
                            } catch (IOException e) {
                                Log.d("ERRRR", e.getMessage() + "\n" + filePath);
                            }

                            status_repeat[0] = false;
                            try {
                                if (adapter2.messages.get(adapter2.getCount() - 1).getType().equalsIgnoreCase("listen") || adapter2.messages.get(adapter2.getCount() - 1).getType().equalsIgnoreCase("listen_stop") || adapter2.messages.get(adapter2.getCount() - 1).getType().equalsIgnoreCase("listen_stop_button")) {
                                    check_repeat[0] = 0;
                                    mSpeechRecogniser.stopListening();
                                    say_text_check = "";
                                    TaskEnter task_enter = new TaskEnter(tasks_arr.get(i).getText(), "listen_stop_button", say_text_check);
                                    adapter2.messages.remove(adapter2.getCount() - 1);
                                    adapter2.add(task_enter);
                                    adapter2.messages.get(adapter2.getCount() - 1).check_repeat = 10; // число паузы цикла
                                    status_voice[0] = false;
                                }

                                mediaPlayer.prepare();
                                mediaPlayer.start();
                                adapter2.messages.get(position).check_repeat = 0;


                                try {
                                    listV.getChildAt(position).findViewById(R.id.repeat_list).setVisibility(View.INVISIBLE);
                                    listV.getChildAt(position).findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                } catch (Exception ignore) {
                                }
                                Log.d("step", "2");

                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        Log.d("step", "3");

                                        for (int i1 = 0; i1 < listV.getCount(); i1++) {
                                            try {
                                                listV.getChildAt(i1).findViewById(R.id.repeat_list).setVisibility(View.VISIBLE);
                                                listV.getChildAt(i1).findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                                                adapter2.messages.get(i1).check_repeat = 4;
                                            } catch (Exception ignore) {
                                            }
                                        }

                                        Log.d("step", "4");
                                        if (old_i[0] == i && adapter2.messages.get(adapter2.getCount() - 1).getType().equalsIgnoreCase("recording"))
                                            i++;

                                        if (adapter2.messages.get(adapter2.getCount() - 1).getType().equalsIgnoreCase("listen") || adapter2.messages.get(adapter2.getCount() - 1).getType().equalsIgnoreCase("listen_stop") || adapter2.messages.get(adapter2.getCount() - 1).getType().equalsIgnoreCase("listen_stop_button")) {
                                            adapter2.messages.get(adapter2.getCount() - 1).check_repeat = 1; // старт recognizer
                                            check_repeat[0] = 0;
                                            status_voice[0] = true;
                                            Log.d("step", "5");
                                            if (old_i[0] == i && adapter2.messages.get(adapter2.getCount() - 1).getType().equalsIgnoreCase("listen_stop"))
                                                i++;
                                        }
                                        status_repeat[0] = true;
                                    }
                                });
                            } catch (Exception ignore) {
                            }
                        } else {
                            final TextView textView = listV.getChildAt(position).findViewById(R.id.repeat_list);
                            final LoadingDots progressBar = listV.getChildAt(position).findViewById(R.id.progressBar);

                            textView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);

                            for (int i1 = 0; i1 < listV.getCount(); i1++) {
                                try {
                                    if (adapter2.messages.get(i1).getType().equalsIgnoreCase("recording"))
                                        adapter2.messages.get(i1).check_repeat = 4;

                                    listV.getChildAt(i1).findViewById(R.id.repeat_list).setVisibility(View.VISIBLE);
                                    listV.getChildAt(i1).findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                                } catch (Exception ignore) {
                                }
                            }

                            mediaPlayer.stop();
                            status_repeat[0] = true;
                        }
                    } else if (adapter2.messages.get(position).getType().equals("listen_stop_button")) {
                        mediaPlayer.stop();

                        for (int i1 = 0; i1 < listV.getCount(); i1++) {
                            try {
                                if (adapter2.messages.get(i1).getType().equalsIgnoreCase("recording"))
                                    adapter2.messages.get(i1).check_repeat = 4;

                                listV.getChildAt(i1).findViewById(R.id.repeat_list).setVisibility(View.VISIBLE);
                                listV.getChildAt(i1).findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                            } catch (Exception ignore) {
                            }
                        }

                        PlayPauseView playPauseView = view.findViewById(R.id.button_rep);

                        Log.d("repeat", "fewa");

                        status_repeat[0] = true;
                        playPauseView.toggle(true);

                        check_repeat[0] = 0;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapter2.messages.get(adapter2.getCount() - 1).check_repeat = 1; // старт recognizer
                                status_voice[0] = true;
                            }
                        }, 400);
                    }
                } catch (Exception ignore) {
                }
            }
        });

        Loader loader = new Loader();
        loader.execute();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (check_open) {
                                    boolean download = false; // ���� ��������

                                    if (check_down_list[0] == true) {
                                        try {
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listV.smoothScrollToPosition(listV.getCount() - 1);
                                                }}, 150);
                                            check_down_list[0] = false;
                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listV.smoothScrollToPosition(listV.getCount() - 1);
                                                }}, 150);
                                        } catch (Exception ignore) {}
                                    }

                                    if (old_i[0] == i && status_voice[0]) {
                                        if (adapter2.messages.get(adapter2.getCount() - 1).check_button) { // ���� ������� �����
                                            adapter2.messages.get(adapter2.getCount() - 1).check_button = false;
                                            i++;
                                        } else if (adapter2.messages.get(adapter2.getCount() - 1).check_repeat == 3) {
                                            i++;
                                        } else if (adapter2.messages.get(adapter2.getCount() - 1).check_repeat == 2) { // ����� �������������
                                            if (adapter2.messages.get(adapter2.getCount() - 1).getType().equals("listen_stop")) {
                                                i += 2;
                                                check_repeat[0] = 0;
                                            }
                                            else
                                                i++;
                                        } else if (adapter2.messages.get(adapter2.getCount() - 1).check_repeat == 1 || adapter2.messages.get(adapter2.getCount() - 1).check_repeat == 5 || adapter2.messages.get(adapter2.getCount() - 1).check_repeat1 == 9) { // ������������ ������������ ��������� �������������
                                            if (check_repeat[0] == 2) {
                                                TaskEnter task_enter = new TaskEnter(tasks_arr.get(i).getText(), "listen_stop", say_text_check);
                                                adapter2.messages.remove(adapter2.getCount() - 1);
                                                adapter2.add(task_enter);
                                                handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listV.smoothScrollToPosition(listV.getCount() - 1);
                                                }}, 150);
                                                i += 2; // += 2 �.�. ���������� listen_stop
                                                adapter2.notifyDataSetChanged();
                                                check_repeat[0] = 0;
                                            } else {
                                                mSpeechRecogniser.startListening(mSpeechRecogniserIntent);
                                                for (int i1 = 0; i1 < listV.getCount(); i1++) {
                                                    try {
                                                        if (adapter2.messages.get(i1).getType().equalsIgnoreCase("recording"))
                                                            adapter2.messages.get(i1).check_repeat = 4;

                                                        listV.getChildAt(i1).findViewById(R.id.repeat_list).setVisibility(View.VISIBLE);
                                                        listV.getChildAt(i1).findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                                                    } catch (Exception ignore) {}
                                                }

                                                TaskEnter task_enter = new TaskEnter(tasks_arr.get(i).getText(), "listen", tasks_arr.get(i).getSay(), mSpeechRecogniser);
                                                adapter2.messages.remove(adapter2.getCount() - 1);
                                                adapter2.add(task_enter);
                                                handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listV.smoothScrollToPosition(listV.getCount() - 1);
                                                }}, 150);
                                                check_repeat[0]++;
                                            }
                                        } else if (adapter2.messages.get(adapter2.getCount() - 1).check_repeat == 6) {
                                            mSpeechRecogniser.stopListening();
                                            TaskEnter task_enter = new TaskEnter(tasks_arr.get(i).getText(), "listen_stop", say_text_check);
                                            adapter2.messages.remove(adapter2.getCount() - 1);
                                            adapter2.add(task_enter);
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listV.smoothScrollToPosition(listV.getCount() - 1);
                                                }}, 150);
                                        } else if (check_think[0] == 5) {
                                            i++;
                                            check_think[0] = 0;
                                            TaskEnter task_enter = new TaskEnter(tasks_arr.get(i).getText(), "think", false);
                                            adapter2.messages.remove(adapter2.getCount() - 1);
                                            adapter2.add(task_enter);
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listV.smoothScrollToPosition(listV.getCount() - 1);
                                                }}, 150);
                                        } else if (check_think[0] != 0)
                                            check_think[0]++;
                                    } else if (the_time_bigger[0] == 5) {
                                        i++;
                                        the_time_bigger[0] = 0;
                                        TaskEnter task_enter1 = new TaskEnter(tasks_arr.get(i).getText(), "think", false);
                                        adapter2.messages.remove(adapter2.getCount() - 1);
                                        adapter2.add(task_enter1);
                                    } else if (the_time_bigger[0] != 0) {
                                        the_time_bigger[0]++;
                                    }

                                    if (i < tasks_arr.size() && old_i[0] != i && status_repeat[0]) {
                                        if (dialog.isShowing())
                                            dialog.dismiss();
                                        old_i[0] = i;
                                        TaskEnter task_enter = new TaskEnter("null", "null", 0);
                                        String the_type = tasks_arr.get(i).getType();
                                        String the_type_text = "text";
                                        String the_type_recording = "recording";
                                        String the_type_button = "button";
                                        String the_type_image = "image";
                                        String the_think_type = "think";
                                        String the_type_listen = "listen";
                                        String the_type_listen_stop = "listen_stop";
                                        String type_end = "end_lesson";


                                        if (the_type.equals(the_type_text) && !tasks_arr.get(i).getText().equals(type_end)) {
                                            task_enter = new TaskEnter(tasks_arr.get(i).getText(), "text", tasks_arr.get(i).getSay());
                                            i++;
                                        } else if (the_type.equals(the_type_recording)) {
                                            download = true;
                                        } else if (the_type.equals(the_type_image)) {
                                            download = true;
                                            check_down_list[0] = true;
                                            storageReference.child(i_image + "i.gif").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    TaskEnter task_enter = new TaskEnter(tasks_arr.get(i).getText(), "image", uri, true);
                                                    adapter2.add(task_enter);
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            listV.smoothScrollToPosition(listV.getCount() - 1);
                                                        }
                                                    }, 150);
                                                    i_image++;
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    storageReference.child(i_image + "i.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            TaskEnter task_enter = new TaskEnter(tasks_arr.get(i).getText(), "image", uri, false);
                                                            adapter2.add(task_enter);
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    listV.smoothScrollToPosition(listV.getCount() - 1);
                                                                }
                                                            }, 150);
                                                            i_image++;
                                                        }
                                                    });
                                                }
                                            });
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listV.smoothScrollToPosition(listV.getCount() - 1);
                                                }
                                            }, 150);
                                        } else if (the_type.equals(the_type_button)) {
                                            task_enter = new TaskEnter(tasks_arr.get(i).getText(), tasks_arr.get(i).getType(), tasks_arr.get(i).getSay());
                                        } else if (the_type.equals(the_think_type)) {
                                            task_enter = new TaskEnter(tasks_arr.get(i).getText(), "think", true);
                                            check_think[0] = 1;
                                        } else if (the_type.equals(the_type_listen)) {
                                            mSpeechRecogniser.startListening(mSpeechRecogniserIntent);
                                            task_enter = new TaskEnter(tasks_arr.get(i).getText(), "listen", tasks_arr.get(i).getSay(), mSpeechRecogniser);
                                        } else if (tasks_arr.get(i).getText().equals(type_end)) {

                                            if (int_lesson_global <= number) {
                                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                                editor.putInt("lessonKey", number + 1);
                                                editor.commit();
                                            }

                                            task_enter = new TaskEnter(tasks_arr.get(i).getText(), "end", tasks_arr.get(i).getSay());

                                        } else {
                                            task_enter = new TaskEnter("null", "null", 0);
                                            i++;
                                        }

                                        if (the_type.equals(the_type_recording)) {
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listV.smoothScrollToPosition(listV.getCount() - 1);
                                                }
                                            }, 150);

                                            try {
                                                if (mediaPlayer.isPlaying())
                                                    mediaPlayer.stop();
                                            } catch (Exception ignore) { }

                                            try {
                                                for (int i1 = 0; i1 < listV.getCount(); i1++) {
                                                    try {
                                                        if (adapter2.messages.get(i1).getType().equalsIgnoreCase("recording") && i1 != i) {
                                                            adapter2.messages.get(i1).check_repeat = 4;

                                                            listV.getChildAt(i1).findViewById(R.id.repeat_list).setVisibility(View.VISIBLE);
                                                            listV.getChildAt(i1).findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                                                        }
                                                    } catch (Exception ignore) {
                                                    }
                                                }
                                            } catch (Exception ignore) {
                                            }

                                            task_enter = new TaskEnter(tasks_arr.get(i).getType());
                                            task_enter.check_recording = i_recording;
                                            Log.d("CheckRecording", String.valueOf(i_recording));
                                            adapter2.add(task_enter);

                                            try {
                                                String filePath = getExternalStoragePublicDirectory(DIRECTORY_MUSIC).toString() + "/" + String.valueOf(i_recording) + "r.mp3";

                                                Log.d("FilePath",getExternalFilesDir(DIRECTORY_MUSIC).toString() + "/" + String.valueOf(i_recording) + "r.mp3" );

                                                mediaPlayer = new MediaPlayer();

                                                try {
                                                    mediaPlayer.setDataSource(filePath);
                                                    i_recording++;

                                                } catch (IOException e) {
                                                    Log.d("ERRRR1", e.getMessage() + "\n" + filePath);
                                                }
                                                mediaPlayer.prepare();
                                                mediaPlayer.start();

                                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                    @Override
                                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                                        adapter2.messages.get(adapter2.getCount() - 1).check_repeat = 4;
                                                        i++;
                                                    }
                                                });
                                            } catch (Exception ignore) {
                                            }
                                        }

                                        if (!download && !the_type.equals(the_type_listen_stop)) {
                                            if (the_type.equals(the_type_listen_stop))
                                                adapter2.messages.remove(adapter2.getCount() - 1);
                                            adapter2.add(task_enter);
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listV.smoothScrollToPosition(listV.getCount() - 1);
                                                }}, 150);
                                        }
                                        handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listV.smoothScrollToPosition(listV.getCount() - 1);
                                                }}, 150);
                                    }
                                }
                            } catch (Exception ignore) {
                                if (i == tasks_arr.size()) {
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putInt("lessonKey", number + 1);
                                    editor.commit();
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    if (i == tasks_arr.size()) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putInt("lessonKey", number + 1);
                        editor.commit();
                    }
                    e.printStackTrace();
                }
            }
        }, delay, period);
    }

    @Override
    protected void onStop() {
        mSpeechRecogniser.stopListening();
        super.onStop();
//        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Ask the user if they want to quit
            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setIcon(R.drawable.the_warning)
                    .setTitle("Вы действительно хотите выйти?")
                    .setMessage("После выхода урок будет потерян. Продолжить?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Stop the activity
                            TaskActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Нет", null)
                    .show();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    protected void onResume() {
        check_open = true;

        try {
            if (adapter2.messages.get(adapter2.getCount() - 1).getType().equals("listen") || adapter2.messages.get(adapter2.getCount() - 1).getType().equals("listen_stop")) {
                adapter2.messages.get(adapter2.getCount() - 1).check_repeat = 5;
            }

            if (adapter2.messages.get(adapter2.getCount() - 1).getType().equals("recording"))
                mediaPlayer.start();
        } catch (Exception ignore) {}
        super.onResume();
    }

    @Override
    protected void onPause() {
        check_open = false;
        mSpeechRecogniser.stopListening();
        mediaPlayer.pause();
        super.onPause();
    }
}