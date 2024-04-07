package com.example.weatherapp.Activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.Adapter.ForecastAdapter
import com.example.weatherapp.ApiInterface
import com.example.weatherapp.R
import com.example.weatherapp.WeatherApp
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.ForecastResponseApi
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

//fd71a799ef6a73ef5857f699acef6f2b
class MainActivity : AppCompatActivity() {

    lateinit var blurView: BlurView
    lateinit var binding: ActivityMainBinding
    private val forecastAdapter by lazy { ForecastAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        blurView = findViewById(R.id.blurView)
        var cityName = intent.getStringExtra("name")
        var cityLat = intent.getDoubleExtra("lat", 0.0)
        var cityLon = intent.getDoubleExtra("lon", 0.0)

        if (cityLat==0.0){
            cityLat=28.7
            cityLon=-77.1
            cityName="Delhi"
        }
        // Fetch weather data for the selected city
        fetchWeatherData(cityName ?: "", cityLat, cityLon)


//        searchCity()

        binding.apply {
            var radius = 10f
            val decorView = window.decorView
            val rootView = decorView.findViewById(android.R.id.content) as ViewGroup
            val windowBackground = decorView.background

            rootView?.let {
                blurView.setupWith(
                    rootView,
                    if (Build.VERSION.SDK_INT >= 31) RenderEffectBlur() else RenderScriptBlur(this@MainActivity)
                )
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)

                blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
                blurView.clipToOutline = true
            }


            addCity.setOnClickListener {
                startActivity(Intent(this@MainActivity, CityListActivity::class.java))
            }
        }

    }


    private fun fetchWeatherData(cityName: String, cityLat: Double, cityLon: Double) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val weatherResponse = retrofit.getWeatherData(cityLat,cityLon,cityName,"fd71a799ef6a73ef5857f699acef6f2b","metric")
        val forecastResponse = retrofit.getForecast(cityLat,cityLon,cityName,"fd71a799ef6a73ef5857f699acef6f2b","metric")

        weatherResponse.enqueue(object: Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                binding.progressBar.visibility= View.VISIBLE
                val responseBody = response.body()
                if(response.isSuccessful && responseBody!=null){
                    val temperature = responseBody.main.temp
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val min = responseBody.main.temp_min
                    val max = responseBody.main.temp_max
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure

                    binding.temperature.text = "$temperature°C"
                    binding.humidity.text = "$humidity%"
                    binding.windspeed.text = "$windSpeed m/s"
                    binding.weather.text = condition
                    binding.minTemp.text = "Min:$min°C"
                    binding.maxTemp.text = "Max:$max°C"
                    binding.condition.text = condition
                    binding.sunrise.text = "${time(sunrise)}"
                    binding.sunset.text = "${time(sunset)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.day.text = dayName(System.currentTimeMillis())
                        binding.date.text = date()
                        binding.cityName.text = "$cityName"

                    changeImagesAccToCondition(condition)

                }
                binding.progressBar.visibility = View.GONE

            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")

            }


        })

        //Forecast 3 hour 5 day
        forecastResponse.enqueue(object: Callback<ForecastResponseApi>{
            override fun onResponse(
                call: Call<ForecastResponseApi>,
                response: Response<ForecastResponseApi>
            ) {
                if(response.isSuccessful){
                    val data = response.body()
                    blurView.visibility= View.VISIBLE

                    data?.let {
                        forecastAdapter.differ.submitList(it.list)
                        binding.forecastView.apply {
                            layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
                            adapter = forecastAdapter
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ForecastResponseApi>, t: Throwable) {

            }

        })

        }

    private fun changeImagesAccToCondition(conditions: String) {
        when(conditions){

            "Clear Sky", "Sunny", "Clear" ->{
                binding.constraintLayout2.setBackgroundResource(R.color.yellow)
                binding.lottie.setAnimation(R.raw.sunny)
            }

            "Haze", "Mist", "Foggy", "Overcast", "Clouds", "Partly Clouds" ->{
                binding.constraintLayout2.setBackgroundResource(R.drawable.haze)
                binding.lottie.setAnimation(R.raw.haze)
            }

            "Rain","Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain"->{
                binding.constraintLayout2.setBackgroundResource(R.drawable.rain)
                binding.lottie.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" ->{
                binding.constraintLayout2.setBackgroundResource(R.drawable.snow)
                binding.lottie.setAnimation(R.raw.snow)
            }
        }

        binding.lottie.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timeStamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timeStamp*1000)))
    }

    fun dayName(timeStamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }


}

