package com.eastcom.linqiang_f.coolweather.util;

import android.text.TextUtils;

import com.eastcom.linqiang_f.coolweather.db.City;
import com.eastcom.linqiang_f.coolweather.db.County;
import com.eastcom.linqiang_f.coolweather.db.Province;
import com.google.gson.JsonIOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by linqiang_f on 2017/10/25.
 */

public class Utility {
    /**
     *  解析和处理服务器返回的省级数据1
     */

    public static boolean handleProvinceResponse(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvinces = new JSONArray(response);
                for (int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return  true;
            }
            catch (JsonIOException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response,int provinceId) throws JSONException {
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCites = new JSONArray(response);
                for (int i=0;i<allCites.length();i++){
                    JSONObject cityObject = allCites.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }
            catch (JsonIOException e){
                e.printStackTrace();
            }
        }

        return false;
    }


    public static boolean handleCountyResponse(String response,int cityId) throws JSONException {
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i=0;i<allCounties.length();i++){
                    JSONObject countObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countObject.getString("name"));
                    county.setWeatherId(countObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }
            catch (JsonIOException e){
                e.printStackTrace();
            }
        }

        return false;
    }



}
