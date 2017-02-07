package com.example.rishabh.friday;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    MyDBHandler myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
