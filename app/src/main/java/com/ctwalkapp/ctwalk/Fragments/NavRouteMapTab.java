package com.ctwalkapp.ctwalk.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Wsdl2Code.WebServices.CTwalkService.Complex_Point;
import com.Wsdl2Code.WebServices.CTwalkService.Complex_Route;
import com.ctwalkapp.ctwalk.R;



import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.Wsdl2Code.WebServices.CTwalkService.Route;
import com.ctwalkapp.ctwalk.R;
import com.ctwalkapp.ctwalk.Utils.MyApplication;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;


/**
 * Created by david on 02/09/15.
 */
public class NavRouteMapTab extends Fragment
{
    private View view;

    private double latitude = 0;
    private double longitude =0;

    public WebView webViewMap;

    //private LocationListener locationListener = new MyLocationListener();

    private Complex_Route route;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_route_nav_map, container, false);

        route = ((MyApplication)getActivity().getApplication()).getSelectedRoute();
        Gson gson = new Gson();
        String routeJson = gson.toJson(route);

        //locationListener = new MyLocationListener();
        //LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, locationListener);

        String urlLocation = "http://ctwalkapp.com/demo/map.php#"+routeJson;


        webViewMap = (WebView) view.findViewById(R.id.webView_routeMap);

        webViewMap.getSettings().setJavaScriptEnabled(true);
        webViewMap.getSettings().getAllowContentAccess();
        webViewMap.getSettings().setAppCacheEnabled(false);
        webViewMap.clearCache(true);
        webViewMap.clearFormData();
        webViewMap.clearAnimation();
        webViewMap.addJavascriptInterface(new WebViewJavaScriptInterface(getActivity().getApplicationContext()), "app");


        //GetLocation();

        //Toast.makeText(getActivity().getApplicationContext(),"GSP- "+ Double.toString(latitude), Toast.LENGTH_LONG).show();


        webViewMap.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                //String msgToSend = Double.toString(latitude) + "-" + Double.toString(longitude);
                //webViewMap.loadUrl("javascript:callFromActivity(\"" + msgToSend + "\")");
            }
        });

        webViewMap.loadUrl(urlLocation);



        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public class WebViewJavaScriptInterface
    {
        private Context context;

        /*
         * Need a reference to the context in order to sent a post message
         */
        public WebViewJavaScriptInterface(Context context)
        {
            this.context = context;
        }

        /*
         * This method can be called from Android. @JavascriptInterface
         * required after SDK version 17.
         */
        @JavascriptInterface
        public void makeToast(String message, boolean lengthLong)
        {
            //Toast.makeText(context, message, (lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
        }
        @JavascriptInterface
        public void showTestOnScreen()
        {
            //Toast.makeText(getActivity().getApplicationContext(), "chuck norris", Toast.LENGTH_SHORT).show();
        }


    }

    private final class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location locFromGps)
        {
            // called when the listener is notified with a location update from the GPS
            //Toast.makeText(getActivity().getApplicationContext(), "location change", Toast.LENGTH_SHORT).show();

            latitude = locFromGps.getLatitude();
            longitude = locFromGps.getLongitude();
            String msgToSend = Double.toString(latitude)+"-"+Double.toString(longitude);
            webViewMap.loadUrl("javascript:updateLocation(\"" + msgToSend + "\")");
        }

        @Override
        public void onProviderDisabled(String provider) {
            // called when the GPS provider is turned off (user turning off the GPS on the phone)
        }

        @Override
        public void onProviderEnabled(String provider) {
            // called when the GPS provider is turned on (user turning on the GPS on the phone)
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // called when the status of the GPS provider changes
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    public void returnToPrevView(View view)
    {
        //TODO change button

        String msgToSend = Double.toString(latitude)+"-"+Double.toString(longitude);
        webViewMap.loadUrl("javascript:callFromActivity(\"" + msgToSend + "\")");

    }

    public void GetLocation()
    {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);



        if (location != null)
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);

            String latitudeSting = Double.toString(latitude);

            //Toast.makeText(getApplicationContext(),"GSP- "+ latitudeSting, Toast.LENGTH_LONG).show();
        }
        else
        {
            //Toast.makeText(getActivity().getApplicationContext(), "GSP - Location is null", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //locationListener = null;
    }
}
