package com.example.rishabh.friday;

/**
 * Created by rishabh on 13/04/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Created by anip on 08/04/17.
 */

public class MyPagerAdapter extends PagerAdapter{
    private Context context;
    private SharedPreferences sharedPreferance;
    private SharedPreferences.Editor editor;
    public MyPagerAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = null;
        switch (position) {
            case 0:
                layout = inflater.inflate(R.layout.intro_slide1, container, false);
                break;
            case 1:
                layout = inflater.inflate(R.layout.intro_slide2, container, false);
                break;
            case 2:
                layout = inflater.inflate(R.layout.intro_slide_3, container, false);
                break;
            case 3:
                layout = inflater.inflate(R.layout.intro_slide_4, container, false);
                break;
            case 4:
                layout = inflater.inflate(R.layout.intro_slide5, container, false);
                break;
            case 5:
                layout = inflater.inflate(R.layout.intro_slide6, container, false);
                Button button  = (Button) (View)layout.findViewById(R.id.btnIntroEnd);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ToActivity.class);
                        sharedPreferance = context.getSharedPreferences("user", Context.MODE_PRIVATE);
                        editor = sharedPreferance.edit();
                        editor.putBoolean("login", true);
                        editor.commit();
                        context.startActivity(intent);
                        ((Activity)context).finish();

                    }
                });
                break;
            default:
                layout = inflater.inflate(R.layout.intro_slide1, container, false);
        }
        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}