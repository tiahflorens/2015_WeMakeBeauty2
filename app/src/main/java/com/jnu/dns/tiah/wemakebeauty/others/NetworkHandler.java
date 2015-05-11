package com.jnu.dns.tiah.wemakebeauty.others;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PeterYoon on 3/24/15.
 */
public class NetworkHandler {

    DefaultHttpClient httpClient;
    HttpEntity httpEntity;
    HttpResponse httpResponse;
    HttpPost httpPost;
    List<NameValuePair> nameValuePairs;
    HttpParams httpParameters;
    //private final String url = "https://168.131.148.50:";
    private final String url = "http://192.168.0.2:5001/Beautalk_Server/index2.jsp";

    public NetworkHandler() {
        httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5500);
        HttpConnectionParams.setSoTimeout(httpParameters, 5500);

        httpClient = new DefaultHttpClient(httpParameters);
        nameValuePairs = new ArrayList<>(2);


    }

    public String sendRequest(String url) {
        Log.d("tiah" , "sendRequest " + url);

        httpPost = new HttpPost(url);
        try {

            httpResponse = httpClient.execute(httpPost); //request함
            httpEntity = httpResponse.getEntity(); //response받음

            return EntityUtils.toString(httpEntity, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String sendRequest(String category, String division, String data) {
        httpPost = new HttpPost(url);
        try {
            httpPost.setHeader("category", category);
            httpPost.setHeader("division", division);//소분류 헤더

            Log.d("tiah", "networkHandler.sendRequest msg: " + data.length() + " , " + data);

            nameValuePairs.clear();
            nameValuePairs.add(new BasicNameValuePair("data", data)); //파라미터 담기
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8")); //인코딩
            httpResponse = httpClient.execute(httpPost); //request함

            httpEntity = httpResponse.getEntity(); //response받음

            String response = EntityUtils.toString(httpEntity, "UTF-8");
            Log.d("tiah", "response : " + response);

            if (response == null) {
                Log.d("tiah", " response is null");
                return null;

            } else if (response.contains("<html>"))

                return null;
            else
                return response;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
