package com.ctwalkapp.ctwalk.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Wsdl2Code.WebServices.CTwalkService.CTwalkService;
import com.Wsdl2Code.WebServices.CTwalkService.City;
import com.Wsdl2Code.WebServices.CTwalkService.Complex_Route;
import com.Wsdl2Code.WebServices.CTwalkService.Route;
import com.ctwalkapp.ctwalk.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoutesAdapter extends ArrayAdapter<Complex_Route>
{
    private CTwalkService service = new CTwalkService();
    private City currCity;
    private List<String> subList = new ArrayList<>();


    public RoutesAdapter(Context context, List<Complex_Route> values)
    {
        super(context, 0, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        Complex_Route route = getItem(position);
        subList.clear();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.route_card_list,parent, false);
        }

        TextView description = (TextView) convertView.findViewById(R.id.Route_Description);
        TextView city = (TextView) convertView.findViewById(R.id.Route_City);
        TextView kmT = (TextView) convertView.findViewById(R.id.Route_Km);
        TextView time = (TextView) convertView.findViewById(R.id.Route_Time);

        description.setText("From " + route.StartAddress+ ". To " + route.EndAddress);
        int km = (route.Distance/1000);

        Date date = new Date(route.Time);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateFormatted = formatter.format(date);

        city.setText("Tel Aviv - Yaffo");
        kmT.setText( String.valueOf(km) + "KM");
        time.setText(dateFormatted);

        try
        {
            getRouteSubCategory(route);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        PredicateLayout subCategoryLayout = (PredicateLayout) convertView.findViewById(R.id.predicate_layoutSubCategory);

        subCategoryLayout.removeAllViews();
        for(int i=0; i<subList.size();i++)
        {
            TextView t = new TextView(getContext());
            t.setText(subList.get(i));
            t.setBackgroundColor(getContext().getResources().getColor(R.color.ButtonSearchBeforePress));
            t.setSingleLine(true);
            t.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_corner));
            t.setTextColor(Color.parseColor("#1c1b20"));
            t.setPadding(16, 2, 16, 8);
            t.setSingleLine(true);
            t.setTextSize(15);
            subCategoryLayout.addView(t, new PredicateLayout.LayoutParams(25, 25));
        }


        return  convertView;
    }

    private void getRouteSubCategory(Complex_Route route) throws JSONException
    {
        JSONArray pointsJson;
        Gson gson = new Gson();
        String points = gson.toJson(route.Points);
        pointsJson = new JSONArray(points);
        for (int i=0;i<pointsJson.length();i++)
        {
            JSONObject jsonObject = pointsJson.getJSONObject(i);
            String subCategories = jsonObject.getString("SubCategories");
            JSONArray subCategoriesJson = new JSONArray(subCategories);
            for (int j = 0; j < subCategoriesJson.length(); j++)
            {
                JSONObject subjsonObject = subCategoriesJson.getJSONObject(j);
                String subCatName = subjsonObject.getString("Name");

                subList.add(subCatName);
            }
        }
    }

}