package com.ctwalkapp.ctwalk.Fragments;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Wsdl2Code.WebServices.CTwalkService.CTwalkService;
import com.Wsdl2Code.WebServices.CTwalkService.Complex_Point;
import com.Wsdl2Code.WebServices.CTwalkService.Complex_Route;
import com.Wsdl2Code.WebServices.CTwalkService.Output;
import com.Wsdl2Code.WebServices.CTwalkService.Point;
import com.Wsdl2Code.WebServices.CTwalkService.Route;
import com.ctwalkapp.ctwalk.Activities.AddressActivity;
import com.ctwalkapp.ctwalk.Activities.SearchActivity;
import com.ctwalkapp.ctwalk.R;
import com.ctwalkapp.ctwalk.Utils.AutoResizeTextView;
import com.ctwalkapp.ctwalk.Utils.PointsAdapter;
import com.ctwalkapp.ctwalk.Utils.MyApplication;
import com.google.gson.Gson;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by david on 02/09/15.
 */
public class NavRouteInfoTab extends Fragment
{
    private static CTwalkService service = new CTwalkService();
    private View view;
    private Complex_Route route;
    private Complex_Route preSaveRoute;
    private String[] listComments = {"Comment 1", "Comment 2", "Comment 3", "Comment 4", "Comment 5"};
    private int mPosition = 0;
    private ImageSwitcher imageSwitcher;
    private Button btnNext;
    private ImageView approvePoint,cancelPoint;
    private TextView AddTextView ,CancelTextView;
    private List<Complex_Point> listPoints = new ArrayList<>();
    private Point m_Point;
    private final int NUMBEROFTRIES = 5;
    private final String kRoutes = "CTwalk_Routes";
	private RelativeLayout relativeLayout;
    private SharedPreferences mSharedPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPref = getActivity().getApplicationContext().getSharedPreferences("CTwalkPref", 0);
        route = ((MyApplication) getActivity().getApplication()).getSelectedRoute();
        listPoints.clear();
        listPoints = ((MyApplication)getActivity().getApplication()).getListPoints();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_route_nav_info, container, false);
        AutoResizeTextView from = (AutoResizeTextView) view.findViewById(R.id.textView_Route_From);
        AutoResizeTextView to = (AutoResizeTextView) view.findViewById(R.id.textView_Route_To);
        TextView city = (TextView) view.findViewById(R.id.Route_City);
        TextView kmT = (TextView) view.findViewById(R.id.Route_Km);
        TextView time = (TextView) view.findViewById(R.id.Route_Time);

        from.setText("From " + route.StartAddress);
        to.setText("To " + route.EndAddress);
        int km = (route.Distance/1000);
        Date date = new Date(route.Time);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateFormatted = formatter.format(date);

        city.setText("Tel Aviv - Yaffo");
        kmT.setText( String.valueOf(km) + "KM");
        time.setText(dateFormatted);

        ListView listView = (ListView) view.findViewById(R.id.list_item_points);
        //ListAdapter theAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, listComments);
        ListAdapter theAdapter = new PointsAdapter(this.getActivity(), listPoints);
        listView.setAdapter(theAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //todo
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //todo
            }
        });

        approvePoint =(ImageView)view.findViewById(R.id.approvepoint);
        AddTextView =(TextView)view.findViewById(R.id.AddTextView);
        cancelPoint =(ImageView)view.findViewById(R.id.cancelPoint);
        CancelTextView =(TextView)view.findViewById(R.id.CancelTextView);

        if(((MyApplication) getActivity().getApplication()).getIsItNewRoute()) {
            approvePoint.setVisibility(View.VISIBLE);
            AddTextView.setVisibility(View.VISIBLE);
            cancelPoint.setVisibility(View.VISIBLE);
            CancelTextView.setVisibility(View.VISIBLE);
            ((MyApplication) getActivity().getApplication()).setIsItNewRouteFalse();
        }
        else {
            approvePoint.setVisibility(View.INVISIBLE);
            AddTextView.setVisibility(View.INVISIBLE);
            cancelPoint.setVisibility(View.INVISIBLE);
            CancelTextView.setVisibility(View.INVISIBLE);
        }

            approvePoint.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AddPointMethod(v);
                }

            });


        cancelPoint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelMethod(v);
            }

        });

        return view;
    }


    public void AddPointMethod(View v) {

        approvePoint.setVisibility(view.INVISIBLE);
        AddTextView.setVisibility(View.INVISIBLE);
        cancelPoint.setVisibility(View.INVISIBLE);
        CancelTextView.setVisibility(View.INVISIBLE);

        Thread thread = new Thread() {
            @Override
            public void run() {
                CTwalkService service = ((MyApplication) getActivity().getApplication()).getService();
                int UserId =   ((MyApplication) getActivity().getApplication()).getUserId();
                boolean ResultOfWarpper = false;
                int numberOfTries= 0;
                Output routeOutput = null;
                Gson gson = new Gson();
                while (!ResultOfWarpper && numberOfTries<NUMBEROFTRIES) {
                    try {
                        route.UserId = UserId;
                        Route RouteToSend = convertComplexToReguler(route);
                        String gsonRoute = gson.toJson(RouteToSend);
                        routeOutput = service.AddRoute(gsonRoute);
                        if (routeOutput.success) {
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
                    List<Complex_Route> routes = ((MyApplication) getActivity().getApplication()).getUserListRoutes();
                    routes.add(route);
                    Gson gsonRoutes = new Gson();
                    gsonRoutes.toJson(routes);
                    successLogin(gsonRoutes.toString());
                    ((MyApplication) getActivity().getApplication()).setUserListRoutes(routes);
                    String list =  routes.toString();
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), RoutesFragment.class);
                    getActivity().startActivity(intent);
                }

            }
        };
        thread.start();

    }

    private void successLogin(String route) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        //on the login store the login
        editor.putString(kRoutes, route);
        editor.commit();
    }


    private Route convertComplexToReguler(Complex_Route route) {

        StringBuilder Result = new StringBuilder();
        for (Complex_Point Point : route.Points) {
            int pointId = Point.PointId;
            Result.append(Integer.toString(pointId)+ ",");
        }
        if(!Result.toString().equals("")) {
            Result.deleteCharAt(Result.length() - 1);
        }

        Route routeToSend = new Route(route.RouteId,route.UserId, Result.toString(),route.StartAddress,route.EndAddress, route.StartPoint, route.EndPoint,
                route.StartPointLatLng, route.EndPointLatLng, route.Directions, route.Line, route.RouteImage, route.Distance, route.Time);
        return routeToSend;
    }

    public void cancelMethod(View v) {

        approvePoint.setVisibility(view.INVISIBLE);
        AddTextView.setVisibility(View.INVISIBLE);
        cancelPoint.setVisibility(View.INVISIBLE);
        CancelTextView.setVisibility(View.INVISIBLE);
        ((MyApplication) getActivity().getApplication()).setTheNumberOfThePageToTheCurrPoint(1);
        Intent intent = new Intent();
        intent.setClass(getActivity(), AddressActivity.class);
        getActivity().startActivity(intent);
    }
}