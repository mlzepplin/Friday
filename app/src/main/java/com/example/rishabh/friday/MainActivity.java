package com.example.rishabh.friday;


import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.Toast;
//mic changes begin
import android.widget.ImageButton;
import android.content.ActivityNotFoundException;
import android.widget.TextView;
//mic changes end
import java.util.ArrayList;
import java.util.Locale;

import static com.example.rishabh.friday.R.id.searchEditText;

public class MainActivity extends AppCompatActivity {


    //mic changes begin
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    //mic changes end

    MyDBHandler myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//mic changes begin
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                promptSpeechInput();
            }
        });
//mic changes end


        //THE SEARCH EDIT TEXT
        final String nullcheck = "";// A NULL STRING
        final EditText searchEditText = (EditText)findViewById(R.id.searchEditText);



        //SEARCH BUTTON
        Button searchButton = (Button)findViewById(R.id.searchButton);
        //EVENT HANDLING
        searchButton.setOnClickListener(

                //Starting up an interface for an event
                new Button.OnClickListener() {

                    public void onClick(View v) {

                        // WILL IMPLEMENT LATER
                        if (nullcheck.equals(searchEditText.getText().toString())) {

                            // TOAST
                            Toast.makeText(MainActivity.this, "enter a label to search for", Toast.LENGTH_LONG).show();

                        }
                        else{

                            boolean exists = myDB.searchLabel(searchEditText.getText().toString());

                            if (exists) {

                                // TOAST
                                Toast.makeText(MainActivity.this, "Label already exists" , Toast.LENGTH_LONG).show();
                                Intent launchLabelDisplayIntent = new Intent(MainActivity.this, LabelDisplay.class);
                                launchLabelDisplayIntent.putExtra("label", searchEditText.getText().toString());
                                startActivity(launchLabelDisplayIntent);


                            }
                            else {
                                //new label ACTIVITY LAUNCHED
                                Intent launchAddNewLabelIntent = new Intent(MainActivity.this, AddNewLabel.class);
                                //launchMeaningIntent.putExtra("search", searchEditText.getText().toString());
                                startActivity(launchAddNewLabelIntent);
                            }
                        }

                    }

                }
        );

        //String[] labels = {"positive words","negative words","harsh words","physical description"};

        myDB = new MyDBHandler(this);

        //FUNCTIONALITY FOR LIST ITEM CLICK

        ArrayList<String> labelList = myDB.getAllLabels();

        ArrayAdapter<String> labelsAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,labelList);


        ListView labelsListView = (ListView)findViewById(R.id.labelsListView);
        labelsListView.setAdapter(labelsAdapter);



        //LONG CLICK LISTENER
        labelsListView.setOnItemLongClickListener(

                new AdapterView.OnItemLongClickListener() {

                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        String label = String.valueOf(parent.getItemAtPosition(position));
                        Intent launchWordListIntent = new Intent(MainActivity.this, LabelDisplay.class);
                        launchWordListIntent.putExtra("label", label);
                        startActivity(launchWordListIntent);

                        return true;
                    }

                }

        );

        //ON CLICK LISTENER
        labelsListView.setOnItemClickListener(

                new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String label = String.valueOf(parent.getItemAtPosition(position));
                        Intent launchWordListIntent = new Intent(MainActivity.this, LabelDisplay.class);
                        launchWordListIntent.putExtra("label", label);
                        startActivity(launchWordListIntent);

                    }

                }

        );


        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(

                //Starting up an interface for an event
                new FloatingActionButton.OnClickListener() {

                    public void onClick(View v) {

                        //ADD NEW LABEL ACTIVITY LAUNCHED
                        Intent launchAddNewLabelIntent = new Intent(MainActivity.this, AddNewLabel.class);
                        //launchAddNewLabelIntent.putExtra("search", searchEditText.getText().toString());
                        startActivity(launchAddNewLabelIntent);
                    }

                }


        );







    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_home_screen, menu);
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

    //mic changes begin
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak!!");
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

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    float[] confidence = data
                            .getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
                    try {
                        //txtSpeechInput.setText(Float.toString(confidence[0]));
                        txtSpeechInput.setText(result.get(0));
                    }
                    catch(NullPointerException ne) {
                        Toast.makeText(getApplicationContext(),
                                "No detection !!!",
                                Toast.LENGTH_SHORT).show();
                        /*Toast.makeText(getApplicationContext(),
                                Locale.getDefault().toString(),
                                Toast.LENGTH_SHORT).show();*/
                    }
                }
                break;
            }
        }
    }
    // mic changes end
}

