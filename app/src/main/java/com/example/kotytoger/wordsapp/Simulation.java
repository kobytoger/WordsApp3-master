package com.example.kotytoger.wordsapp;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Simulation extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = 300000;
    private TextView timer_view;
    private CountDownTimer countDownTimer;
    private boolean isTimeRunning;
    private long timeLeftInMillis = START_TIME_IN_MILLIS;

    DatabaseHelper myDB;

    private ColorStateList defaultColor;

    private int counter;
    private TextView wordTextView;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private Button buttonConfirmNext;
    private boolean answered;
    private String correct;
    HashMap<String, String> hashMap;
    List<String> keys;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);

        myDB = new DatabaseHelper(this);
        hashMap = new HashMap<>();

        timer_view = findViewById(R.id.id_time);
        startTimer();
        updateCountDownText();

        counter = 0;
        wordTextView = findViewById(R.id.id_word);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        rb4 = findViewById(R.id.radio_button4);
        buttonConfirmNext = findViewById(R.id.id_confirm);

        defaultColor = rb1.getTextColors();

        Cursor data = myDB.getListContents();
        if (data.getCount() == 0) {
            Toast.makeText(this, "There are no contents in this list!", Toast.LENGTH_LONG).show();
        } else {
            while (data.moveToNext()) {
                hashMap.put(data.getString(1), data.getString(2));
            }
        }

        keys = new ArrayList<>(hashMap.keySet());

        if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
            showNextWord();
        }
        showNextWord();

        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!answered) {
                    if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
                        checkAnswer();
                    } else {
                        Toast.makeText(Simulation.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showNextWord();
                }
            }
        });

    }

    private void showNextWord(){

        buttonConfirmNext.setText("Confirm");
        rb1.setTextColor(defaultColor);
        rb2.setTextColor(defaultColor);
        rb3.setTextColor(defaultColor);
        rb4.setTextColor(defaultColor);
        rbGroup.clearCheck();

        List<String> tempKeys = new ArrayList<>(hashMap.keySet());
        Random rand = new Random();

        if(!keys.isEmpty()) {

            key = keys.get(rand.nextInt(keys.size()));

            String tempKey;

            List<String> options = new ArrayList<>();

            //correct
            correct = hashMap.get(key);
            options.add(correct);
            tempKeys.remove(key);
            //distracors
            for (int i = 0; i < 3; i++) {
                do {
                    tempKey = tempKeys.get(rand.nextInt(tempKeys.size()));
                } while (tempKey.equals(key));

                options.add(hashMap.get(tempKey));
                tempKeys.remove(tempKey);
            }

            Collections.shuffle(options);

            //anim
            AlphaAnimation fadeOut = new AlphaAnimation(0.0f , 1.0f ) ;
            AlphaAnimation fadeIn = new AlphaAnimation( 1.0f , 0.0f ) ;
            wordTextView.startAnimation(fadeIn);
            wordTextView.startAnimation(fadeOut);
            fadeIn.setDuration(200);
            fadeIn.setFillAfter(true);
            fadeOut.setDuration(200);
            fadeOut.setFillAfter(true);
            fadeOut.setStartOffset(400+fadeIn.getStartOffset());

            wordTextView.setText(key);

            rb1.setText(options.get(0));
            rb2.setText(options.get(1));
            rb3.setText(options.get(2));
            rb4.setText(options.get(3));
            keys.remove(key);
            answered = false;

        }
        else{
            buttonConfirmNext.setText("Finish");
            String grade = Float.toString((float)counter/(float)hashMap.size()*100);
            wordTextView.setText("Grade: " + grade);
            rbGroup.setVisibility(View.GONE);
            timer_view.setVisibility(View.GONE);
            buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    private void checkAnswer() {
        answered = true;

        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        String answer = (String) rbSelected.getText();
        if(answer == correct) {
            //Toast.makeText(this, answer, Toast.LENGTH_LONG).show();
            rb1.setTextColor(Color.RED);
            rb2.setTextColor(Color.RED);
            rb3.setTextColor(Color.RED);
            rb4.setTextColor(Color.RED);
            rbSelected.setTextColor(Color.GREEN);
            buttonConfirmNext.setText("Next");
            counter++;

        }
        else{
            rbSelected.setTextColor(Color.RED);
            Toast.makeText(this, "Wrong Answer", Toast.LENGTH_LONG).show();
            buttonConfirmNext.setText("Next");
            counter--;
            keys.add(key);

        }
    }


    protected void startTimer()
    {
        countDownTimer = new CountDownTimer(timeLeftInMillis,1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isTimeRunning = false;
                Toast.makeText(Simulation.this,"TIME'S UP! START AGAIN!",Toast.LENGTH_LONG).show();
                finish();

            }
        }.start();

        isTimeRunning = true;
    }

    protected void updateCountDownText()
    {
        int minutes = (int)(timeLeftInMillis/1000) / 60;
        int seconds = (int)(timeLeftInMillis/1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes,seconds);

        timer_view.setText(timeLeftFormatted);

    }

}
