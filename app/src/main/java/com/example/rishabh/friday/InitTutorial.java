package com.example.rishabh.friday;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class InitTutorial extends AppCompatActivity {

    private ViewPager viewPager;
    private MyPagerAdapter adapter;
    private Button endIntroButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSharedPreferences("user", Context.MODE_PRIVATE).getBoolean("login",false)){
            Intent intent= new Intent(InitTutorial.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_init_tutorial);

        adapter = new MyPagerAdapter(this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);


//
    }
}
