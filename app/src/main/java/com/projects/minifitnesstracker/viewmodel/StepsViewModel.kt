package com.projects.minifitnesstracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.projects.minifitnesstracker.model.DateStepsTuple
import com.projects.minifitnesstracker.model.TrackingData
import com.projects.minifitnesstracker.model.TrackingDataDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class StepsViewModel(application: Application) : AndroidViewModel(application) {
    private val _dateStepsLiveData = MutableLiveData<List<DateStepsTuple>>()
    val dateStepsLiveData: LiveData<List<DateStepsTuple>>
        get() = _dateStepsLiveData
    private val _errorSteps = MutableLiveData<Boolean>()
    val errorSteps: LiveData<Boolean>
        get() = _errorSteps
    private val _canGetData = MutableLiveData(false)
    val canGetData: LiveData<Boolean>
        get() = _canGetData
    private val _currentSteps = MutableLiveData<Int>()
    val currentSteps: LiveData<Int>
        get() = _currentSteps

    private val dao by lazy {
        TrackingDataDatabase(getApplication()).TrackingDataDao()
    }

    private val currentDate = Date(System.currentTimeMillis())

    fun getData() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val yearAndMonth = simpleDateFormat.format(currentDate)
        viewModelScope.launch(Dispatchers.IO) {
            _dateStepsLiveData.postValue(dao.getAllStepRecordsPerMonth(yearAndMonth))
        }
    }

    fun setCurrentSteps(steps: Int) {
        _currentSteps.value = steps
    }

    fun setSteps(inputSteps: String?, currentTrackingData: TrackingData) {
        val amountOfSteps = parseSteps(inputSteps)
        val fieldValid = validateInput(amountOfSteps)
        if (fieldValid) {
            _currentSteps.value = amountOfSteps
            currentTrackingData.amountOfSteps = amountOfSteps
            viewModelScope.launch(Dispatchers.IO) {
                dao.update(currentTrackingData)
                _canGetData.postValue(!_canGetData.value!!)
            }
        }
    }



    private fun parseSteps(inputSteps: String?): Int {
        return try {
            inputSteps?.trim()?.toInt() ?: 0
        } catch (e: java.lang.Exception) {
            0
        }
    }

    private fun validateInput(steps: Int): Boolean {
        var result = true
        if (steps <= 0) {
            _errorSteps.value = true
            result = false
        }
        return result
    }

    fun resetErrorSteps() {
        _errorSteps.value = false
    }
}










