package com.ctwalkapp.ctwalk.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import com.Wsdl2Code.WebServices.CTwalkService.CTwalkService;
import com.Wsdl2Code.WebServices.CTwalkService.Complex_Route;
import com.Wsdl2Code.WebServices.CTwalkService.Output;
import com.Wsdl2Code.WebServices.CTwalkService.User;
import com.ctwalkapp.ctwalk.Fragments.RoutesFragment;
import com.ctwalkapp.ctwalk.R;
import com.ctwalkapp.ctwalk.Utils.MyApplication;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.gson.Gson;

public class LoginActivity extends FragmentActivity implements View.OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener
{

    // Facebook login references
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    private LoginButton mFacebookLoginButton;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton btnSignIn;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private static final int GOOGLE_SIGIN = 100;
    private User user = new User();
    private static CTwalkService service = new CTwalkService();
    private List<Complex_Route> listRoutes= new ArrayList<>();
    private SharedPreferences mSharedPref;
    private String key1 = "Bar12345Bar12345"; // 128 bit key
    private String key2 = "ThisIsASecretKet";
    private final String kUSERID = "CTwalk_login_userid";
    private final String kUSERNAME = "CTWalk_login_username";
    private final String KName = "CTwalk_login_name" ;
    private final int NEW_USER = 1;
    private final int NUMBEROFTRYS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        mSharedPref = getApplicationContext().getSharedPreferences("CTwalkPref", 0); // 0 - for private mode;
        int alreadyLoggedIn = mSharedPref.getInt(kUSERID, 0);
       if (alreadyLoggedIn != 0 ) {

            ((MyApplication) getApplication()).setUserId(mSharedPref.getInt(kUSERID, 0));
            ((MyApplication) getApplication()).setFirstName(mSharedPref.getString(KName, "DEFAULT"));
           ((MyApplication) getApplication()).setUserName(mSharedPref.getString(kUSERNAME, "DEFAULT"));
           startActivity(new Intent(getApplicationContext(), RoutesFragment.class));
        } else {


        // Initializing google plus api client
        btnSignIn = (SignInButton) findViewById(R.id.LoginGooglebutton);
           btnSignIn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   // Signin button clicked
                   signInWithGplus();
                }
            });
        setGooglePlusButtonText(btnSignIn, "Log in with Google");
        mAccessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                    updateWithToken(newAccessToken);
                }
            };


            mCallbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    UserLoginWithFacebookAsyncTask signUpWithFacebookAsyncTask = new UserLoginWithFacebookAsyncTask(loginResult.getAccessToken(), true);
                    signUpWithFacebookAsyncTask.execute((Void) null);
                }

                @Override
                public void onCancel() {
                    // App code
                    Log.v("LoginActivity", "cancel");
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                    Log.e("LoginActivity", exception.getCause().toString());
                }
            });
            updateWithToken(AccessToken.getCurrentAccessToken());
            mFacebookLoginButton = (LoginButton) findViewById(R.id.LoginFacebookbutton);
            mFacebookLoginButton.setReadPermissions(Arrays.asList("public_profile, email"));

            // Disable auto popup of the keyboard
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            Button mSignInButton = (Button) findViewById(R.id.buttonLoginApp);
            mSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveToSignUpActivity();
                }
            });

            //Button mSkipInButton = (Button) findViewById(R.id.skiploginbutton);
            //mSkipInButton.setOnClickListener(new View.OnClickListener() {
              //  @Override
              //  public void onClick(View view) {
              //      startActivity(new Intent(getApplicationContext(), RoutesFragment.class));
              //  }
           // });

        }
    }

    @Override
    public void onClick (View v){
            switch (v.getId()) {
                case R.id.LoginGooglebutton:
                    // Signin button clicked
                    signInWithGplus();
                    break;
            }
        }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        //Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Update the UI after signin
        updateUI(true);

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        updateUI(false);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, GOOGLE_SIGIN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == GOOGLE_SIGIN) {
            if (responseCode == RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
        else
        {
            super.onActivityResult(requestCode, responseCode, intent);
            mCallbackManager.onActivityResult(requestCode, responseCode, intent);
        }
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            if (currentPerson != null) {
                try {

                    initService();
                    // Show signed-in user's name
                    String[] fullName = currentPerson.getDisplayName().split(" ");
                    String currentAccount = Plus.AccountApi.getAccountName(mGoogleApiClient);
                    user.FirstName = fullName[0];
                    user.LastName = fullName[1];
                    ((MyApplication) getApplication()).setEmailAddress(currentAccount);
                    if(currentPerson.getGender() == Person.Gender.FEMALE) {
                        user.Gender = false;
                    }
                    else user.Gender = true;
                    user.Email = (currentAccount);
                    user.Password ="";// hashString(user.Email);
                    user.GoogleId = currentPerson.getId();
                    addUserDetails();
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                btnSignIn.setVisibility(View.VISIBLE);
                //profile_layout.setVisibility(View.GONE);
            }
        }
    }

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }


    private void updateWithToken(final AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    UserLoginWithFacebookAsyncTask userLoginWithFacebookAsyncTask = new UserLoginWithFacebookAsyncTask(currentAccessToken, false);
                    userLoginWithFacebookAsyncTask.execute((Void) null);
                }
            }, 1);
        }
    }

    private void moveToSignUpActivity()
    {
        final EditText Email = (EditText)findViewById(R.id.Email);
        final EditText Password = (EditText)findViewById(R.id.Password);
        final String EmailString = Email.getText().toString();
        final String PasswordString = Password.getText().toString();

        if(PasswordString.isEmpty() || EmailString.isEmpty() ){
            findViewById(R.id.ErrorTextView).setVisibility(View.VISIBLE);
        } else {
            ((MyApplication) getApplication()).setEmailAddress(EmailString);
            ((MyApplication) getApplication()).setPassword(PasswordString);
            LaunchDialog launchDialog = new LaunchDialog(EmailString, PasswordString);
            launchDialog.execute((Void) null);
        }

    }

    protected Dialog onCreateDialog(int n){
        Dialog d = null;
        switch(n){
            case NEW_USER:
                d= new AlertDialog.Builder(LoginActivity.this)
                        .setMessage("The User Doesn't Exist,Do you want to create it ?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        startActivity(new Intent(getApplicationContext(), LoginThroughAppActivty.class));
                                    }
                                })
                        .create();
                break;
            default:
                d= null;
                break;
        }
        return d;
    }

    public static String decrypt(String key1, String key2, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(key2.getBytes("UTF-8"));

            SecretKeySpec skeySpec = new SecretKeySpec(key1.getBytes("UTF-8"),
                    "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public class UserLoginWithFacebookAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private final AccessToken mAccessToken;
        private final boolean mIsItTheFirstTimeThatLogin;

        UserLoginWithFacebookAsyncTask(AccessToken accessToken,boolean isItTheFirstTimeThatLogin )
        {
            mAccessToken = accessToken;
            mIsItTheFirstTimeThatLogin = isItTheFirstTimeThatLogin;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            userGraphRequest(mAccessToken);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(!mIsItTheFirstTimeThatLogin) {
                //UserLoginTask authTask = new UserLoginTask(user.Email, user.Password);
                //authTask.execute((Void) null);
            }else{
                ((MyApplication)getApplication()).setUser(user);
                startActivity(new Intent(getApplicationContext(), AfterLoginActivty.class));
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    private void initService()
    {
        service.setUrl("http://ctwalkapp.com/CTwalkService/Service.svc?wsdl");
        service.setTimeOut(7);
        ((MyApplication)getApplication()).setService(service);

    }

    private void userGraphRequest(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                // Application code
                Log.v("LoginActivity", response.toString());
                try {
                    initService();
                    String[] fullName = object.getString("name").split(" ");
                    user.FirstName = fullName[0];
                    user.LastName = fullName[1];
                    user.Email = (object.getString("email"));
                    ((MyApplication) getApplication()).setEmailAddress(user.Email);
                    user.Password = "";// hashString(user.Email);
                    user.FacebookId = (object.getString("id"));
                    if (object.getString("gender").equals("male"))
                        user.Gender = true;
                    else user.Gender = false;

                    addUserDetails();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, name, email, gender");
            request.setParameters(parameters);
            request.executeAndWait();
        }catch (Throwable e) {
            e.printStackTrace();
        }
    }


    private void addUserDetails() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    ((MyApplication) getApplication()).setFirstName(user.FirstName);
                    ((MyApplication) getApplication()).setLastName(user.LastName);
                    ((MyApplication) getApplication()).setUser(user);
                    ((MyApplication) getApplication()).setUserName(user.Email);
                    String Email = user.Email;
                    Output userCheck = service.GetUserByEmail(Email);
                        if (userCheck.success) {
                            JSONObject UserJson = new JSONObject(userCheck.value);
                            int userId = UserJson.getInt("UserId");
                            ((MyApplication) getApplication()).setUserId(userId);
                            successLogin();
                            setExistUserRoute(userId);
                            startActivity(new Intent(getApplicationContext(), RoutesFragment.class));
                    } else
                        startActivity(new Intent(getApplicationContext(), AfterLoginActivty.class));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

        };
            thread.start();
        }

    private void setExistUserRoute(int UserId)
    {

        try {
        Output output = service.GetAllComplexRoutesByUserId(UserId, true);
        if (output.success)
        {
            JSONArray routesJson = null;
            routesJson = new JSONArray(output.value);
            for (int i = 0; i < routesJson.length(); i++)
            {
                Gson gson = new Gson();
                String jsonObject = routesJson.getJSONObject(i).toString();
                Complex_Route Complex_Routes = gson.fromJson(jsonObject,Complex_Route.class);
                listRoutes.add(Complex_Routes);
            }
            ((MyApplication) getApplication()).setUserListRoutes(listRoutes);

        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void successLogin() {
        SharedPreferences.Editor editor = mSharedPref.edit();

        //on the login store the login
        editor.putInt(kUSERID, ((MyApplication) getApplication()).getUserId());
        editor.putString(kUSERNAME, ((MyApplication) getApplication()).getUserName());
        editor.putString(KName, ((MyApplication) getApplication()).getFirstName());
        editor.commit();
    }


    private boolean IsTheUserExist(String email) {

        Output userCheck = service.GetUserByEmail(email);
        return userCheck.success;
    }

    private void userAndPasswordFunc(String email, String password) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.ErrorTextView).setVisibility(View.INVISIBLE);
            }
        });
        Output UserCheckPassword = service.GetUserByEmailAndPassword(email, password);
        try{
            if (UserCheckPassword.success) {
                JSONObject jsonObject = new JSONObject(UserCheckPassword.value);
                int userId = jsonObject.getInt("UserId");
                String Name = jsonObject.getString("FirstName");
                SharedPreferences.Editor editor = mSharedPref.edit();
                //on the login store the login
                ((MyApplication) getApplication()).setFirstName(Name);
                editor.putInt(kUSERID, userId);
                editor.putString(kUSERNAME, email);
                editor.putString(KName, Name);
                editor.commit();
                setExistUserRoute(userId);
                startActivity(new Intent(getApplicationContext(), RoutesFragment.class));
        } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.ErrorTextView).setVisibility(View.VISIBLE);
                    }
                });

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                int padding = (int) getResources().getDimension(R.dimen.com_facebook_share_button_padding_left);
                int drawablePadding = (int) getResources().getDimension(R.dimen.com_facebook_share_button_padding_left);
                tv.setPadding(padding, 0, padding, 0);
                tv.setCompoundDrawablePadding(drawablePadding);
                tv.setText(buttonText);
                tv.setGravity(Gravity.CENTER);
                return;
            }
        }
    }


    private boolean WarpperForLoginThroughApp(String email,String password)
    {
        boolean OpenDialog = false;
        boolean isWeGetAnAnswer = false;
        int numberOfTries= 0;
        while (!isWeGetAnAnswer && numberOfTries<NUMBEROFTRYS) {
            try {
                if (!IsTheUserExist(email)) {
                    OpenDialog = true;
                    isWeGetAnAnswer = true;
                } else {
                    userAndPasswordFunc(email, password);
                    OpenDialog = false;
                    isWeGetAnAnswer = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                numberOfTries++;
            }
        }
        if(numberOfTries == NUMBEROFTRYS) {
           int i = 5;
        }
        return OpenDialog;
    }

    public class LaunchDialog extends AsyncTask<Void,Void,Boolean>{
        String email,password;

        public LaunchDialog(String email,String password){
            this.email= email;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            initService();
            return WarpperForLoginThroughApp(email,password);
        }

        @Override
        protected void onPostExecute(final Boolean OpenDialog) {

            if (OpenDialog)
                showDialog(NEW_USER);
        }
    }

}