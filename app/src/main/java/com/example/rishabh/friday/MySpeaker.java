package com.example.rishabh.friday;

import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by rishabh on 22/02/17.
 */
public class MySpeaker extends Thread {

    android.content.Context context;
    private TextToSpeech t1;
    MyListener myListener;

    public MySpeaker(android.content.Context context){

        this.context = context;
        myListener = new MyListener();

    }

    public void run(){

        t1=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            public void onInit(int status) {

                Toast.makeText(context, "in init", Toast.LENGTH_SHORT).show();
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                    String toSpeak = "Please dictate the note";
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH,null);

                }
            }
        });

        if(!t1.isSpeaking()) {

            t1.shutdown();
            myListener.promptSpeechInput();
        }

    }



}
