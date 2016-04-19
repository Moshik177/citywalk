package com.ctwalkapp.ctwalk.Entities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ctwalkapp.ctwalk.Activities.LoginActivity;
import com.ctwalkapp.ctwalk.Fragments.RoutesFragment;
import com.ctwalkapp.ctwalk.R;
import com.ctwalkapp.ctwalk.Utils.MyApplication;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;



public class BaseActivity extends ActionBarActivity
{

    private final String kUSERID = "CTwalk_login_userid";
    private final String kUSERNAME = "CTWalk_login_username";

    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ActionBar actionBar =  getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        View mActionBarView = getLayoutInflater().inflate(R.layout.my_action_bar, null);
        actionBar.setCustomView(mActionBarView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mSharedPref = getApplicationContext().getSharedPreferences("FindMyFriendsPref", 0); // 0 - for private mode;
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
		 if (id == R.id.action_MenuBar)
        {
            startActivity(new Intent(getApplicationContext(), RoutesFragment.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateToLoginScreen() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void logout() {
        // Stops gps update service
       // Intent gpsUpdatesIntent = new Intent(getApplicationContext(), LocationServiceManager.class);
       // getApplicationContext().stopService(gpsUpdatesIntent);

        // This cleans the logged in user details aka logout
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(kUSERID);
        editor.remove(kUSERNAME);
        editor.commit();

        // logging out from facebook as well
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
    }
}