package com.ctwalkapp.ctwalk.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodingLocation {

    private static final String TAG = "GeocodingLocation";
    private static int NUMBEROFTRIES = 5;
    public static void getLocationFromAddress(final String locationAddress,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                 Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                Address address = null;
                Double Latitude, Longitude;
                Latitude = Longitude = 0.0;
                boolean ResultOfWarpper = false;
                int numberOfTries = 0;
                while (!ResultOfWarpper && numberOfTries < NUMBEROFTRIES) {
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(locationAddress, 1);
                        if (addressList != null && addressList.size() > 0) {
                            address = addressList.get(0);
                            Latitude = address.getLatitude();
                            Longitude = address.getLongitude();
                            ResultOfWarpper = true;
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Unable to connect to Geocoder", e);
                        numberOfTries++;
                        e.printStackTrace();
                    } finally {
                        Message message = Message.obtain();
                        message.setTarget(handler);
                        if (address != null) {
                            message.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putDouble("Latitude", Latitude);
                            bundle.putDouble("Longitude", Longitude);
                            bundle.putString("Address",address.getAddressLine(0).toString());
                            message.setData(bundle);
                        } else {
                            message.what = 2;
                            Bundle bundle = new Bundle();
                            String result = "Address: " + locationAddress +
                                    "\n Unable to get Latitude and Longitude for this address location.";
                            bundle.putString("address", result);
                            message.setData(bundle);
                        }
                        message.sendToTarget();
                    }
                }
                if(numberOfTries == NUMBEROFTRIES) {}
            }
        };
        thread.start();
    }

    public static void getAddressFromLocation(final Double latitude, final Double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder;
                List<Address> addresses;
                String address = null;
                boolean ResultOfWarpper = false;
                int numberOfTries = 0;
                while (!ResultOfWarpper && numberOfTries < NUMBEROFTRIES) {
                    try {
                        geocoder = new Geocoder(context, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        address = addresses.get(0).getAddressLine(0);
                        ResultOfWarpper = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        numberOfTries++;
                    } finally {
                        Message message = Message.obtain();
                        message.setTarget(handler);
                        if (address != null) {
                            message.what = 3;
                            Bundle bundle = new Bundle();
                            bundle.putString("address", address);
                            message.setData(bundle);
                        } else {
                            message.what = 4;
                            Bundle bundle = new Bundle();
                            address = "Unable to get address location";
                            bundle.putString("address", address);
                            message.setData(bundle);
                        }
                        message.sendToTarget();
                    }

                }
                if(numberOfTries == NUMBEROFTRIES) {}
            }
        };
            thread.start();
        }
    }

