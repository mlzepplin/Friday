package com.example.rishabh.friday;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import java.util.Locale;

public class ToActivity extends AppCompatActivity {

    private TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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
}

