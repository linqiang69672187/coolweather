package com.eastcom.linqiang_f.coolweather;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.eastcom.linqiang_f.coolweather.gson.Forecast;
import com.eastcom.linqiang_f.coolweather.gson.Weather;
import com.eastcom.linqiang_f.coolweather.util.HttpUtil;
import com.eastcom.linqiang_f.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private  TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity =(TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        forecastLayout =(LinearLayout)findViewById(R.id.forecast_layout);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);//有缓存直接解析使用缓存数据
        if (weatherString!=null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }
        else {
            String weatherID = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeaher(weatherID);
        }
    }

    /**
     * 有缓存直接解析使用缓存数据
     * @param weatherID
     */
    private void requestWeaher(String weatherID) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherID+"&key=5f87ca742088431e8027e65cab3e6ff4";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
             e.printStackTrace();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_LONG).show();
                 }
             });


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
              final String responseText = response.body().string();
              final Weather weather = Utility.handleWeatherResponse(responseText);
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      if (weather !=null&&"ok".equals(weather.status)){
                         SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                         editor.putString("weather",responseText);
                         editor.apply();
                          showWeatherInfo(weather);
                      }else {
                          Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_LONG).show();
                      }
                  }
              });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split("")[1];
        String degree = weather.now.temperature +"℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast:weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText =(TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
       if (weather.aqi !=null){
            aqiText.setText(weather.aqi.city.api);
            pm25Text.setText(weather.aqi.city.pm25);
       }
       String comfort ="舒适度："+weather.suggestion.comfort.info;
       String carWash = "洗车指数："+weather.suggestion.carWash.info;
       String sport = "运动建议："+weather.suggestion.sport.info;
       comfortText.setText(comfort);
       carWashText.setText(carWash);
       sportText.setText(sport);
       weatherLayout.setVisibility(View.VISIBLE);
    }
}
