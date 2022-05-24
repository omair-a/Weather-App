package android.cs453.weather;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private Context context;
    private ArrayList<Weather> weatherArrayList;

    public WeatherAdapter() {
        weatherArrayList = new ArrayList<>();
    }

    public WeatherAdapter(Context context, ArrayList<Weather> weatherArrayList) {
        this.context = context;
        this.weatherArrayList = weatherArrayList;
        notifyDataSetChanged();
    }

    public void setWeatherList(ArrayList<Weather> weatherList) {
        this.weatherArrayList = weatherList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_rv_item, parent, false);
        return new WeatherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        Weather weather = weatherArrayList.get(position);
        //holder.temperatureTextView.setText(weather.getTemperature() + "°F");
        holder.temperatureTextView.setText(weather.temperature + "°F");
        Picasso.get().load("http:".concat(weather.getIcon())).into(holder.weatherImageView);
        //holder.windTextView.setText(weather.getWindSpeed() + " km/h");
        holder.windTextView.setText(weather.windSpeed + " km/h");

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(weather.getTime());
            holder.timeTextView.setText(output.format(t));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherArrayList.size();
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView windTextView, temperatureTextView, timeTextView;
        ImageView weatherImageView;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);

            windTextView = (TextView)itemView.findViewById(R.id.card_wind_speed_tv);
            temperatureTextView = (TextView)itemView.findViewById(R.id.card_temperature_tv);
            timeTextView = (TextView)itemView.findViewById(R.id.card_time_tv);
            weatherImageView = (ImageView)itemView.findViewById(R.id.card_weather_image);
        }
    }
}
