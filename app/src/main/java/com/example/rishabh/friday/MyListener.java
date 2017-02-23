package com.example.rishabh.friday;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by rishabh on 22/02/17.
 */
public class MyListener extends Activity implements Runnable{


    private AddNewLabel act;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String speech;



    public void printTh(){

        if(!act.equals(null))
        Log.w("myApp","FINE FINE FINE" );
        else
            Log.w("myApp", "NULL NULL NULL");
    }


    public void run(){

        promptSpeechInput();


    }
    public void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
        //       "Speak!!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(MyApp.getContext(),
                    "speech_not_supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> result;
        //speech = null;

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    float[] confidence = data
                            .getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
                    try {

                        //String toSpeak = result.get(0);
                        //t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                        //txtSpeechInput.setText(result.get(0));
                        speech = result.get(0);
                        //addDescriptionEditText.setText(speech);
                        Toast.makeText(MyApp.getContext(),speech,Toast.LENGTH_LONG).show();


                        //findCommand(result.get(0));
                    }
                    catch(NullPointerException ne) {
                        // Toast.makeText(getApplicationContext(),
                        //       "No detection !!!",
                        //     Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }




}
