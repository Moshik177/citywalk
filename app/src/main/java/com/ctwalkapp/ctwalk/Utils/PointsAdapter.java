package com.ctwalkapp.ctwalk.Utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Wsdl2Code.WebServices.CTwalkService.Complex_Point;
import com.Wsdl2Code.WebServices.CTwalkService.Point;
import com.ctwalkapp.ctwalk.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 07/09/15.
 */
public class PointsAdapter extends ArrayAdapter<Complex_Point>
{
    private List<String> subList = new ArrayList<>();

    public PointsAdapter(Context context, List<Complex_Point> values)
    {
        super(context, 0, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        subList.clear();
        Complex_Point point = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.point_card_list,parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.point_Name);
        TextView description = (TextView) convertView.findViewById(R.id.point_Description);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.point_Img);
        imageView.setImageResource(R.drawable.routecreaorpin);

        name.setText(point.Name);
        description.setText(point.LocalName);

        try
        {
            getPointSubCategory(point);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        PredicateLayout subCategoryLayout_points = (PredicateLayout) convertView.findViewById(R.id.predicate_layoutSubCategory_points);
        subCategoryLayout_points.removeAllViews();
        for(int i=0; i<subList.size();i++)
        {
            TextView t = new TextView(getContext());
            t.setText(subList.get(i));

            t.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_corner));
            t.setTextColor(Color.parseColor("#1c1b20"));
            t.setPadding(16, 2, 16, 8);
            t.setSingleLine(true);
            t.setTextSize(15);
            subCategoryLayout_points.addView(t, new PredicateLayout.LayoutParams(10, 10));
        }


        return  convertView;

    }

    private void getPointSubCategory(Complex_Point point) throws JSONException
    {
        JSONArray pointsJson;
        Gson gson = new Gson();
        String Types = gson.toJson(point.Types);
        pointsJson = new JSONArray(Types);
        for (int i=0;i<pointsJson.length();i++)
        {
            JSONObject jsonObject = pointsJson.getJSONObject(i);
            String subPoint = jsonObject.getString("Name");

            subList.add(subPoint);
        }
    }
}
