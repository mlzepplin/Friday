package com.example.rishabh.friday;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class LabelDisplay extends AppCompatActivity {


    MyDBHandler myDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_display);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent incomingIntent = getIntent();
        final String label = incomingIntent.getStringExtra("label");
        final int counter = incomingIntent.getIntExtra("counter",0);

        myDB = new MyDBHandler(this);

        final String description = myDB.getDescription(label);

        TextView descriptionTextView = (TextView)findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(label+"\n\n ------------------- \n"+ description);

        //making text view scrollable
        descriptionTextView.setMovementMethod(new ScrollingMovementMethod());
        ImageButton edit = (ImageButton)findViewById(R.id.edit);
        edit.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View c) {
                        Intent launchAddNewLabelIntent = new Intent(LabelDisplay.this, AddNewLabel.class);
                        launchAddNewLabelIntent.putExtra("EDIT", label);
                        launchAddNewLabelIntent.putExtra("from","edit");
                        launchAddNewLabelIntent.putExtra("counter",counter);
                        startActivity(launchAddNewLabelIntent);
                        finish();
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_one_word_meaning, menu);
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


        onBackPressed();
        return true;

        }

}

