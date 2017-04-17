package com.example.rishabh.friday;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class AddNewLabel extends AppCompatActivity {

    EditText addLabelEditText;
    Button addNoteButton;
    EditText addDescriptionEditText;
    MyDBHandler myDB;
    private TextToSpeech t1,t2,t3;
    private static String flag;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private String newMeaning;
    private static final String TAG = "Http Connection";
    final String nullCheck = "";
    private String seema;
    private SpeechListener speech ;
    private String speak;
    private String from;
    private String edit;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_label);
        flag= "INACTIVE";

        addLabelEditText = (EditText)findViewById(R.id.addLabelEditText);
        addNoteButton = (Button)findViewById(R.id.addNoteButton);
        addDescriptionEditText = (EditText)findViewById(R.id.addDescriptionEditText);

        //making the textView scrollable
        addDescriptionEditText.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        edit = intent.getStringExtra("EDIT");

        myDB = new MyDBHandler(this);


        if(edit != null) {
            addLabelEditText.setText(edit);
            addDescriptionEditText.setText(myDB.getDescription(edit));
        }

        addDescriptionEditText.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View view) {

                        seema = "Description";
                        if(from.equals("new"))
                            promptSpeechInput("Description of the note?");
                        else if(from.equals("edit"))
                            promptSpeechInput("Speak the line to be appended..");
                    }
                }
        );

        addLabelEditText.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View view) {

                        seema = "Label";
                        if(from.equals("new"))
                            promptSpeechInput("A label to represent your note");
                        if(from.equals("edit"))
                            promptSpeechInput("New label name?");

                    }
                }
        );

        //EVENT HANDLING //question here
        addNoteButton.setOnClickListener(

                //Starting up an interface for an event
                new Button.OnClickListener() {

                    public void onClick(View view) {


                        final  String label = addLabelEditText.getText().toString();
                        String description = addDescriptionEditText.getText().toString();
                        System.out.println(description);
                        String type = "note";
                        final String toSpeak;
                        if(from.equals("edit")) {
                            toSpeak = "Your note with label "+label+" has been updated";
                            myDB.deleteLabel(label);
                            myDB.insertLabel(type, description, label);
                            System.out.println(myDB.getDescription(label));
                        }
                        else {
                            if (nullCheck.equals(label)) {
                                Toast.makeText(AddNewLabel.this, "must enter a label", Toast.LENGTH_LONG).show();
                                addLabelEditText.callOnClick();
                                toSpeak = "";
                            }

                            else {
                                myDB.insertLabel(type, description, label);
                                toSpeak = "Your note has been saved with label "+label;
                            }
                        }
                        if(toSpeak != "") {
                            t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        t1.setLanguage(Locale.US);

                                        t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                                    } //here ^^
                                }
                            });
                            int counter = getIntent().getIntExtra("counter", 0);
                            Intent launchLabelListIntent = new Intent(AddNewLabel.this, MainActivity.class);
                            launchLabelListIntent.putExtra("counter", counter);
                            startActivity(launchLabelListIntent);
                        }
                    }
                }
        );

        if(from.equals("new"))
            addLabelEditText.callOnClick();
        else if(from.equals("edit"))
            addDescriptionEditText.callOnClick();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    public void exitButton1(View view) {
        System.exit(0);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void beginInput() {

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
                        if(from.equals("new")) {
                            switch (seema) {
                                case "Label":
                                    if (myDB.searchLabel(speak)) {
                                        Toast.makeText(AddNewLabel.this, "Note with label "+speak+" already exists, use edit functionality or enter new Label", Toast.LENGTH_LONG).show();
                                        addLabelEditText.callOnClick();
                                    }
                                    else {
                                        addLabelEditText.setText(speak);
                                        addDescriptionEditText.callOnClick();
                                    }
                                    break;

                                case "Description":
                                    addDescriptionEditText.setText(speak);
                                    addNoteButton.callOnClick();
                                    break;

                            }
                        }
                        else if(from.equals("edit")) {
                            switch (seema) {
                                case "Description":
                                    String temp = addDescriptionEditText.getText().toString();
                                    temp = temp + "\n" + speak;
                                    addDescriptionEditText.setText(temp);
                                    addNoteButton.callOnClick();
                                    break;
                            }
                        }
                    }
                    catch(NullPointerException ne) {
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
