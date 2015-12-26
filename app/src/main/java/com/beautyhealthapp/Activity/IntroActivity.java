package com.beautyhealthapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beautyhealthapp.R;

import java.util.ArrayList;
import java.util.List;


public class IntroActivity extends Activity {

    private final int[] imageResIds = {
    	R.mipmap.into1,
        R.mipmap.into2,
        R.mipmap.into3
    };

    /**
     * onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        initViewPager();
    }

    /**
     * ViewPager
     */
    @SuppressWarnings("deprecation")
	private void initViewPager() {
        ViewPager vp = (ViewPager) findViewById(R.id.intro_vPager);
        vp.setAdapter(new ViewPagerAdapter(this));
        vp.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == imageResIds.length) {
                    gotoActivity();
                }
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        vp.setCurrentItem(0);
    }

    /**
     * ViewPager
     */
    private class ViewPagerAdapter extends PagerAdapter {

        private List<View> views;

        public ViewPagerAdapter(Context context) {
            views = new ArrayList<View>();
            for (int i = 0; i < imageResIds.length; i++) {
                ImageView iv = new ImageView(context);
                iv.setBackgroundResource(imageResIds[i]);
                //iv.setImageResource(imageResIds[i]);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                iv.setLayoutParams(params);
                views.add(iv);
            }
            views.add(new ImageView(IntroActivity.this));
            views.get(views.size()-2).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoActivity();
                }
            });
        }

        @Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView(views.get(position));
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object instantiateItem(View collection, int position) {        
            ((ViewPager) collection).addView(views.get(position), 0);
            return views.get(position);
        }

        @Override
        public boolean isViewFromObject(View v, Object o) {
            return v == o;
        }

    }

    
    private void gotoActivity() {
        Class<?> gotoClz = null;
        try {
            gotoClz = Class.forName(getIntent().getStringExtra("goto"));
        } catch (Exception e) {
        }
        if (gotoClz != null) {
        	Intent intent = new Intent();
        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(IntroActivity.this, gotoClz);
			intent.putExtra("goto", MainActivity.class.getName());
            startActivity(intent);
        }
        finish();
    }
    
    @Override
    public void onBackPressed() {
        gotoActivity();
    }

}
