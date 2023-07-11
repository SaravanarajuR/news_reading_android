package com.example.newsreading;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class GetCountry {
    public static String getCountry(Context context, double latitude, double longitude) {
        String countryName ="";
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                countryName=String.valueOf(address.getCountryCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return countryName;
    }
}
