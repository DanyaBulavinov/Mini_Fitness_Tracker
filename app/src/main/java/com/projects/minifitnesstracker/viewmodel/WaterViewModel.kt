package com.projects.minifitnesstracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.projects.minifitnesstracker.model.DateGlassesTuple
import com.projects.minifitnesstracker.model.TrackingData
import com.projects.minifitnesstracker.model.TrackingDataDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class WaterViewModel(application: Application) : AndroidViewModel(application) {
    private val _dateGlassesLiveData = MutableLiveData<List<DateGlassesTuple>>()
    val dateGlassesLiveData: LiveData<List<DateGlassesTuple>>
        get() = _dateGlassesLiveData
    private val _errorGlasses = MutableLiveData<Boolean>()
    val errorGlasses: LiveData<Boolean>
        get() = _errorGlasses
    private val _canGetData = MutableLiveData(false)
    val canGetData: LiveData<Boolean>
        get() = _canGetData
    private val _currentGlasses = MutableLiveData<Int>()
    val currentGlasses: LiveData<Int>
        get() = _currentGlasses

    private val dao by lazy {
        TrackingDataDatabase(getApplication()).TrackingDataDao()
    }

    private val currentDate = Date(System.currentTimeMillis())

    fun getData() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val yearAndMonth = simpleDateFormat.format(currentDate)
        viewModelScope.launch(Dispatchers.IO) {
            _dateGlassesLiveData.postValue(dao.getAllGlassesOfWaterRecords(yearAndMonth))
        }
    }

    fun setCurrentGlasses(glasses: Int) {
        _currentGlasses.value = glasses
    }

    fun setGlasses(inputGlasses: String?, currentTrackingData: TrackingData) {
        val amountOfGlasses = parseGlasses(inputGlasses)
        val fieldValid = validateInput(amountOfGlasses)
        if (fieldValid) {
            _currentGlasses.value = amountOfGlasses
            currentTrackingData.glassesOfWater = amountOfGlasses
            viewModelScope.launch(Dispatchers.IO) {
                dao.update(currentTrackingData)
                _canGetData.postValue(!_canGetData.value!!)
            }
        }
    }


    private fun parseGlasses(inputGlasses: String?): Int {
        return try {
            inputGlasses?.trim()?.toInt() ?: 0
        } catch (e: java.lang.Exception) {
            0
        }
    }

    private fun validateInput(glasses: Int): Boolean {
        var result = true
        if (glasses <= 0) {
            _errorGlasses.value = true
            result = false
        }
        return result
    }

    fun resetErrorGlasses() {
        _errorGlasses.value = false
    }
}