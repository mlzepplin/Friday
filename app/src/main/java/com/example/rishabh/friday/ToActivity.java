package com.example.rishabh.friday;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class ToActivity extends AppCompatActivity {

    private TextToSpeech t1;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String flag= "INACTIVE";
    private View view;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //speech
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                view = v;
                promptSpeechInput();
            }
        });
        //here

        int counter = 0;
        Intent intent = getIntent();
//mic changes end
        counter = intent.getIntExtra("counter",0);
        System.out.println(counter);
        if(counter<1) {

            t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.US);

                        t1.speak("Welcome, I am FRIDAY, your personal Task Manager.", TextToSpeech.QUEUE_FLUSH, null);

                    } //here ^^
                }
            });
        }
        counter++;

    }

    public void NoteRedirect(View view) {
        Intent intent = new Intent(ToActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void ReminderRedirect(View view) {
        Intent intent = new Intent(ToActivity.this, ReminderActivity.class);
        startActivity(intent);
    }

    public void ChecklistRedirect(View view) {
        Intent intent = new Intent(ToActivity.this, CheckList.class);
        startActivity(intent);
    }

    public void Exit(View view) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();

    }

    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak!!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            intent.putExtra("counter", counter);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "speech_not_supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("111111");
        if(null != data) {
            switch (requestCode) {
                case REQ_CODE_SPEECH_INPUT: {
                    System.out.println("222222 "+resultCode+" "+data.toString());
                    if (resultCode == RESULT_OK && null != data) {
                        System.out.println("222222 "+resultCode+" "+data.toString());
                        ArrayList<String> result = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        float[] confidence = data
                                .getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
                        try {

                            Toast.makeText(getApplicationContext(),
                                    result.get(0),
                                    Toast.LENGTH_SHORT).show();
                            findCommand(result.get(0));
                        }
                        catch(NullPointerException ne) {
                            Toast.makeText(getApplicationContext(),
                                    "No detection !!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    flag = "INACTIVE";
                    break;
                }
            }
        }
        System.out.println("333333");
    }

    public void findCommand(String com) {

        if(com.matches("(.*)note(.*)") || com.matches("(.*)Note(.*)") || com.matches("(.*)NOTE(.*)") || com.matches("(.*)notes(.*)") || com.matches("(.*)Notes(.*)") || com.matches("(.*)NOTES(.*)")) {
            NoteRedirect(view);
        }
        else if(com.matches("(.*)reminder(.*)") || com.matches("(.*)Reminder(.*)") || com.matches("(.*)REMINDER(.*)") || com.matches("(.*)reminders(.*)") || com.matches("(.*)Reminders(.*)") || com.matches("(.*)REMINDERS(.*)")) {
            ReminderRedirect(view);
        }
        else if(com.matches("(.*)checklist(.*)") || com.matches("(.*)Checklist(.*)") || com.matches("(.*)CHECKLIST(.*)") || com.matches("(.*)checklists(.*)") || com.matches("(.*)Checklists(.*)") || com.matches("(.*)CHECKLISTS(.*)")) {
            ChecklistRedirect(view);
        }
        else if(com.matches("(.*)exit(.*)") || com.matches("(.*)Exit(.*)") || com.matches("(.*)EXIT(.*)")) {
            Exit(view);
        }
        else  {
            final String toSpeak;
            toSpeak = "Command not recognized!";
            //here
            t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.US);

                        t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                    }


                }
            }
            );
        }

    }
}

