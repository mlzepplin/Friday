package com.example.rishabh.friday;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewItem extends AppCompatActivity {


    Button addItemButton;
    EditText addItemEditText;
    MyDBHandler myDB;
    private static String flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        addItemButton = (Button)findViewById(R.id.addItemButton);
        addItemEditText = (EditText)findViewById(R.id.addItemEditText);


        myDB = new MyDBHandler(this);



        addItemEditText.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View view) {



                    }
                }
        );

        //EVENT HANDLING //question here
        addItemButton.setOnClickListener(

                //Starting up an interface for an event
                new Button.OnClickListener() {

                    public void onClick(View view) {


                        final  String item = addItemEditText.getText().toString();


                            if (item.equals(null)) {
                                Toast.makeText(AddNewItem.this, "must enter an item", Toast.LENGTH_LONG).show();

                            }
                            else {
                                myDB.insertCheckListItem(item);
                                Intent launchItemListIntent = new Intent(AddNewItem.this, CheckList.class);
                                startActivity(launchItemListIntent);

                            }



                    }
                }
        );



    }



}


