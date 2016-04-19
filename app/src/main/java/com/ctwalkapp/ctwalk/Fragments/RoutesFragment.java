package com.ctwalkapp.ctwalk.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Wsdl2Code.WebServices.CTwalkService.Complex_Point;
import com.Wsdl2Code.WebServices.CTwalkService.Complex_Route;
import com.ctwalkapp.ctwalk.Activities.AddressActivity;
import com.ctwalkapp.ctwalk.Entities.BaseActivity;
import com.ctwalkapp.ctwalk.R;
import com.ctwalkapp.ctwalk.Utils.MyApplication;
import com.ctwalkapp.ctwalk.Utils.RoutesAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Delayed;

public class RoutesFragment extends BaseActivity{

    private String userName;
    private TextView userNameTextView;
    private List<Complex_Route> listRoutes= new ArrayList<>();
    private List<Complex_Point> listPoints = new ArrayList<>();
    private Complex_Point m_Point;
    private Button BtnCreateANewWalk;
    private Integer randNumber;
    private static Random rand = new Random();


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_routes);

    final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 1000);

        userName = ((MyApplication)getApplication()).getFirstName();
        listRoutes = ((MyApplication)getApplication()).getUserListRoutes();
        ListView resultListView = (ListView)findViewById(R.id.resultListviewRoutes);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_Title);
        setLayoutBackgroundImage(relativeLayout);

        userNameTextView = (TextView)findViewById(R.id.textView_RouteTitle);
        userNameTextView.setText("Hi, " + userName);
        BtnCreateANewWalk = (Button)findViewById(R.id.BtnCreateANewWalk);
        ListAdapter theAdapter = new RoutesAdapter(this, listRoutes);
        resultListView.setAdapter(theAdapter);
        BtnCreateANewWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddressActivity.class));
            }
        });

        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ((MyApplication)getApplication()).setSelectedRoute(listRoutes.get(position));

                try {
                    getListPointsFromRoute(listRoutes.get(position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //TODO: move to pick route view
                startActivity(new Intent(getApplicationContext(), RouteSlidingFragment.class));
            }
        });

    }


    @Override
    public void onBackPressed() {
       // startActivity(new Intent(getApplicationContext(), RoutesFragment.class));
        Toast.makeText(this, "to Exit please press on the exit button", Toast.LENGTH_LONG).show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    private void getListPointsFromRoute(Complex_Route route) throws JSONException
    {
        //listPoints.clear();
        listPoints = route.Points;
        ((MyApplication)getApplication()).setListPoints(listPoints);
    }

    public static int randInt(int min, int max)
    {

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public void setLayoutBackgroundImage(RelativeLayout i_RelativeLayout)
    {
        randNumber = randInt(0,9);

        switch (randNumber)
        {
            case 0:
                i_RelativeLayout.setBackgroundResource(R.drawable.city0);
                break;
            case 1:
                i_RelativeLayout.setBackgroundResource(R.drawable.city1);
                break;
            case 2:
                i_RelativeLayout.setBackgroundResource(R.drawable.city2);
                break;
            case 3:
                i_RelativeLayout.setBackgroundResource(R.drawable.city3);
                break;
            case 4:
                i_RelativeLayout.setBackgroundResource(R.drawable.city4);
                break;
            case 5:
                i_RelativeLayout.setBackgroundResource(R.drawable.city5);
                break;
            case 6:
                i_RelativeLayout.setBackgroundResource(R.drawable.city6);
                break;
            case 7:
                i_RelativeLayout.setBackgroundResource(R.drawable.city7);
                break;
            case 8:
                i_RelativeLayout.setBackgroundResource(R.drawable.city8);
                break;
            case 9:
                i_RelativeLayout.setBackgroundResource(R.drawable.city9);
                break;
            default:
                i_RelativeLayout.setBackgroundResource(R.drawable.city0);
                break;
        }

    }


}
