package com.ctwalkapp.ctwalk.Utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ctwalkapp.ctwalk.Fragments.NavRouteDirecTab;
import com.ctwalkapp.ctwalk.Fragments.NavRouteInfoTab;
import com.ctwalkapp.ctwalk.Fragments.NavRouteMapTab;

/**
 * Created by david on 04/09/15.
 */
public class RouteFragmentPagerAdapter extends FragmentPagerAdapter
{
    //private String tabTitles[] = new String[]{"Info", "Directions", "Map"};
    private String tabTitles[] = new String[]{"Info", "Directions"};
    private Context context;

    public RouteFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        this.context = context;
    }

    @Override
    public Fragment getItem(int position)
    {
        if(position==0)
        {
            return new NavRouteInfoTab();
        }
        else if (position==1)
        {
            return new NavRouteDirecTab();
        }
        //else if (position==2)
        //{
        //    return new NavRouteMapTab();
        //}
        return null;
    }

    @Override
    public int getCount()
    {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
