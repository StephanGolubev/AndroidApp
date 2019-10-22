package com.evgenyenglish.englishapp;

import android.net.Uri;
import android.speech.SpeechRecognizer;

public class TaskEnter {

    private SpeechRecognizer speechRecognizer;

    private String text;

    private String type;

    private String parse_;

    private int say;

    private String number; // number lessons

    private int download;

    private boolean gif;

    private Uri uri; // URI на изображения/записи

    public boolean check_button = false;
    public int check_repeat = -1;
    public int check_repeat1 = 0;
    public int check_recording = -1;

    public TaskEnter(String type_in) {
        type = type_in;
    }

    public TaskEnter(String text_in, String type_in, Uri uri1, boolean check_gif) {
        text = text_in;
        type = type_in;
        uri = uri1;
        gif = check_gif;
    }

    public TaskEnter(String text_in, String type_in, boolean check_gif) {
        text = text_in;
        type = type_in;
        gif = check_gif;
    }

    public TaskEnter(String text_in, String type_in, String number1, int uri_download) {
        text = text_in;
        type = type_in;
        number = number1;
        download = uri_download;
    }

    public TaskEnter(String text_in, String type_in, int say_in) {
        text = text_in;
        type = type_in;
        say = say_in;
    }

    public TaskEnter(String text_in, String type_in, String say_in) {
        text = text_in;
        type = type_in;
        parse_ = say_in;
    }

    public TaskEnter(String text_in, String type_in, int say_in, SpeechRecognizer speechRecognizer1) {
        text = text_in;
        type = type_in;
        say = say_in;
        speechRecognizer = speechRecognizer1;
    }

    public int getDownload() {
        return download;
    }

    public String getNumber() {
        return number;
    }

    public Uri getUri() {
        return uri;
    }

    public boolean isGif() {
        return gif;
    }

    public void setParse_(String parse_) {
        this.parse_ = parse_;
    }

    public SpeechRecognizer getSpeechRecognizer() {
        return speechRecognizer;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public String getParse_() {
        return parse_;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int isSay() {
        return say;
    }

    public void setSay(int say) {
        this.say = say;
    }
}
