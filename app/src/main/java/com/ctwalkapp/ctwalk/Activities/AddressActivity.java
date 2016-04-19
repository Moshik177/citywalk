package com.ctwalkapp.ctwalk.Activities;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctwalkapp.ctwalk.Entities.BaseActivity;
import com.ctwalkapp.ctwalk.Fragments.RoutesFragment;
import com.ctwalkapp.ctwalk.R;
import com.ctwalkapp.ctwalk.Utils.GeocodingLocation;
import com.ctwalkapp.ctwalk.Utils.MyApplication;
import android.support.v7.app.ActionBar;
import android.widget.Toast;


public class AddressActivity extends BaseActivity {

    private double KLat = 32.080341;
    private double Klog = 34.780639;
    private double x, y;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
    }

    public void AnywhereTheStreetsWillTakeUsFunc(View v) {

        TextView tv = (TextView) findViewById(R.id.myAddress);
        String mAddressOutput = "Rabin Square,Tel Aviv,Israel";
        Double KLat = 32.080341;
        Double Klog = 34.780639;
        ((MyApplication) getApplication()).setAddressName(mAddressOutput);
        ((MyApplication) getApplication()).setLatitude(KLat);
        ((MyApplication) getApplication()).setLongitude(Klog);
        ((MyApplication)getApplication()).setTheNumberOfThePageToTheCurrPoint(1);
        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
    }

    public void IHaveAnAddressFunc(View v) {
        final EditText havingAddress = (EditText) findViewById(R.id.havingAddress);
        ImageView toSearch = (ImageView) findViewById(R.id.toSearch);
        havingAddress.setVisibility(View.VISIBLE);
        toSearch.setVisibility(View.VISIBLE);
        toSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String sHavingAddress = havingAddress.getText().toString();
                if (!sHavingAddress.isEmpty()) {//&& validateAddress(sHavingAddress)) {//&& funcfromDB ) {
                    findViewById(R.id.ErrorTextView).setVisibility(View.INVISIBLE);
                    GeocodingLocation locationAddress = new GeocodingLocation();
                    locationAddress.getLocationFromAddress(sHavingAddress,
                            getApplicationContext(), new GeocoderHandler());
                    ((MyApplication)getApplication()).setTheNumberOfThePageToTheCurrPoint(1);
                    //((MyApplication) getApplication()).setAddressName(sHavingAddress);
                } else {
                    findViewById(R.id.ErrorTextView).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private boolean validateAddress(String address) {
        return address.matches("\\d+\\s+(([a-zA-Z])+|([a-zA-Z]+\\s+[a-zA-Z]+))\\s+[a-zA-Z]*");
    } // end method validateAddress


    public void MyCurrentLocationFunc(View v) {

        final LocationManager lm;
        LocationListener locationListenerNetwork;
        Location location;
        final GeocodingLocation locationAddress = new GeocodingLocation();
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            x = location.getLatitude();
            y = location.getLongitude();
            ((MyApplication) getApplication()).setLatitude(x);
            ((MyApplication) getApplication()).setLatitude(y);
        }

        locationListenerNetwork = new LocationListener() {
            public void onLocationChanged(Location location) {

                x = location.getLatitude();
                y = location.getLongitude();
                ((MyApplication) getApplication()).setLatitude(x);
                ((MyApplication) getApplication()).setLatitude(y);
                lm.removeUpdates(this);
                Context context = getApplicationContext();

            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        locationAddress.getAddressFromLocation(x, y, getApplicationContext(), new GeocoderHandler());
        //((MyApplication) getApplication()).setAddressName(mAddressOutput);
        ((MyApplication)getApplication()).setTheNumberOfThePageToTheCurrPoint(1);
        // startActivity(new Intent(getApplicationContext(), SearchActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_addrass_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(final Message message) {

            switch (message.what) {
                case 1:
                    bundle = message.getData();
                    ((MyApplication) getApplication()).setLatitude(bundle.getDouble("Latitude"));
                    ((MyApplication) getApplication()).setLongitude(bundle.getDouble("Longitude"));
                    ((MyApplication) getApplication()).setAddressName(bundle.getString("Address"));
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    break;
                case 2:
                    findViewById(R.id.ErrorTextView).setVisibility(View.VISIBLE);
                case 3:
                    bundle = message.getData();
                    ((MyApplication) getApplication()).setAddressName(bundle.getString("address"));
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    break;
                case 4:
                    findViewById(R.id.ErrorTextView).setVisibility(View.VISIBLE);
            }

        }
    }
}
