package com.ctwalkapp.ctwalk.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.Wsdl2Code.WebServices.CTwalkService.CTwalkService;
import com.Wsdl2Code.WebServices.CTwalkService.Complex_Route;
import com.Wsdl2Code.WebServices.CTwalkService.Output;
import com.ctwalkapp.ctwalk.Fragments.RoutesFragment;
import com.ctwalkapp.ctwalk.R;
import com.ctwalkapp.ctwalk.Utils.MyApplication;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity {

    private View view;
    private static CTwalkService service = new CTwalkService();
    private ImageView imageView;
    private List<Complex_Route> listRoutes= new ArrayList<>();
    private final int NUMBEROFTRYS = 5;
    private SharedPreferences mSharedPref;
    private final String kUSERID = "CTwalk_login_userid";
    private final String kUSERNAME = "CTWalk_login_username";
    private final String kRoutes = "CTwalk_Routes";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** set time to splash out */
        final int welcomeScreenDisplay = 3500;
        /** create a thread to show splash up to splash time */
        Thread welcomeThread = new Thread() {
        int wait = 0;

            @Override
            public void run() {
                try {
                    super.run();
                    /**
                     * use while to get the splash time. Use sleep() to increase
                     * the wait variable for every 100L.
                     */
                    while (wait < welcomeScreenDisplay) {
                        sleep(100);
                        wait += 100;
                    }
                } catch (Exception e) {
                    System.out.println("Exception thrown:" + e);
                } finally {
                    /**
                     * Called after splash times up. Do some action after splash
                     * times up. Here we moved to another main activity class
                     */
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        };
        welcomeThread.start();

        imageView = (ImageView) findViewById(R.id.img_splash);
        final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        animationFadeIn.setStartOffset(800);
        imageView.startAnimation(animationFadeIn);
        new Task().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        return super.onOptionsItemSelected(item);
    }

    public class Task extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... params)
        {
            return getAllCitiesFromTheServer();

        }

        @Override
        protected void onPostExecute(String result)
        { }
    }

    private String getAllCitiesFromTheServer()
    {
        boolean isWeGetAnAnswer = false;
        int numberOfTrys = 0;
        String Routes = null;
        service.setUrl("http://ctwalkapp.com/CTwalkService/Service.svc?wsdl");
        service.setTimeOut(14);
        mSharedPref = getApplicationContext().getSharedPreferences("CTwalkPref", 0); // 0 - for private mode;
        int UserId = mSharedPref.getInt(kUSERID, 0);

        if (UserId != 0 ) {
            ((MyApplication) getApplication()).setUserId(mSharedPref.getInt(kUSERID, 0));
            ((MyApplication) getApplication()).setUserName(mSharedPref.getString(kUSERNAME, ""));
                 Routes = mSharedPref.getString(kRoutes, "");
                //output = service.GetAllComplexRoutesByUserId(UserId, true);
        }
        else
        {
            ((MyApplication) getApplication()).setUserId(2);
            ((MyApplication) getApplication()).setUserName("Walker");
            ((MyApplication) getApplication()).setFirstName("Walker");
        }
        while(!isWeGetAnAnswer && numberOfTrys < NUMBEROFTRYS)
        {
            try
            {
                if (Routes.isEmpty())
                {
                    JSONArray routesJson = new JSONArray(Routes);
                    isWeGetAnAnswer = true;
                    for (int i = 0; i < routesJson.length(); i++)
                    {
                        Gson gson = new Gson();
                        String jsonObject = routesJson.getJSONObject(i).toString();
                        Complex_Route Complex_Routes = gson.fromJson(jsonObject,Complex_Route.class);
                        listRoutes.add(Complex_Routes);
                    }
                    ((MyApplication) getApplication()).setUserListRoutes(listRoutes);

                }
                else
                {
                    numberOfTrys = 5;
                }

            }
            catch (Exception e)
            {
                numberOfTrys++;
            }
        }
        if(numberOfTrys == NUMBEROFTRYS )
        {
            return null;
        }
        else return Routes;
    }

}
