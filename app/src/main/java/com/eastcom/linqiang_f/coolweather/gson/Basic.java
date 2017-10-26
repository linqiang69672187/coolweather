package com.eastcom.linqiang_f.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by linqiang_f on 2017/10/26.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }

}
