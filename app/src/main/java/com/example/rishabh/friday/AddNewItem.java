package com.example.rishabh.friday;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class AddNewItem extends AppCompatActivity {


    Button addItemButton;
    EditText addItemEditText;
    MyDBHandler myDB;
    private static String flag;

    //Speech stuff
    private TextToSpeech t1;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private static final String TAG = "Http Connection";
    final String nullCheck = "";
    //private String seema;
    private String speak;
    private String from;
    private String edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        addItemButton = (Button) findViewById(R.id.addItemButton);
        addItemEditText = (EditText) findViewById(R.id.addItemEditText);

        //speech
        Intent intent=getIntent();
        from=intent.getStringExtra("from");


        myDB = new MyDBHandler(this);


        addItemEditText.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View view) {

                        if (from.equals("speech"))
                            promptSpeechInput("Description of the checklist item?");

                    }
                }
        );

        //EVENT HANDLING //question here
        addItemButton.setOnClickListener(

                //Starting up an interface for an event
                new Button.OnClickListener() {

                    public void onClick(View view) {



                        final String item = addItemEditText.getText().toString();

                        final String toSpeak;
                        //speech stuff

                        if (nullCheck.equals(item)) {
                            Toast.makeText(AddNewItem.this, "Must enter an item", Toast.LENGTH_LONG).show();

                        } else {
                            if (myDB.searchCheckListLabel(item)) {
                                toSpeak = "An item with label" + item + "already exists";
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
                            else {
                                myDB.insertCheckListItem(new CheckListItem(0, item));
                                //speech stuff
                                toSpeak = "Your item with label" + item + "has been saved!";
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
                            //here
                            Intent launchItemListIntent = new Intent(AddNewItem.this, CheckList.class);
                            startActivity(launchItemListIntent);

                        }

                    }
                }
        );

        //speech
        if (from.equals("speech"))
            addItemEditText.callOnClick();
    }

    private void promptSpeechInput(String dialog) {
        flag = "ACTIVE";
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                dialog);
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "speech_not_supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void exitButton2(View view) {
        Toast.makeText(this, "Note not saved!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> result;
        System.out.println("Speech Result returned");
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    float[] confidence = data
                            .getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
                    try {

                        speak = result.get(0);

                        addItemEditText.setText(speak);
                        addItemButton.callOnClick();


                    } catch (NullPointerException ne) {
                        Toast.makeText(getApplicationContext(),
                                "No detection !!!",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            }
        }


    }
}


