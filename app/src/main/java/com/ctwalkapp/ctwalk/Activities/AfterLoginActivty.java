package com.ctwalkapp.ctwalk.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.Wsdl2Code.WebServices.CTwalkService.CTwalkService;
import com.Wsdl2Code.WebServices.CTwalkService.Output;
import com.Wsdl2Code.WebServices.CTwalkService.User;
import com.ctwalkapp.ctwalk.Entities.BaseActivity;
import com.ctwalkapp.ctwalk.Fragments.RoutesFragment;
import com.ctwalkapp.ctwalk.R;
import com.ctwalkapp.ctwalk.Utils.MyApplication;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AfterLoginActivty extends BaseActivity {

    private EditText CountryText,AgeTextView,CityTextView,FirstNameText,LastNameText;
    private SharedPreferences mSharedPref;
    private final String kUSERID = "CTwalk_login_userid";
    private final String kUSERNAME = "CTwalk_login_username";
    private final String KName =  "CTwalk_login_name";
    private final int NUMBEROFTRIES = 5;
    private String FirstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login_activty);

        mSharedPref = getApplicationContext().getSharedPreferences("CTwalkPref", 0);
        CountryText = (EditText)findViewById(R.id.countryText);
        CityTextView = (EditText)findViewById(R.id.HomeTownText);
        AgeTextView = (EditText)findViewById(R.id.AgeTextView);
        FirstNameText = (EditText)findViewById(R.id.FirstNameText);
        LastNameText = (EditText)findViewById(R.id.LastNameText);
        FirstName = ((MyApplication) getApplication()).getFirstName();
        String LastName =  ((MyApplication) getApplication()).getLastName();
        FirstNameText.setText(FirstName);
        LastNameText.setText(LastName);
        final Button button = (Button) findViewById(R.id.MoveToSearchButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (loginChecks())
                {
                    findViewById(R.id.ErrorTextView).setVisibility(View.INVISIBLE);
                    AddUserFromLoginAsyncTask getAllGroupsFromCityAsyncTask =  new AddUserFromLoginAsyncTask();
                    getAllGroupsFromCityAsyncTask.execute();
                }else{
                    findViewById(R.id.ErrorTextView).setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private boolean addUser()

    {
        boolean isUserIsLegel = false;
        User user =((MyApplication)getApplication()).getUser();
        CTwalkService service = ((MyApplication)getApplication()).getService();
        user.Age = Integer.parseInt(AgeTextView.getText().toString());
        user.CityId = 1;
        user.LastSeen = getLastSeen();
        Gson gson = new Gson();
        String sUser = gson.toJson(user);
        Output output = service.AddUser(sUser);
        if(output.success == true) {
                isUserIsLegel = true;
                ((MyApplication) getApplication()).setUserId(Integer.parseInt(output.value));
                ((MyApplication) getApplication()).setUserName(user.Email);
                ((MyApplication) getApplication()).setFirstName(FirstName);
                successLogin();
            }
        return isUserIsLegel;

    }
    private String getLastSeen()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //get current date time with Date()
        Date date = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Date d=new Date();
        StringBuilder dateAndTime = new StringBuilder();
        dateAndTime.append(dateFormat.format(date));
        dateAndTime.append("T");
        dateAndTime.append(timeFormat.format(d));

        return dateAndTime.toString();
    }


    private void successLogin() {
        SharedPreferences.Editor editor = mSharedPref.edit();

        //on the login store the login
        editor.putInt(kUSERID, ((MyApplication) getApplication()).getUserId());
        editor.putString(kUSERNAME, ((MyApplication) getApplication()).getUserName());
        editor.putString(KName, ((MyApplication) getApplication()).getFirstName());
        editor.commit();
    }


    private boolean loginChecks()
    {
        String sCity = CityTextView.getText().toString();
        String sCountryText = CountryText.getText().toString();
        String sAgeTextView = AgeTextView.getText().toString();

        boolean result = true;

        if (sCountryText.isEmpty() || sCity.isEmpty() || sAgeTextView.isEmpty() ) {
            result = false;
        }

        if(!sCity.matches("[a-zA-Z]+")){
            result = false;
        }
            return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_after_login_activty, menu);
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

    private boolean WarpperForLoginThroughApp()
    {
        boolean ResultOfWarpper = false;
        int numberOfTries= 0;
        while (!ResultOfWarpper && numberOfTries<NUMBEROFTRIES) {
            try {
                if (addUser()) {
                    ResultOfWarpper = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                numberOfTries++;
            }
        }
        return ResultOfWarpper;
    }

    public void MoveToMainWithOutLogin(View v) {
        startActivity(new Intent(getApplicationContext(), RoutesFragment.class));
    }

    public class AddUserFromLoginAsyncTask extends AsyncTask<Void,Void,Boolean >
    {

        public AddUserFromLoginAsyncTask() {}

        @Override
        protected Boolean  doInBackground(Void... params)
        {
            try {
                if(WarpperForLoginThroughApp())
                    return true;
                else return false;
            }
            catch (Exception e) {
                Log.e("addUser", e.getCause().toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            if(success)
                startActivity(new Intent(getApplicationContext(), RoutesFragment.class));
            else  findViewById(R.id.ErrorTextView).setVisibility(View.VISIBLE);
        }
    }
}
