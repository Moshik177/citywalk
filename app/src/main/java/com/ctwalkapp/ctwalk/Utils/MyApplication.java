package com.ctwalkapp.ctwalk.Utils;

import android.app.Application;
import android.widget.Toast;

import com.Wsdl2Code.WebServices.CTwalkService.CTwalkService;
import com.Wsdl2Code.WebServices.CTwalkService.City;
import com.Wsdl2Code.WebServices.CTwalkService.Complex_Point;
import com.Wsdl2Code.WebServices.CTwalkService.Complex_Route;
import com.Wsdl2Code.WebServices.CTwalkService.Point;
import com.Wsdl2Code.WebServices.CTwalkService.Route;
import com.Wsdl2Code.WebServices.CTwalkService.SubCategory;
import com.Wsdl2Code.WebServices.CTwalkService.User;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Moshik on 14/08/2015.
 */
public class MyApplication extends Application {

    private String selectingCityName;
    private CTwalkService service;
    private User user;
    private int userId;
    private String userName;
    private String addressName;
    private String typeOfAttribute;
    private int categoryId;
    private List<SubCategory> subCategoryList;
    private boolean isThisTheFirstPoint = true;
    private Double Latitude;
    private Double Longitude;
    private int TheNumberOfThePage = 1;
    private String personPhotoUrl;
    private static boolean activityVisible;
    private Complex_Route selectedRoute = new Complex_Route();
    private Complex_Route preSaveRoute = new Complex_Route();
    private List<Complex_Route> userListRoutes = new ArrayList<>();
    private static List<Complex_Point> listPoints = new ArrayList<>();
    private static List<Complex_Point> preSavelistPoints = new ArrayList<>();
    private String emailAddress;
    private String password;
    private String firstName;
    private String lastName;
    private boolean isItNewRoute;

    public Complex_Route getPreSaveRoute() {
        return preSaveRoute;
    }

    public void setPreSaveRoute(Complex_Route preSaveRoute) {
        this.preSaveRoute = preSaveRoute;
    }

    public void setIsItNewRoute() {isItNewRoute = true;}

	public void setIsItNewRouteFalse() {isItNewRoute = false;}

    public boolean getIsItNewRoute() {return isItNewRoute;}

    public void updateNewRoute() {isItNewRoute = false;}

    public String getSelectingCityName() {return selectingCityName;}

    public void setSelectingCityName(String selectingCityName) {this.selectingCityName = selectingCityName;}

    public String getFirstName() {return firstName;}

    public void setLastName(String lastName) {this.lastName = lastName;}

    public String getLastName() {return lastName;}

    public void setFirstName(String firstName) {this.firstName  = firstName;}

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {this.emailAddress = emailAddress;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password  = password;}

    public CTwalkService getService()
    {
        return service;
    }

    public void setService(CTwalkService service)
    {
        this.service = service;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUserId() {return userId;}

    public void setUserId(int userId) {this.userId = userId;}

    public String getUserName() {return userName;}

    public void setUserName(String userName) { this.userName = userName;}

    public String getAddressName() {return addressName;}

    public void setAddressName(String addressName) { this.addressName = addressName;}

    public void setChoosingTypeOfAttribute(String TypeOfAttribute) {this.typeOfAttribute = TypeOfAttribute;}

    public String getChoosingTypeOfAttribute() { return typeOfAttribute;}

    public void setLatitude (Double Latitude) {this.Latitude =Latitude;}

    public Double getLatitude() { return Latitude;}

    public void setLongitude (Double Longitude) {this.Longitude =Longitude;}

    public Double getLongitude() { return Longitude;}

    public void setCategoryId (int CategoryId) {this.categoryId =CategoryId;}

    public int getCategoryId() { return categoryId;}

    public void setsubCategoryList (List<SubCategory> subCategoryList) {this.subCategoryList = subCategoryList;}

    public List<SubCategory> getsubCategoryList() { return subCategoryList;}

    public boolean getIsThisTheFirstPoint() {return isThisTheFirstPoint;}

    public void setIsThisTheFirstPoint() {this.isThisTheFirstPoint = false;}

    public int getTheNumberOfThePage() {return TheNumberOfThePage;}

    public void UpdateIsThisTheFirstPoint() {this.TheNumberOfThePage++;}

    public void setTheNumberOfThePageToTheCurrPoint(int TheNumberOfThePage) {this.TheNumberOfThePage = TheNumberOfThePage;}

    public static List<Complex_Point> getListPoints() {
        return listPoints;
    }

    public void setListPoints(List<Complex_Point> listPoints){ this.listPoints = listPoints;}

    public static List<Complex_Point> getPreSavelistPoints() { return preSavelistPoints; }

    public void setPreSavelistPoints(List<Complex_Point> preSavelistPoints) {
        MyApplication.preSavelistPoints = preSavelistPoints;
    }

    public List<Complex_Route> getUserListRoutes() {
        return userListRoutes;
    }

    public void setUserListRoutes(List<Complex_Route> userListRoutes) {this.userListRoutes = userListRoutes;}

    public String getPersonPhotoUrl() {
        return personPhotoUrl;
    }

    public void setPersonPhotoUrl(String personPhotoUrl) {
        this.personPhotoUrl = personPhotoUrl;
    }

    public Complex_Route getSelectedRoute() {
        if (selectedRoute== null) {
            Toast.makeText(getApplicationContext(), "Selected Route is empty, will be send back to city page", Toast.LENGTH_SHORT).show();
        }
        return selectedRoute;
    }

    public void setSelectedRoute(Complex_Route selectedRoute) {this.selectedRoute = selectedRoute;}

    public static void activityResumed() {activityVisible = true;}

    public static void activityPaused() {
        activityVisible = false;
    }

}