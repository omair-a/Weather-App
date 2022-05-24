package android.cs453.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;

import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeLayout;
    private ProgressBar loadingProgressBar;
    private TextView cityTextView, temperatureTextView, weatherTextView;
    private RecyclerView weatherRecyclerView;
    private TextInputEditText cityEditText;
    private ImageView backgroundImageView, searchImageView, iconImageView;
    private ArrayList<Weather> weatherList;
    private WeatherAdapter adapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        homeLayout = findViewById(R.id.home_relative_layout);
        loadingProgressBar = findViewById(R.id.loading_progress_bar);
        cityTextView = findViewById(R.id.city_text_view);
        temperatureTextView = findViewById(R.id.temperature_text_view);
        weatherTextView = findViewById(R.id.weather_text_view);
        weatherRecyclerView = findViewById(R.id.weather_recycler_view);
        cityEditText = findViewById(R.id.city_edit_text);
        backgroundImageView = findViewById(R.id.background_image_view);
        searchImageView = findViewById(R.id.search_image_view);
        iconImageView = findViewById(R.id.weather_image_view);

        Picasso.get().load("https://images.unsplash.com/photo-1532178910-7815d6919875?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8Y2xvdWR5JTIwc2t5fGVufDB8fDB8fA%3D%3D&w=1000&q=80").into(backgroundImageView);

        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        adapter = new WeatherAdapter();
        weatherRecyclerView.setAdapter(adapter);
        weatherRecyclerView.setLayoutManager(layoutManager);

        loadingProgressBar.setVisibility(View.GONE);
        homeLayout.setVisibility(View.VISIBLE);

        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEditText.getText().toString();
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                }
                else {
                    cityEditText.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });
    }

    private void getWeatherInfo(String cityName) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=dca892fc2d534382a1461857211207&q=" + cityName + "&days=1&aqi=yes&alerts=yes";
        cityTextView.setText(cityName);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingProgressBar.setVisibility(View.GONE);
                homeLayout.setVisibility(View.VISIBLE);
                weatherList = new ArrayList<>();

                try {
                    Weather weather = new Weather();
                    String temperature = response.getJSONObject("current").getString("temp_f");
                    temperatureTextView.setText(temperature + "°F");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconImageView);
                    weatherTextView.setText(condition);;

                    if (isDay == 1) {
                        //daytime background
                        Picasso.get().load("https://thumbs.dreamstime.com/b/dramatic-morning-sky-sunrise-panorama-over-mountains-clouds-burning-sun-sunset-fire-109064989.jpg").into(backgroundImageView);
                    }
                    else {
                        //nighttime background
                        Picasso.get().load("https://www.thetimes.co.uk/imageserver/image/%2Fmethode%2Ftimes%2Fprod%2Fweb%2Fbin%2Fdc017522-71eb-11ea-a7b2-0673a3ece2ba.jpg?crop=2687%2C3359%2C1176%2C0").into(backgroundImageView);
                    }


                    JSONObject forecastObject = response.getJSONObject("forecast");
                    JSONObject forecastDayObject = forecastObject.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastDayObject.getJSONArray("hour");

                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourObject = hourArray.getJSONObject(i);
                        String time = hourObject.getString("time");
                        String temper = hourObject.getString("temp_f");
                        String img = hourObject.getJSONObject("condition").getString("icon");
                        String wind = hourObject.getString("wind_mph");

                        weatherList.add(new Weather(time, temper, img, wind));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.setWeatherList(weatherList);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setMessage("Please enter a valid city name.");
                dialog.setTitle("Error");
                dialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                weatherList.clear();
                                adapter.notifyDataSetChanged();
                                temperatureTextView.setText("0");
                            }
                        });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
