package com.github.pashmentov96.reader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public class ScreenSlidePagerActivity extends FragmentActivity {
    private ViewPager viewPager;

    private PagerAdapter pagerAdapter;

    String textOfBook;
    static final String ARGUMENT_TEXT = "argument_text";

    int numPages;

    public static Intent getIntent(Context context, String text) {
        Intent intent = new Intent(context, ScreenSlidePagerActivity.class);
        intent.putExtra(ARGUMENT_TEXT, text);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        textOfBook = getIntent().getStringExtra(ARGUMENT_TEXT);
        numPages = (textOfBook.length() + 199) / 200;

        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            int beginIndex = 200 * position;
            return ScreenSlidePageFragment.newInstance(position, textOfBook.substring(beginIndex, beginIndex + 200));
        }

        @Override
        public int getCount() {
            return numPages;
        }
    }

}
