package com.dicoding.utsschoolapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.utsschoolapp.data.response.DataSekolahItem
import com.dicoding.utsschoolapp.data.response.SchoolResponse
import com.dicoding.utsschoolapp.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val _listSchool = MutableLiveData<List<DataSekolahItem>>()
    val listSchool: LiveData<List<DataSekolahItem>> = _listSchool

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        fetchDataFromApi("")
    }

    fun fetchDataFromApi(sekolah: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().searchSchools("100", sekolah)
        client.enqueue(object : Callback<SchoolResponse> {
            override fun onResponse(call: Call<SchoolResponse>, response: Response<SchoolResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listSchool.value = response.body()?.dataSekolah
                } else {
                    val statusCode = response.code()
                    handleHttpError(statusCode, response.message())
                    Log.e("MainViewModel", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<SchoolResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message ?: "Unknown error"
                Log.e("MainViewModel", "onFailure: ${t.message}")
            }
        })
    }

    private fun handleHttpError(statusCode: Int, errorMessage: String) {
        when (statusCode) {
            401 -> _errorMessage.value = "$statusCode : Bad Request"
            403 -> _errorMessage.value = "$statusCode : Forbidden"
            404 -> _errorMessage.value = "$statusCode : Not Found"
            else -> _errorMessage.value = "$statusCode : $errorMessage"
        }
    }
}