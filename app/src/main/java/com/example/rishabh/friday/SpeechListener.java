package com.example.rishabh.friday;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

import static android.R.attr.data;

/**
 * Created by Powerhouse on 2/22/2017.
 */

public class SpeechListener implements RecognitionListener {
    String speech;
    SpeechRecognizer sr;
    Intent intent;
    int done = 0;

    public void intialize(Context context) {
        sr = SpeechRecognizer.createSpeechRecognizer(context);
        sr.setRecognitionListener(this);
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        sr.startListening(intent);
    }

    public int getDone() {
        return done;
    }

    public String getSpeech() {
        return speech;
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onResults(Bundle bundle) {
        done = 1;
        ArrayList<String> result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        speech = result.get(0);
    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onError(int i) {

    }

}
