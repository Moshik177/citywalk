package com.ctwalkapp.ctwalk.Fragments;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.Wsdl2Code.WebServices.CTwalkService.Complex_Route;
import com.Wsdl2Code.WebServices.CTwalkService.Route;
import com.ctwalkapp.ctwalk.Entities.DirectionDetails;
import com.ctwalkapp.ctwalk.R;
import com.ctwalkapp.ctwalk.Utils.DirectionAdapter;
import com.ctwalkapp.ctwalk.Utils.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by david on 02/09/15.
 */
public class NavRouteDirecTab extends Fragment
{
    private View view;
    private Complex_Route route;
    private String directionStr;
    private String[] directionArr;
    private List<DirectionDetails> directionList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        route = ((MyApplication)getActivity().getApplication()).getSelectedRoute();

        try {
            getRouteDirection(route);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getRouteDirection(Complex_Route route) throws JSONException {
        JSONArray routeJson;
        routeJson = new JSONArray(route.Directions);
        //add first point for direction list
        DirectionDetails directionDetailsHead = new DirectionDetails(0, 5, null, "Stopover 0", 0);
        directionList.add(directionDetailsHead);

        for (int i=0;i<routeJson.length();i++)
        {
            JSONObject jsonObject = routeJson.getJSONObject(i);
            double distance = jsonObject.getDouble("distance");
            int sign = jsonObject.getInt("sign");
            String interval = jsonObject.getString("interval");
            String text = jsonObject.getString("text");
            int time = jsonObject.getInt("time");

            DirectionDetails directionDetails = new DirectionDetails(distance, sign, interval, text, time);

            directionList.add(directionDetails);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_route_nav_directions, container, false);

        ListView listView = (ListView) view.findViewById(R.id.list_item_directions);
        //ListAdapter theAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, directionArr);
        ListAdapter theAdapter = new DirectionAdapter(this.getActivity(), directionList);
        listView.setAdapter(theAdapter);

        return view;
    }
}
