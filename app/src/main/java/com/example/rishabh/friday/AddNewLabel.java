package com.example.rishabh.friday;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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


    private final int REQ_CODE_SPEECH_INPUT = 100;

    private String newMeaning;
    private static final String TAG = "Http Connection";
    final String nullCheck = "";

    private String speechInput ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_label);



        addLabelEditText = (EditText)findViewById(R.id.addLabelEditText);
        addNoteButton = (Button)findViewById(R.id.addNoteButton);
        addDescriptionEditText = (EditText)findViewById(R.id.addDescriptionEditText);

        //making the textView scrollable
        addDescriptionEditText.setMovementMethod(new ScrollingMovementMethod());

        myDB = new MyDBHandler(this);
        // db = openOrCreateDatabase("imeanDB", Context.MODE_PRIVATE,null);
        //db.execSQL("DROP TABLE IF EXISTS words");
        //db.execSQL("CREATE TABLE IF NOT EXISTS words(_word VARCHAR, _meaning VARCHAR, _label VARCHAR);");

        //meaningTextView.setText(newMeaning);




        //EVENT HANDLING
        addNoteButton.setOnClickListener(

                //Starting up an interface for an event
                new Button.OnClickListener() {

                    public void onClick(View view) {

                        // TOAST
                        //Toast.makeText(MeaningDisplay.this, "button clicked", Toast.LENGTH_SHORT).show();
                        String label = addLabelEditText.getText().toString();
                        String description  = addDescriptionEditText.getText().toString();
                        String type = "note";
                        if(nullCheck.equals(label)){

                            Toast.makeText(AddNewLabel.this, "must enter a label", Toast.LENGTH_LONG).show();
                        }
                        else {
                            myDB.insertLabel(type, description, label);
                            //db.execSQL("INSERT INTO words(_word, _meaning, _label) VALUES('"+search+"','"+newLabel+"','"+newMeaning+"');");
                            Intent launchLabelListIntent = new Intent(AddNewLabel.this, MainActivity.class);
                            startActivity(launchLabelListIntent);
                        }
                        //Database();
                        // Toast.makeText(MeaningDisplay.this, "word added", Toast.LENGTH_SHORT).show();


                    }


                }
        );
        //mic changes begin
        t1=new TextToSpeech(this.getApplicationContext(), new TextToSpeech.OnInitListener() {
            public void onInit(int status) {

                //Toast.makeText(AddNewLabel.this, "in init", Toast.LENGTH_SHORT).show();



                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                    String toSpeak = "Please dictate the note";
                   t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH,null);

                }
            }
        });

       // MySpeaker mySpeaker = new MySpeaker(this.getApplicationContext());
        //mySpeaker.run();



        //MyListener myListener = new MyListener(AddNewLabel.this);


        //myListener.printTh();
        //myListener.run();

        AsyncTaskRunner runner = new AsyncTaskRunner();
        //String sleepTime = time.getText().toString();
        runner.execute();


        //promptSpeechInput();

        /*
        System.out.println("aa rha hai yahan? " + speech);

        if(speech != null) {
            addDescriptionEditText.setText(speech);
            //promptSpeechInput();
            t2=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        t2.setLanguage(Locale.US);
                        String toSpeak = "Please state the label name";
                        t2.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });
            //t1 = null;
            speech = null;
            promptSpeechInput();
            addLabelEditText.setText(speech);
            t3=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        t3.setLanguage(Locale.US);
                        String toSpeak = "Do you want to save, YES or NO?";
                        t3.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });
            speech = null;
            promptSpeechInput();
            if(speech.equalsIgnoreCase("yes"))
                addNoteButton.callOnClick();

        }
        */

        //mic changes end



    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        /*
        getMenuInflater().inflate(R.menu.menu_meaning_display, menu);
        */
        return true;

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
            Toast.makeText(getApplicationContext(),
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
                        speechInput = result.get(0);
                        addDescriptionEditText.setText(speechInput);


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

    //Assync Task

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {

            //publishProgress("Sleeping...");
            // Calls onProgressUpdate()

            try {

                t1=new TextToSpeech(MyApp.getContext(), new TextToSpeech.OnInitListener() {
                    public void onInit(int status) {

                        Toast.makeText(AddNewLabel.this, "in init", Toast.LENGTH_SHORT).show();
                        if(status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.US);
                            String toSpeak = "Please dictate the note";
                            t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH,null);

                        }
                    }
                });

                //t1.stop();

                    } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {

            // execution of result of Long time consuming operation
            while(t1.isSpeaking()){

                if(!t1.isSpeaking()){

                    t1.shutdown();

                    promptSpeechInput();
                    break;

                }

            }


        }


        @Override
        protected void onPreExecute() {

        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }









}
