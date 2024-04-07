package com.example.weatherapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.Adapter.CityAdapter
import com.example.weatherapp.ApiInterface
import com.example.weatherapp.WeatherApp
import com.example.weatherapp.databinding.ActivityCityListBinding
import com.example.weatherapp.model.CityResponseApi
import com.example.weatherapp.model.ForecastResponseApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CityListActivity : AppCompatActivity() {
    lateinit var binding: ActivityCityListBinding
    private val cityAdapter by lazy { CityAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            cityEdit.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    progressBar.visibility= View.VISIBLE
                    val retrofit = Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl("https://api.openweathermap.org")
                        .build().create(ApiInterface::class.java)

                    val cityResponse = retrofit.getCitiesList(p0.toString(),5,"fd71a799ef6a73ef5857f699acef6f2b")

                    cityResponse.enqueue(object :Callback<CityResponseApi>{
                        override fun onResponse(
                            call: Call<CityResponseApi>,
                            response: Response<CityResponseApi>
                        ) {
                            if (response.isSuccessful){
                                val data=response.body()
                                data?.let {
                                    progressBar.visibility=View.GONE
                                    cityAdapter.differ.submitList(it)
                                    cityView.apply {
                                        layoutManager=LinearLayoutManager(this@CityListActivity,LinearLayoutManager.HORIZONTAL,false)
                                        adapter=cityAdapter
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<CityResponseApi>, t: Throwable) {
                            Toast.makeText(this@CityListActivity, "Failed to fetch city data: ${t.message}", Toast.LENGTH_SHORT).show()
                        }

                    })
                }

            })
        }

    }


}