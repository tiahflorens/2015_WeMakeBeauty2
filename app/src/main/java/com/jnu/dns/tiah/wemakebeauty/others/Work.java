package com.jnu.dns.tiah.wemakebeauty.others;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.jnu.dns.tiah.wemakebeauty.items.EventItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Peter on 2015-03-31.
 */
public class Work {

    private int screenWidth;

    public Work(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public Work(){

    }

    final String base = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
    final String sensor = "&sensor=true";
    final String key = "&key=AIzaSyAZfexaq3HT5IIXHJHrWjsCRv-q-c6xA3A";

    public String getURL(String text, double lat, double lng, int rad) {

        final String query = "query=" + text;
        final String location = "&location=" + lat + "," + lng;
        final String raidus = "&radius=" + rad;
        return base + query + sensor + location + raidus + key;
    }


    public double getDistDiff(LatLng pos1, LatLng pos2) {

        double theta, dist;


        theta = pos1.longitude - pos2.longitude;

        dist = Math.sin(deg2rad(pos1.latitude)) * Math.sin(deg2rad(pos2.latitude))
                + Math.cos(deg2rad(pos1.latitude)) * Math.cos(deg2rad(pos2.latitude))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515; // statute miles. ?⑥쐞??湲곕낯 留덉씪.
        dist = dist * 1.609344;

// return meter

        return dist * 1000;
    }

    private double deg2rad(double deg) {
        return (double) (deg * Math.PI / (double) 180d);
    }

    private double rad2deg(double rad) {
        return (double) (rad * (double) 180d / Math.PI);
    }

    public double getMeterPerPixelByZoomLevel(float zoom) {
        switch ((int) zoom) {

            case 10:
                return 95;
            case 11:
                return 48;
            case 12:
                return 24;
            case 13:
                return 12;
            case 14:
                return 6;
            case 15:
                return 3;
            case 16:
                return 1.48;
            case 17:
                return 0.74;
            case 18:
                return 0.37;
            case 19:
                return 0.19;
            default:
                return 0;

        }
    }


    public ArrayList<EventItem> parsing(String raw, String name, String due, String memo) {
        Log.d("tiah", "work.parsing");
        ArrayList<EventItem> list = new ArrayList<>();
        try {
            JSONObject src = new JSONObject(raw);

            if (src.getString("status").equalsIgnoreCase("ok")) {
                JSONArray arr = src.getJSONArray("results");

                for (int i = 0; i < arr.length(); i++) {

                    JSONObject item = arr.getJSONObject(i);
                    String lat = item.getJSONObject("geometry").getJSONObject("location").getString("lat");
                    String lng = item.getJSONObject("geometry").getJSONObject("location").getString("lng");

                    list.add(new EventItem(name, memo, due, lat, lng));
                    Log.d("tiah" ,"add");

                }
            }else
                Log.d("tiah","statuc not ok");

        } catch (JSONException e) {
            e.printStackTrace();

        }
        return list;

    }

    public String getDateDifference(long due){

        long diff = due - System.currentTimeMillis();

        diff /= 1000;
        diff/= (3600*24);

        return (int)diff +"일 남았어요!";
    }

    public int getSearchRadius(double meter) {


        return (int) (screenWidth / 2 * (0.8) * meter);

      /*  switch ((int) zoom) {
            case 15:
                return 600;
            case 16:
                return 480;
            case 17:
                return 250;
            case 18:
                return 130;
            case 19:
                return 70;
            default:
                return 0;
        }
        */
    }


    public void decoding(int[] arr, int result) {
        switch (result) {
            case 0:
                arr[0] = 0;
                arr[1] = 0;
                break;
            case 1:
                arr[0] = 0;
                arr[1] = 1;
                break;
            case 2:
                arr[0] = 1;
                arr[1] = 0;
                break;
            case 3:
                arr[0] = 1;
                arr[1] = 1;
                break;
        }
    }

    public String getShapeInText(int val){
        if(val==0)
            return "둥근형";
        else
            return "갸름한형";
    }

    public String getTypeInText(int val){
        if(val==0)
            return "건성피부";
        else
            return "지성피부";

    }
    public String getToneInText(int val){
        if(val == 0 )
            return "차가운피부";
        else
            return "따뜻한피부";
    }


}
