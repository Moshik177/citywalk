package com.ctwalkapp.ctwalk.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import com.Wsdl2Code.WebServices.CTwalkService.CTwalkService;
import com.Wsdl2Code.WebServices.CTwalkService.Facility;
import com.Wsdl2Code.WebServices.CTwalkService.Output;
import com.Wsdl2Code.WebServices.CTwalkService.Point;
import com.Wsdl2Code.WebServices.CTwalkService.Restriction;
import com.Wsdl2Code.WebServices.CTwalkService.SubCategory;
import com.Wsdl2Code.WebServices.CTwalkService.SubCategoryType;
import com.ctwalkapp.ctwalk.Entities.BaseActivity;
import com.ctwalkapp.ctwalk.R;
import com.ctwalkapp.ctwalk.Utils.MyApplication;
import com.ctwalkapp.ctwalk.Utils.PredicateLayout;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.util.ArrayList;
import java.util.List;

public class SearchAttributeActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private CTwalkService service;
    private Point PointToAdd = new Point();
    private ArrayList<Integer> subCategoryIds = new ArrayList<Integer>();
    private ArrayList<Integer> facilityIds = new ArrayList<Integer>();
    private ArrayList<Integer> restrictionIds = new ArrayList<Integer>();
    private List<SubCategoryType> subCategoryType = new ArrayList<SubCategoryType>();
    private List<Facility> facility = new ArrayList<Facility>();
    private List<Restriction> restriction = new ArrayList<Restriction>();
    private SharedPreferences mSharedPref;
    private final String kPOINTS = "CTwalk_Search_Points";
    private final int NUMBEROFTRIES = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_attribute);
        String TypeOfAtrribute = ((MyApplication) getApplication()).getChoosingTypeOfAttribute();
        mSharedPref = getApplicationContext().getSharedPreferences("CTwalkPref", 0);

        chooseWhichImageViewTurnVisable(TypeOfAtrribute);
        AddSubCategoryAsyncTask addSubCategoryAsyncTask =new AddSubCategoryAsyncTask();
        addSubCategoryAsyncTask.execute();
    }

    private void chooseWhichImageViewTurnVisable(String TypeOfAtrribute) {
        switch(TypeOfAtrribute)
        {
         case "Food And Drinks":
             ImageView action_foodAndDrink = (ImageView)findViewById(R.id.action_foodAndDrink);
             action_foodAndDrink.setVisibility(View.VISIBLE);
                break;
         case "Art And Culture":
             ImageView action_art = (ImageView)findViewById(R.id.action_art);
             action_art.setVisibility(View.VISIBLE);
                break;
         case "Shopping":
             ImageView action_shopping = (ImageView)findViewById(R.id.action_shopping);
             action_shopping.setVisibility(View.VISIBLE);
                break;
         case "Chilling":
             ImageView action_chilling = (ImageView)findViewById(R.id.action_chilling);
             action_chilling.setVisibility(View.VISIBLE);
                break;
         case "Family Fan":
             ImageView action_familyFan = (ImageView)findViewById(R.id.action_familyFan);
             action_familyFan.setVisibility(View.VISIBLE);
                break;
         case "History And Religion":
             ImageView action_history = (ImageView)findViewById(R.id.action_history);
             action_history.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_attribute_activity, menu);
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

    private void PutOutSpinner(List<SubCategory> subCategoryList) {

        spinner = (Spinner)findViewById(R.id.spinnerTypeAAttribute);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for(SubCategory subCategoryItem : subCategoryList) {
            adapter.add(subCategoryItem.Name);
        }
        adapter.add("Select a Type"); //This is the text that will be displayed as hint.
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount()); //set the hint the default selection so it appears on launch.
        spinner.setOnItemSelectedListener(this);

    }

    private void ShowTheDetails(List<String> DetailsSubCategory) {

        subCategoryType = parserArrayToSubCategoryType(DetailsSubCategory.get(0));
        facility = parserArrayToFacilitiesFromSubCategory(DetailsSubCategory.get(2));
        restriction =parserArrayToRestrictionsFromSubCategory(DetailsSubCategory.get(1));

        PredicateLayout subCategoryLayout = (PredicateLayout)findViewById(R.id.predicate_layoutSubCategory);
        PredicateLayout FacilityLayout = (PredicateLayout)findViewById(R.id.predicate_layoutFacility);
        PredicateLayout RestrictionLayout = (PredicateLayout)findViewById(R.id.predicate_layoutRestriction);
        TextView MustBe = (TextView) findViewById(R.id.MustBe);
        TextView NiceToHave = (TextView) findViewById(R.id.NiceToHave);

        subCategoryLayout.removeAllViews();
        FacilityLayout.removeAllViews();
        RestrictionLayout.removeAllViews();
        MustBe.setVisibility(View.INVISIBLE);
        NiceToHave.setVisibility(View.INVISIBLE);

        for( SubCategoryType subCategoryTypeItem : subCategoryType) {
            TextView t = new TextView(this);
            t.setText(subCategoryTypeItem.Name);
            t.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            t.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
            t.setLayoutParams(new TableLayout.LayoutParams(PredicateLayout.LayoutParams.WRAP_CONTENT, PredicateLayout.LayoutParams.FILL_PARENT, 1f));
            t.setTextColor(Color.parseColor("#1c1b20"));
            t.setTextSize(15);
            t.setSingleLine(true);
            t.setOnClickListener(OnClickButton(t));
            t.setId(subCategoryTypeItem.ID);
            t.setHint("Type");
            t.setOnClickListener(OnClickButton(t));
            subCategoryLayout.addView(t, new PredicateLayout.LayoutParams(15, 15));

        }
        if(!restriction.isEmpty()) {
            MustBe.setVisibility(View.VISIBLE);
        }

        for (Restriction restrictionItem : restriction) {
            TextView t = new TextView(this);
            t.setText(restrictionItem.Name);
            t.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            t.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
            t.setLayoutParams(new TableLayout.LayoutParams(PredicateLayout.LayoutParams.WRAP_CONTENT, PredicateLayout.LayoutParams.FILL_PARENT, 1f));
            t.setTextColor(Color.parseColor("#1c1b20"));
            t.setTextSize(15);
            t.setSingleLine(true);
            t.setOnClickListener(OnClickButton(t));
            t.setId(restrictionItem.ID);
            t.setHint("Restriction");
            RestrictionLayout.addView(t, new PredicateLayout.LayoutParams(15, 15));
        }

        if(!facility.isEmpty()) {
            NiceToHave.setVisibility(View.VISIBLE);
        }

        for( Facility facilityItem : facility) {
            TextView t = new TextView(this);
            t.setText(facilityItem.Name);
            t.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            t.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
            t.setLayoutParams(new TableLayout.LayoutParams(PredicateLayout.LayoutParams.WRAP_CONTENT, PredicateLayout.LayoutParams.FILL_PARENT, 1f));
            t.setTextColor(Color.parseColor("#1c1b20"));
            t.setTextSize(15);
            t.setSingleLine(true);
            t.setOnClickListener(OnClickButton(t));
            t.setId(facilityItem.ID);
            t.setHint("Facility");
            FacilityLayout.addView(t, new PredicateLayout.LayoutParams(15, 15));
        }


        ImageView approvePoint =(ImageView)findViewById(R.id.approvepoint);
        approvePoint.setVisibility(View.VISIBLE);
        TextView AddTextView =(TextView)findViewById(R.id.AddTextView);
        AddTextView.setVisibility(View.VISIBLE);
        ImageView cancelPoint =(ImageView)findViewById(R.id.cancelPoint);
        cancelPoint.setVisibility(View.VISIBLE);
        TextView CancelTextView =(TextView)findViewById(R.id.CancelTextView);
        CancelTextView.setVisibility(View.VISIBLE);

    }

     private View.OnClickListener OnClickButton(final TextView text)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                int SubItemId = text.getId();
                CharSequence typeOfSubCategory = text.getHint();
                if(!checkIfThisItemIsBeenOnTheList(SubItemId,typeOfSubCategory)){
                    text.setBackground(getResources().getDrawable(R.drawable.rounded_corner_with_color));
                    text.setTextColor(Color.parseColor("#ffffff"));
                    addTypeOfSubItemToList(SubItemId, typeOfSubCategory);
                } else{
                    removeTypeOfSubItemFromList(SubItemId, typeOfSubCategory);
                    text.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
                    text.setTextColor(Color.parseColor("#1c1b20"));
                }
            }
        };
    }

    private void removeTypeOfSubItemFromList(int subItemId, CharSequence typeOfSubCategory) {

        boolean isTheRemoveSucceeded ;

        if(typeOfSubCategory.equals("Restriction")) {
            isTheRemoveSucceeded = restrictionIds.remove(Integer.valueOf(subItemId));
        } else if(typeOfSubCategory.equals("Facility")) {
            isTheRemoveSucceeded = facilityIds.remove(Integer.valueOf(subItemId));
        } else if(typeOfSubCategory.equals("Type")) {
            isTheRemoveSucceeded = subCategoryIds.remove(Integer.valueOf(subItemId));
        }
    }

    private void addTypeOfSubItemToList(int subItemId, CharSequence typeOfSubCategory) {

        if(typeOfSubCategory.equals("Restriction")) {
            restrictionIds.add(subItemId);
        } else if(typeOfSubCategory.equals("Facility")) {
             facilityIds.add(subItemId);
        } else if(typeOfSubCategory.equals("Type")) {
           subCategoryIds.add(subItemId);
        }
    }

    public void cancelMethod(View v) {

        subCategoryType.clear();
        facility.clear();
        restriction.clear();

        subCategoryIds.clear();
        facilityIds.clear();
        restrictionIds.clear();

        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
    }

    public void AddPointMethod(View v) {

        PointToAdd.Types = getSubCategoryFromArrayId("Type");
        PointToAdd.Restrictions =  getSubCategoryFromArrayId("Restrictions");
        PointToAdd.Facilities = getSubCategoryFromArrayId("Facilities");
        SharedPreferences.Editor editor = mSharedPref.edit();
        JSONArray jsonArray = new JSONArray();
        //on the login store the login

        boolean isItTheFirst =  ((MyApplication) getApplication()).getIsThisTheFirstPoint();
        try {
            if(isItTheFirst) {
                ArrayList<Point> listOfPoints = new ArrayList<Point>();
                Point FirstPoint = new Point();
                FirstPoint.Lat = ((MyApplication) getApplication()).getLatitude();
                FirstPoint.Lng = ((MyApplication) getApplication()).getLongitude();
                FirstPoint.Name = ((MyApplication) getApplication()).getAddressName();
                listOfPoints.add(FirstPoint);
                listOfPoints.add(PointToAdd);
                for (int i=0; i < listOfPoints.size(); i++)
                    jsonArray.put(getJSONObject(listOfPoints.get(i)));
                ((MyApplication) getApplication()).setIsThisTheFirstPoint();

            }else {
                String gsonListOfPoints = mSharedPref.getString(kPOINTS, "");
                jsonArray = new JSONArray(gsonListOfPoints);
                JSONObject jsonObject =  getJSONObject(PointToAdd);
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
        e.printStackTrace();
    }
        String list =  jsonArray.toString();
        editor.putString(kPOINTS, list);
        editor.commit();
        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
    }

    private JSONObject getJSONObject(Point point) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Budget", point.Budget);
            obj.put("CityId",point.CityId);
            obj.put("Facilities", point.Facilities);
            obj.put("HouseNumber",point.HouseNumber);
            obj.put("Lat", point.Lat);
            obj.put("LocalName", point.LocalName);
            obj.put("Lng", point.Lng);
            obj.put("Name", point.Name);
            obj.put("NeighborhoodId",point.NeighborhoodId);
            obj.put("PointId",point.PointId);
            obj.put("Rating",point.Rating);
            obj.put("Restrictions",point.Restrictions);
            obj.put("StreetId", point.StreetId);
            obj.put("SubCategories", point.SubCategories);
            obj.put("Types", point.Types);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private String getSubCategoryFromArrayId(String arrayName) {

        StringBuilder Result = new StringBuilder();

        if(arrayName.equals("Type")) {
            for(Integer subCategoryItem : subCategoryIds) {
                Result.append(subCategoryItem.toString()+",");
            }
        }else if(arrayName.equals("Restrictions")){
            for(Integer restrictionItem : restrictionIds) {
                Result.append(restrictionItem.toString()+",");
            }
        }else if (arrayName.equals("Facilities")){
            for(Integer facilityItem : facilityIds) {
                Result.append(facilityItem.toString()+",");
            }
        }
        if(!Result.toString().equals("")) {
            Result.deleteCharAt(Result.length() - 1);
            return Result.toString();
        }
        else return null;
    }

    private boolean checkIfThisItemIsBeenOnTheList(int subItemId, CharSequence typeOfSubCategory) {

        boolean isThisItemHasBeenOnTheList = false;
        if(typeOfSubCategory.equals("Restriction")) {
            if(restrictionIds.contains(subItemId)) {
                isThisItemHasBeenOnTheList = true;
            }
        } else if(typeOfSubCategory.equals("Facility")) {
            if(facilityIds.contains(subItemId)) {
                isThisItemHasBeenOnTheList = true;
            }
        } else if(typeOfSubCategory.equals("Type")) {
                if (subCategoryIds.contains(subItemId)) {
                    isThisItemHasBeenOnTheList = true;
                }
            }
         return isThisItemHasBeenOnTheList;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String ItemPicked = spinner.getSelectedItem().toString();
        //check that the string that select is not the hinted one
        if(!ItemPicked.equals("Select a Type")) {
            List<SubCategory> subCategoryList = ((MyApplication) getApplication()).getsubCategoryList();
            int reasonID = getIdFromString(ItemPicked, subCategoryList);
            PointToAdd.Name = ItemPicked;
            PointToAdd.SubCategories = Integer.toString(reasonID);
            GetAllDetailsSubCategoryAsyncTask getAllDetailsSubCategoryAsyncTask = new GetAllDetailsSubCategoryAsyncTask(reasonID);
            getAllDetailsSubCategoryAsyncTask.execute();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private int getIdFromString(String ItemPicked ,List<SubCategory> subCategoryList) {
        int reasonID = -1;
        for (SubCategory subCategoryItem : subCategoryList){
            if(ItemPicked.equals(subCategoryItem.Name)) {
                reasonID = subCategoryItem.ID;
                break;
            }
        }
        return reasonID;
    }

    private CTwalkService getService() {
        CTwalkService service = new CTwalkService();
        service.setUrl("http://ctwalkapp.com/CTwalkService/Service.svc?wsdl");
        service.setTimeOut(7);
        ((MyApplication)getApplication()).setService(service);
        return service;
    }

    private List<SubCategory> getSubCategory(int id) {

        service = getService();
        int numberOfTries = 0;
        while (numberOfTries < NUMBEROFTRIES){
            try {
                Output SubCategories = service.GetAllSubCategoriesByCategoryId(id, false);
                List<SubCategory> subCategoryArray;
                if (SubCategories.success == false)
                    return null;
                else {
                    String Result = SubCategories.value;
                    subCategoryArray = parserArrayTosubCategory(Result);
                    return subCategoryArray;
                }
            } catch (Exception e) {
                e.printStackTrace();
                numberOfTries++;
            }
    }
        return null;
   }

    private List<String> getAllTheInfoAboutSubCategory(int id) {
        int numberOfTries = 0;
        while (numberOfTries < NUMBEROFTRIES){
            try {
                 Output SubCategoryTypes =  service.GetAllSubCategoryTypesBySubCategoryID(id, true);
                 Output Restrictions =  service.GetAllRestrictionsBySubCategoryId(id, true);
                 Output Facilities = service.GetAllFacilitiesBySubCategoryId(id, true);
                 List<String> result = new ArrayList<String>();
                 result.add(SubCategoryTypes.value);
                 result.add(Restrictions.value);
                 result.add(Facilities.value);
                 return result;
            } catch (Exception e) {
                e.printStackTrace();
                numberOfTries++;
            }
        }
        return null;
    }

    private List<SubCategory> parserArrayTosubCategory(String ArrayOfsubCategory) {

        List<SubCategory> subCategoryArrayList = new ArrayList<SubCategory>();
        try {

            JSONArray jsonarray = new JSONArray(ArrayOfsubCategory);

            for (int i = 0; i < jsonarray.length(); i++) {

                JSONObject jsonobject = jsonarray.getJSONObject(i);
                SubCategory subCategory = new SubCategory();
                subCategory.ID = Integer.parseInt(jsonobject.getString("ID"));
                subCategory.Name = jsonobject.getString("Name");
                subCategory.Icon = jsonobject.getString("Icon");
                subCategoryArrayList.add(subCategory);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return subCategoryArrayList;
    }

    private List<Facility> parserArrayToFacilitiesFromSubCategory(String Facility) {

        List<Facility> FacilityArrayList = new ArrayList<Facility>();
        try {
            Object json = new JSONTokener(Facility).nextValue();

            if(json instanceof JSONObject) {
                JSONObject jsonCollection = new JSONObject(Facility);
                Facility facility = new Facility();
                facility.ID = Integer.parseInt(jsonCollection.getString("ID"));
                facility.Name = jsonCollection.getString("Name");
                FacilityArrayList.add(facility);
            }
            else if (json instanceof JSONArray) {
                JSONArray jsonarray = new JSONArray(Facility);

                for (int i = 0; i < jsonarray.length(); i++) {

                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    Facility facility = new Facility();
                    facility.ID = Integer.parseInt(jsonobject.getString("ID"));
                    facility.Name = jsonobject.getString("Name");
                    FacilityArrayList.add(facility);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FacilityArrayList;
    }

    private List<Restriction> parserArrayToRestrictionsFromSubCategory(String Restriction) {

        List<Restriction> RestrictionArrayList = new ArrayList<Restriction>();
        if(Restriction != null) {
            try {
                Object json = new JSONTokener(Restriction).nextValue();

                if (json instanceof JSONObject) {
                    JSONObject jsonCollection = new JSONObject(Restriction);
                    Restriction restriction = new Restriction();
                    restriction.ID = Integer.parseInt(jsonCollection.getString("ID"));
                    restriction.Name = jsonCollection.getString("Name");
                    RestrictionArrayList.add(restriction);

                } else if (json instanceof JSONArray) {

                    JSONArray jsonCollection = new JSONArray(Restriction);

                    for (int i = 0; i < jsonCollection.length(); i++) {

                        JSONObject jsonobject = jsonCollection.getJSONObject(i);
                        Restriction restriction = new Restriction();
                        restriction.ID = Integer.parseInt(jsonobject.getString("ID"));
                        restriction.Name = jsonobject.getString("Name");
                        RestrictionArrayList.add(restriction);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return RestrictionArrayList;
    }

    private List<SubCategoryType> parserArrayToSubCategoryType(String SubCategoryType) {

        List<SubCategoryType> subCategoryTypeArrayList = new ArrayList<SubCategoryType>();
        if(SubCategoryType!= null) {
            try {
                Object json = new JSONTokener(SubCategoryType).nextValue();
                if (json instanceof JSONObject) {
                    JSONObject jsonCollection = new JSONObject(SubCategoryType);
                    SubCategoryType subCategoryType = new SubCategoryType();
                    subCategoryType.ID = Integer.parseInt(jsonCollection.getString("ID"));
                    subCategoryType.Name = jsonCollection.getString("Name");
                    subCategoryType.Icon = jsonCollection.getString("Icon");
                    subCategoryType.SubCategoryID = Integer.parseInt(jsonCollection.getString("SubCategoryID"));
                    subCategoryTypeArrayList.add(subCategoryType);
                } else if (json instanceof JSONArray) {
                    JSONArray jsonCollection = new JSONArray(SubCategoryType);

                    for (int i = 0; i < jsonCollection.length(); i++) {
                        JSONObject jsonobject = jsonCollection.getJSONObject(i);
                        SubCategoryType subCategoryType = new SubCategoryType();
                        subCategoryType.ID = Integer.parseInt(jsonobject.getString("ID"));
                        subCategoryType.Name = jsonobject.getString("Name");
                        subCategoryType.Icon = jsonobject.getString("Icon");
                        subCategoryType.SubCategoryID = Integer.parseInt(jsonobject.getString("SubCategoryID"));
                        subCategoryTypeArrayList.add(subCategoryType);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return subCategoryTypeArrayList;
    }

    private class AddSubCategoryAsyncTask extends AsyncTask<Void,Void,List<SubCategory> >
    {
        int categoryId;

        public AddSubCategoryAsyncTask() {
            categoryId = ((MyApplication)getApplication()).getCategoryId();
        }

        @Override
         protected List<SubCategory> doInBackground(Void... params) {
            List<SubCategory> subCategory =  getSubCategory(categoryId);
            return subCategory;
        }

        @Override
        protected void onPostExecute(final List<SubCategory> subCategoryList) {
            if(subCategoryList != null) {
                PutOutSpinner(subCategoryList);
                ((MyApplication) getApplication()).setsubCategoryList(subCategoryList);
            }
        }
    }

    private class GetAllDetailsSubCategoryAsyncTask extends AsyncTask<Void,Void,List<String> >
    {
        int SubcategoryId;

        public GetAllDetailsSubCategoryAsyncTask(int SubcategoryId) {
            this.SubcategoryId = SubcategoryId;
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            List<String> DetailsSubCategory =  getAllTheInfoAboutSubCategory(SubcategoryId);
            return DetailsSubCategory;
        }

        @Override
        protected void onPostExecute(final List<String> DetailsSubCategory) {
            if(DetailsSubCategory != null) {
                ShowTheDetails(DetailsSubCategory);
            }
        }
    }

}