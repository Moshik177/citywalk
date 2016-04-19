package com.ctwalkapp.ctwalk.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.ctwalkapp.ctwalk.Activities.MapActivity;
import com.ctwalkapp.ctwalk.Entities.BaseActivity;
import com.ctwalkapp.ctwalk.NavigationSlideTabs.SlidingTabLayout;
import com.ctwalkapp.ctwalk.R;
import com.ctwalkapp.ctwalk.Utils.MyApplication;
import com.ctwalkapp.ctwalk.Utils.RouteFragmentPagerAdapter;

/**
 * Created by david on 04/09/15.
 */
public class RouteSlidingFragment extends BaseActivity
{
    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_route_slide_view);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new RouteFragmentPagerAdapter(getSupportFragmentManager(),RouteSlidingFragment.this));

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.route_sliding_tabs);

        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mSharedPref = getApplicationContext().getSharedPreferences("FindMyFriendsPref", 0); // 0 - for private mode;
     Boolean isNewRoute = ((MyApplication)getApplication()).getIsItNewRoute();
        if(isNewRoute)
        {
            getMenuInflater().inflate(R.menu.menu_main_view_new, menu);
        }
        else
        {
            getMenuInflater().inflate(R.menu.menu_main_view, menu);
        }
        return super.onCreateOptionsMenu(menu);
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
        if (id == R.id.action_map)
        {
            startActivity(new Intent(getApplicationContext(), MapActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
