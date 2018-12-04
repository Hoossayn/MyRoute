package com.example.android.myroute.Activities;
//*****************************************************************************************************
//*****************************************************************************************************

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.myroute.R;
import com.example.android.myroute.adapter.SliderAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

//*****************************************************************************************************
//*****************************************************************************************************
public class SlideActivity extends AppCompatActivity {
    public static final String TEXT = "text";
    private static final String SHARED_PREFS = "sharedPrefs";
    TextView helloworld;
    String compare = "text";
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private SliderAdapter sliderAdapter;
    private Button mNextbtn;
    private Button mBackbtn;
    private Button skipbtn;
    private Button dontshow;
    private int mCurrentPage;
    private TextView mDots[];
    //*****************************************************************************************************
    //*****************************************************************************************************
    ViewPager.OnPageChangeListener viewlistener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        //*****************************************************************************************************
        //*****************************************************************************************************
        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            mCurrentPage = i;
            if (i == 0) {
                mNextbtn.setEnabled(true);
                mBackbtn.setEnabled(false);
                mBackbtn.setVisibility(View.INVISIBLE);
            } else if (i == mDots.length - 1) {
                mNextbtn.setEnabled(true);
                mBackbtn.setEnabled(true);
                mBackbtn.setVisibility(View.VISIBLE);
                //   skipbtn.setVisibility(View.VISIBLE);
                mNextbtn.setVisibility(View.INVISIBLE);
            } else {
                mNextbtn.setEnabled(true);
                mBackbtn.setEnabled(true);
                mBackbtn.setVisibility(View.VISIBLE);
                mNextbtn.setVisibility(View.VISIBLE);
            }
        }

        //*****************************************************************************************************
        //*****************************************************************************************************
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dontshow = (Button) findViewById(R.id.stopshowing);
        helloworld = (TextView) findViewById(R.id.helloworld);
        dontshow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                helloworld.setText("123".toString());
                editor.putString(TEXT, helloworld.getText().toString());
                editor.apply();
                Intent intent = new Intent(SlideActivity.this, RouteList.class);
                startActivity(intent);
                finish();
            }
        });
        //*****************************************************************************************************
        //*****************************************************************************************************
        //Shared preference code to hold the shared preference value so as to know if it should keep showing
        //the slide screen or not to
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        compare = sharedPreferences.getString(TEXT, "text");
        helloworld.setText(compare);
        if (helloworld.getText().toString() != "text") {
            Intent intent = new Intent(SlideActivity.this, RouteList.class);
            startActivity(intent);
            finish();
        }
        mSlideViewPager = (ViewPager) findViewById(R.id.slideviewpager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotslayout);
        mNextbtn = (Button) findViewById(R.id.nextbtn);
        mBackbtn = (Button) findViewById(R.id.prevbtn);
        skipbtn = (Button) findViewById(R.id.skipbtn);
        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewlistener);
        //*****************************************************************************************************
        //*****************************************************************************************************
        mNextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage + 1);
            }
        });
        //*****************************************************************************************************
        //*****************************************************************************************************
        mBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage - 1);
            }
        });
        //*****************************************************************************************************
        //*****************************************************************************************************
        skipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SlideActivity.this, RouteList.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //*****************************************************************************************************
    //*****************************************************************************************************
    public void addDotsIndicator(int position) {
        mDots = new TextView[3];
        mDotLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(Color.parseColor("#E0F2F1"));
            mDotLayout.addView(mDots[i]);
        }
        if (mDots.length > 0) {
            mDots[position].setTextColor(Color.parseColor("#FF9800"));
        }
    }
}
