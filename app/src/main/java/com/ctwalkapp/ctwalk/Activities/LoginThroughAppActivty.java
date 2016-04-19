package com.ctwalkapp.ctwalk.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;
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


public class LoginThroughAppActivty extends BaseActivity {

    private EditText AgeTextView,HomeTownText,FirstNameText,LastNameText,EmailText,passwordText,countryText;
    private static CTwalkService service = new CTwalkService();
    private Spinner dropdown;
    private SharedPreferences mSharedPref;
    private final String kUSERID = "CTwalk_login_userid";
    private final String kUSERNAME = "CTwalk_login_username";
    private final String KName = "CTwalk_login_name" ;
    private String key1 = "Bar12345Bar12345"; // 128 bit key
    private String key2 = "ThisIsASecretKet";
    private final int NUMBEROFTRIES = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_through_app_activity);
        getallEditTexts();
        mSharedPref = getApplicationContext().getSharedPreferences("CTwalkPref", 0);
        final Button button = (Button) findViewById(R.id.MoveToSearchButton);
        String EmailString = ((MyApplication) getApplication()).getEmailAddress();
        String PasswordString =((MyApplication) getApplication()).getPassword();
        EmailText  = (EditText)findViewById(R.id.EmailText);
        passwordText = (EditText)findViewById(R.id.passwordText);
        EmailText.setText(EmailString);
        passwordText.setText(PasswordString);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (loginChecks()) {
                    findViewById(R.id.ErrorTextView).setVisibility(View.INVISIBLE);
                    AddUserFromLoginAsyncTask getAllGroupsFromCityAsyncTask = new AddUserFromLoginAsyncTask();
                    getAllGroupsFromCityAsyncTask.execute();

                } else {
                    findViewById(R.id.ErrorTextView).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initService()
    {
        service.setUrl("http://ctwalkapp.com/CTwalkService/Service.svc?wsdl");
        service.setTimeOut(7);
        ((MyApplication)getApplication()).setService(service);

    }

    private void getallEditTexts()
    {
        dropdown = (Spinner)findViewById(R.id.spinner_Gender);
        String[] items = new String[]{"Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        HomeTownText = (EditText)findViewById(R.id.HomeTownText);
        AgeTextView = (EditText)findViewById(R.id.AgeTextView);
        FirstNameText = (EditText)findViewById(R.id.FirstNameText);
        LastNameText = (EditText)findViewById(R.id.LastNameText);
        EmailText  = (EditText)findViewById(R.id.EmailText);
        passwordText = (EditText)findViewById(R.id.passwordText);
        countryText = (EditText)findViewById(R.id.countryText);

    }

    private boolean loginChecks()
    {
        String sFirstName = FirstNameText.getText().toString();
        String sLastName = LastNameText.getText().toString();
        String sHomeTownText = HomeTownText.getText().toString();
        String sAgeTextView = AgeTextView.getText().toString();
        String sEmailText = EmailText.getText().toString();
        String spasswordText = passwordText.getText().toString();
        String scountryText = countryText.getText().toString();

        boolean result = true;

        if (sFirstName.isEmpty() || sLastName.isEmpty() ||  sHomeTownText.isEmpty() || sAgeTextView.isEmpty() || sEmailText.isEmpty() || spasswordText.isEmpty() || scountryText.isEmpty() ) {
            result = false;
        }

        if(!sHomeTownText.matches("[a-zA-Z]+") && !sFirstName.matches("[a-zA-Z]+") && !sLastName.matches("[a-zA-Z]+") && !scountryText.matches("[a-zA-Z]+") ){
            result = false;
        }
        return result;
    }

    private boolean addUser()
    {
        boolean isUserIsLegel = false;
        User user = new User();
        user.Age = Integer.parseInt(AgeTextView.getText().toString());
        user.FirstName = FirstNameText.getText().toString();
        user.Email= EmailText.getText().toString();
        user.LastName = LastNameText.getText().toString();
        user.Password = passwordText.getText().toString();
        //user.country =countryText.getText().toString();
        user.CityId =1;
        String gender = (dropdown.getSelectedItem().toString());
        if(gender.compareTo("male") == 0)    {
            user.Gender = true;
        } else user.Gender = false;
        user.LastSeen = getLastSeen();
        Gson gson = new Gson();
        String sUser = gson.toJson(user);
        Output output = service.AddUser(sUser);

        if(output.success == true)
        {
            isUserIsLegel = true;
            ((MyApplication) getApplication()).setUserId(Integer.parseInt(output.value));
            ((MyApplication) getApplication()).setUserName(user.Email);
            ((MyApplication) getApplication()).setFirstName(user.FirstName);
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



    public static String encrypt(String key1, String key2, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(key2.getBytes("UTF-8"));

            SecretKeySpec skeySpec = new SecretKeySpec(key1.getBytes("UTF-8"),
                    "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string:"
                    + Base64.encodeToString(encrypted, Base64.DEFAULT));
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_throw_app_activty, menu);
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

    public void MoveToMainWithOutLogin(View v) {
        startActivity(new Intent(getApplicationContext(), RoutesFragment.class));
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

    public class AddUserFromLoginAsyncTask extends AsyncTask<Void,Void,Boolean >
    {
        public AddUserFromLoginAsyncTask() {}

        @Override
        protected Boolean  doInBackground(Void... params)
        {
            try {
                initService();
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
