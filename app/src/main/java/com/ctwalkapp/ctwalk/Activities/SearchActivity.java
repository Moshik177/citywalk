package com.ctwalkapp.ctwalk.Activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Wsdl2Code.WebServices.CTwalkService.CTwalkService;
import com.Wsdl2Code.WebServices.CTwalkService.Complex_Point;
import com.Wsdl2Code.WebServices.CTwalkService.Complex_Route;
import com.Wsdl2Code.WebServices.CTwalkService.Output;
import com.Wsdl2Code.WebServices.CTwalkService.Point;
import com.Wsdl2Code.WebServices.CTwalkService.Restriction;
import com.Wsdl2Code.WebServices.CTwalkService.Route;
import com.ctwalkapp.ctwalk.Entities.BaseActivity;
import com.ctwalkapp.ctwalk.Fragments.RouteSlidingFragment;
import com.ctwalkapp.ctwalk.R;
import com.ctwalkapp.ctwalk.Utils.CustomGrid;
import com.ctwalkapp.ctwalk.Utils.MyApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {

    private SharedPreferences mSharedPref;
    private final String kPOINTS = "CTwalk_Search_Points";
    private final int NUMBEROFTRIES = 5;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        if(((MyApplication)getApplication()).getTheNumberOfThePage() == 1) {
            ((MyApplication)getApplication()).UpdateIsThisTheFirstPoint();
            TextView Category =(TextView)findViewById(R.id.Category);
            Category.setText(((MyApplication)getApplication()).getAddressName());

        } else {
            TextView numberOfPoint = (TextView)findViewById(R.id.numberOfPoint);
            int numberOfPointInt =((MyApplication) getApplication()).getTheNumberOfThePage();
            numberOfPoint.setText(Integer.toString(numberOfPointInt));
            ((MyApplication)getApplication()).UpdateIsThisTheFirstPoint();
            TextView Category =(TextView)findViewById(R.id.Category);
            Category.setText(((MyApplication) getApplication()).getChoosingTypeOfAttribute());
            TextView Search =(TextView)findViewById(R.id.Search);
            Search.setVisibility(View.VISIBLE);
            ImageView searchroutePic =(ImageView)findViewById(R.id.searchroutePic);
            searchroutePic.setVisibility(View.VISIBLE);
            ImageView addAPointPic =(ImageView)findViewById(R.id.addAPointPicSecond);
            addAPointPic.setVisibility(View.VISIBLE);
            TextView addAPoint =(TextView)findViewById(R.id.addAPointSecond);
            addAPoint.setVisibility(View.VISIBLE);
            ImageView addAPointPicOne =(ImageView)findViewById(R.id.addAPointPic);
            addAPointPicOne.setVisibility(View.INVISIBLE);
            TextView addAPointOne =(TextView)findViewById(R.id.addAPoint);
            addAPointOne.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        int numberOfPoints = ((MyApplication)getApplication()).getTheNumberOfThePage();
        ((MyApplication)getApplication()).setTheNumberOfThePageToTheCurrPoint(numberOfPoints - 1);
         super.onBackPressed();
    }

    public void OpenLinearLayout(View v) {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.LinearLayout);
        linearLayout.setBackgroundColor(getResources().getColor(R.color.ActionBarColor));
        GridView grid;
        String[] web = {
               "Food & Drinks",
                "Shopping",
                "Art & Culture",
                "History & Religion",
                "Family Fun",
                "Chillax"
        } ;
        int[] imageId = {
                R.mipmap.foodw,
                R.mipmap.shoppingw,
                R.mipmap.artw,
                R.mipmap.historyw,
                R.mipmap.familyw,
                R.mipmap.chillaxw,
        };
        CustomGrid adapter = new CustomGrid(SearchActivity.this, web, imageId);
        grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View ChoosingAttribute,
                                    int position, long id) {
                String nameTypeOfAttribute = null;
                int categoryId = 0;
                if (position == 0) {
                    nameTypeOfAttribute = "Food And Drinks";
                    categoryId = 2;
                }

                else if (position == 2) {
                    nameTypeOfAttribute = "Art And Culture";
                    categoryId = 5;
                }

                else if (position == 1) {
                    nameTypeOfAttribute = "Shopping";
                    categoryId = 3;
                }

                else if (position == 5) {
                    nameTypeOfAttribute = "Chilling";
                    categoryId = 7;
                }

                else if (position == 4) {
                    nameTypeOfAttribute = "Family Fan";
                    categoryId = 4;
                }

                else if (position == 3) {
                    nameTypeOfAttribute = "History And Religion";
                    categoryId = 6;
                }

                ((MyApplication) getApplication()).setChoosingTypeOfAttribute(nameTypeOfAttribute);
                ((MyApplication) getApplication()).setCategoryId(categoryId);
                startActivity(new Intent(getApplicationContext(), SearchAttributeActivity.class));

            }
        });

    }

    public void OpenSearch(View v) {
            Thread thread = new Thread() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar = (ProgressBar) findViewById(R.id.login_progress);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });

                    CTwalkService service = ((MyApplication)getApplication()).getService();
                    mSharedPref = getApplicationContext().getSharedPreferences("CTwalkPref", 0);
                    String gsonListOfPoints = mSharedPref.getString(kPOINTS, "");
                    boolean ResultOfWarpper = false;
                    int numberOfTries= 0;
                    Output route = null;
                    while (!ResultOfWarpper && numberOfTries<NUMBEROFTRIES) {
                        try {
                            route = service.BuildRoute(gsonListOfPoints);
                            if (route.success) {
                                ResultOfWarpper = true;
                            } else {
                                numberOfTries++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            numberOfTries++;
                    }
                }
                if(numberOfTries == NUMBEROFTRIES) {
                } else {
                    Gson gson = new Gson();
                    Complex_Route BuildRoute = gson.fromJson(route.value,Complex_Route.class);
                    ((MyApplication)getApplication()).setSelectedRoute(BuildRoute);
                    ((MyApplication)getApplication()).setListPoints(BuildRoute.Points);
                    ((MyApplication)getApplication()).setIsItNewRoute();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                    startActivity(new Intent(getApplicationContext(), RouteSlidingFragment.class));
                }

            }
        };
        thread.start();
    }



    @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.menu_search, menu);
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

        }
