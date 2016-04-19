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
import com.Wsdl2Code.WebServices.CTwalkService.SubCategoryType;
import com.ctwalkapp.ctwalk.Entities.DirectionDetails;
import com.ctwalkapp.ctwalk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by david on 07/09/15.
 */
public class DirectionAdapter extends ArrayAdapter<DirectionDetails>
{

    private List<String> subList = new ArrayList<>();
    private List<Complex_Point> pointList = new ArrayList<>();

    public DirectionAdapter(Context context, List<DirectionDetails> values)
    {
        super(context, 0, values);
        pointList = MyApplication.getListPoints();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //return super.getView(position, convertView, parent);
        DirectionDetails direction = getItem(position);


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.direction_card_list,parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.direc_Text);
        TextView textView_km = (TextView) convertView.findViewById(R.id.direc_km);
        TextView textView_time = (TextView) convertView.findViewById(R.id.direc_time);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.direc_Img);

        TextView textView_pointname = (TextView) convertView.findViewById(R.id.textView_pointname);
        PredicateLayout subCategoryLayout_direction = (PredicateLayout) convertView.findViewById(R.id.predicate_layoutSubCategory_direction);
        subCategoryLayout_direction.removeAllViews();
        if (direction.text.contains("Stopover"))
        {
            String text = direction.text;
            text = text.replaceAll("\\D+","");
            getPointSubCategoryByPointID(Integer.parseInt(text));
            String name = pointList.get(Integer.parseInt(text)).Name;
            textView_pointname.setText(name);
            textView.setText(null);
            textView_km.setText(null);
            textView_time.setText(null);



            for(int i=0; i<subList.size();i++)
            {
                TextView t = new TextView(getContext());
                t.setText(subList.get(i));
                t.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_corner));
                t.setTextColor(Color.parseColor("#1c1b20"));
                t.setPadding(16, 2, 16, 8);
                t.setSingleLine(true);
                t.setTextSize(15);
                subCategoryLayout_direction.addView(t, new PredicateLayout.LayoutParams(10, 10));
            }
        }
        else
        {
            textView_pointname.setText(null);
            textView.setText(direction.text);
            //textView.setTypeface(null, Typeface.BOLD);
            if (direction.distance == 0.0)
            {
                textView_km.setText(null);
                textView_time.setText(null);
            }
            else
            {
                textView_km.setText(String.valueOf(Math.ceil(direction.distance)) + 'm');
                Date date = new Date(direction.time);
                DateFormat formatter = new SimpleDateFormat("mm:ss");
                String dateFormatted = formatter.format(date);

                textView_time.setText(dateFormatted);
            }

        }

        switch (direction.sign)
        {
            case 0:
                imageView.setImageResource(R.drawable.forwarddirection);
                break;
            case -1:
                imageView.setImageResource(R.drawable.slighleftdirection);
                break;
            case 1:
                imageView.setImageResource(R.drawable.slightrightdirection);
                break;
            case -2:
                imageView.setImageResource(R.drawable.leftdirection);
                break;
            case 2:
                imageView.setImageResource(R.drawable.rightdirections);
                break;
            case -3:
                imageView.setImageResource(R.drawable.leftdirection);
                break;
            case 3:
                imageView.setImageResource(R.drawable.rightdirections);
                break;
            case 4:
                imageView.setImageResource(R.drawable.routecreaorpin);
                break;
            case 5:
                imageView.setImageResource(R.drawable.routecreaorpin);
                break;
            case 6:
                imageView.setImageResource(R.drawable.routecreaorpin);
                break;
            default:
                break;

        }
        return  convertView;

    }

    private void getPointSubCategoryByPointID(int position)  {

        Complex_Point point;
        subList.clear();
        point = pointList.get(position);
        for (int i=0;i<point.Types.size();i++)
        {
            SubCategoryType currSub = point.Types.get(i);
            String subPoint = currSub.Name;
            subList.add(subPoint);
        }
    }
}