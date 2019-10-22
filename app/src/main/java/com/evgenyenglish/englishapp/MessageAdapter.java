package com.evgenyenglish.englishapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.eyalbira.loadingdots.LoadingDots;
import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter;
import com.ohoussein.playpause.PlayPauseView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class MessageAdapter extends BaseAdapter {

    String say_recognizer = "";
    List<TaskEnter> messages = new ArrayList<>();
    List<TextView2> textViewList = new ArrayList<>();

    Typeface tf;
    Context context;
    int max_i = -1;
    final Animation animAplha;

    private class TextView2 {
        public TextView textView;
        public int i;

        public TextView2(TextView textView, int i) {
            this.i = i;
            this.textView = textView;
        }
    }


    public MessageAdapter(Context context) {
        this.context = context;
        tf= Typeface.createFromAsset(context.getAssets(),"font/Roboto/RobotoSlab-Light.ttf");
        animAplha = AnimationUtils.loadAnimation(context, R.anim.alpha);
    }

    public static final Spannable getColoredString(CharSequence text, int color) {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(color), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public void add(TaskEnter message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "WrongConstant"})
    @Override
    public View getView(int i, View convertView, ViewGroup parent) { // По факту главная функция адаптера
        final int position = i;

        convertView = null;

        final MessageViewHolder holder = new MessageViewHolder(); // View на Activity

        Log.d("status", String.valueOf(i));

        LayoutInflater messageInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final TaskEnter message = messages.get(i); // То, что мы передаем в TaskEnter, можно доставать от сюда

        if (message.getType().equals("text")) {
            convertView = messageInflater.inflate(R.layout.task_text, null);

            holder.message = convertView.findViewById(R.id.txtview);
            holder.message.setTypeface(tf);
            holder.message.setText(message.getText());
            convertView.setTag(holder);
        }
        else if (message.getType().equals("button")) {
            if (i >= max_i){
                convertView = messageInflater.inflate(R.layout.task_button_mes, null);
                holder.button = convertView.findViewById(R.id.button2);
                holder.button.setTypeface(tf);
                holder.button.setText(message.getText());

//                final Animation startAnimation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.blinking_animation);
//                holder.button.startAnimation(startAnimation);
                ShimmerLayout shimmerText = (ShimmerLayout) convertView.findViewById(R.id.shimmer_text);
                shimmerText.startShimmerAnimation();

                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        messages.get(position).check_button = true;
                        holder.button.startAnimation(animAplha);
                    }
                });
                convertView.setTag(holder);
            }else{
                convertView = messageInflater.inflate(R.layout.task_button_mes, null);
                holder.button = convertView.findViewById(R.id.button2);
                holder.button.setTypeface(tf);
                holder.button.setText(message.getText());

                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        messages.get(position).check_button = true;
                        holder.button.startAnimation(animAplha);
                    }
                });
                convertView.setTag(holder);
            }


        }
        else if (message.getType().equals("recording")) {
            convertView = messageInflater.inflate(R.layout.task_their_think, null);

            if (messages.get(i).check_repeat  != 4) {
                final TextView textView = convertView.findViewById(R.id.repeat_list);
                final LoadingDots progressBar = convertView.findViewById(R.id.progressBar);

                textView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                convertView.setTag(holder);
            } else {
                final TextView textView = convertView.findViewById(R.id.repeat_list);
                final LoadingDots progressBar = convertView.findViewById(R.id.progressBar);

                textView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);

            }
        }
        else if (message.getType().equals("image")) {
            convertView = messageInflater.inflate(R.layout.task_image, null);
            ImageView imageView = convertView.findViewById(R.id.imageView);

            if (message.isGif()) {
                Glide.with(context)
                        .load(message.getUri())
                        .asGif()
                        .error(android.R.drawable.stat_notify_error)
                        .crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(imageView);
            }
            else
                Picasso.with(context).load(message.getUri()).into(imageView);
            message.check_button = true;
            convertView.setTag(holder);
        }
        else if (message.getType().equals("listen")) {
            convertView = messageInflater.inflate(R.layout.task_listen, null);

            int[] colors = {
                    ContextCompat.getColor(context, R.color.color_all),
                    ContextCompat.getColor(context, R.color.color_all),
                    ContextCompat.getColor(context, R.color.color_all),
                    ContextCompat.getColor(context, R.color.color_all),
                    ContextCompat.getColor(context, R.color.color_all),
            };

            int[] heights = { 22, 26, 20, 25, 18 };

            holder.recognitionProgressView = convertView.findViewById(R.id.button_rep);

            holder.recognitionProgressView.setSpeechRecognizer(message.getSpeechRecognizer());

            holder.recognitionProgressView.setColors(colors);
            holder.recognitionProgressView.setBarMaxHeightsInDp(heights);
            holder.recognitionProgressView.setCircleRadiusInDp(5);
            holder.recognitionProgressView.setSpacingInDp(5);
            holder.recognitionProgressView.setIdleStateAmplitudeInDp(5);
            holder.recognitionProgressView.setRotationRadiusInDp(15);
            holder.recognitionProgressView.play();

            holder.recognitionProgressView.setRecognitionListener(new RecognitionListenerAdapter() {
                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results
                            .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                    if (matches != null) {
                        say_recognizer = matches.get(0);
                        messages.get(position).check_repeat = 6;
                    }
                }

                @Override
                public void onError(int error) {
                    super.onError(error);
                    messages.get(position).check_repeat1 = 9;
                }
            });

            convertView.setTag(holder);
        }
        else if (message.getType().equals("listen_stop_button")) {
            convertView = messageInflater.inflate(R.layout.task_listen_button, null);

            return convertView;
        }
        else if (message.getType().equals("listen_stop")) {
            if (i >= max_i) {
                message.setParse_(say_recognizer);
                say_recognizer = "";

                convertView = messageInflater.inflate(R.layout.task_text_right, null);
                if (!message.getParse_().isEmpty()){
                    convertView = messageInflater.inflate(R.layout.task_text_right, null);
                    holder.message = convertView.findViewById(R.id.txtview);

                    holder.message.setText("");

                    holder.message.setTypeface(tf);

                    message.check_repeat = 2;

                    String say_said = message.getParse_();
                    String say_said_good =say_said.replace("i'm","i am");
                    String say_perfect =say_said_good.replace("'s"," is");

                    String[] say_string = say_perfect.split(" ");
                    String[] check_string = message.getText().split(" ");

                    int i1, i2, length = 0;

                    try {
                        for (i1 = 0, i2 = 0; i1 < say_string.length;) {
                            if (i1 > 0 || i2 > 0)
                                holder.message.append(" ");

                            if (similarity(say_string[i1],check_string[i2])>0.3) {
                                holder.message.append(getColoredString(say_string[i1], Color.BLACK));
                                length++;
                                i1++;
                                i2++;
                            } else {
                                boolean check_miss = false;
                                for (int j = i1; j < check_string.length; j++) {
                                    if (similarity(say_string[i1],check_string[j])>0.3) {
                                        for (int buf = i1; buf < j; buf++) {
                                            holder.message.append(getColoredString(check_string[buf], Color.YELLOW));
                                            message.check_repeat = 1;
                                            length++;
                                        }
                                        i2 = j;
                                        check_miss = true;
                                    } else if (check_miss)
                                        break;
                                }

                                if (!check_miss) {
                                    System.out.print(say_string[i1] + "(R) ");
                                    holder.message.append(getColoredString(say_string[i1], Color.RED));
                                    message.check_repeat = 1;
                                    length++;
                                    i1++;
                                    i2++;
                                }
                            }
                        }
                    } catch (Exception ignore) {}

                    if (length < check_string.length) {
                        holder.message.append(" ");
                        for (int j = length; j < check_string.length; j++) {
                            holder.message.append(getColoredString(check_string[j], Color.YELLOW));
                            message.check_repeat = 1;
                            if (j != check_string.length - 1)
                                holder.message.append(" ");
                        }
                    }

                    if (length < say_string.length) {
                        holder.message.append(" ");
                        for (int j = length; j < say_string.length; j++) {
                            holder.message.append(getColoredString(say_string[j], Color.RED));
                            message.check_repeat = 1;
                            if (j != check_string.length - 1)
                                holder.message.append(" ");

                        }
                    }
                    textViewList.add(new TextView2(holder.message, i));
                }
            }
            else {
                convertView = messageInflater.inflate(R.layout.task_text_right, null);
                holder.message = convertView.findViewById(R.id.txtview);

                for (int j = 0; j < textViewList.size(); j++)
                    if (textViewList.get(j).i == i) {
                        holder.message.setTypeface(tf);
                        holder.message.setText(textViewList.get(j).textView.getText());
                    }

            }
            convertView.setTag(holder);
        }
        else if (message.getType().equals("think")) {
            convertView = messageInflater.inflate(R.layout.task_think_mes, null);

            LoadingDots view = convertView.findViewById(R.id.thingdots);
            final ImageView imageView = convertView.findViewById(R.id.repeat_list);

            if (message.isGif()) {
                view.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);

            }
            else {
                view.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                Picasso.with(context).load(R.drawable.dots).into((ImageView)convertView.findViewById(R.id.repeat_list));
            }

            convertView.setTag(holder);
        } else if (message.getType().equals("end")) {
            convertView = messageInflater.inflate(R.layout.task_null, null);

            convertView.setTag(holder);
        }


        if (i > max_i)
            max_i = i;
        return convertView;
    }



    //        Функция для определения схожетси строчек
    private static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0; /* both strings are zero length */
        }

        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    private static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();


        s1 = s1.replace("?", "");
        s1 = s1.replace("!", "");
        s1 = s1.replace(".", "");

        s2 = s2.replace("?", "");
        s2 = s2.replace("!", "");
        s2 = s2.replace(".", "");

        s1 = s1.replace("I'm", "i am");

        s2 = s2.replace("I'm", "i am");

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];

    }

}


class MessageViewHolder {
    public TextView message;
    public RecognitionProgressView recognitionProgressView;
    public Button button;
}


