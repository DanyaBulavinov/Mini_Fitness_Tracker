package com.projects.minifitnesstracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.projects.minifitnesstracker.model.DateSleepTuple
import com.projects.minifitnesstracker.model.TrackingData
import com.projects.minifitnesstracker.model.TrackingDataDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.time.days

class SleepViewModel(application: Application) : AndroidViewModel(application) {
    private val _dateSleepLiveData = MutableLiveData<List<DateSleepTuple>>()
    val dateSleepLiveData: LiveData<List<DateSleepTuple>>
        get() = _dateSleepLiveData
    private val _errorStartHours = MutableLiveData<Boolean>()
    val errorStartHours: LiveData<Boolean>
        get() = _errorStartHours
    private val _errorStartMinutes = MutableLiveData<Boolean>()
    val errorStartMinutes: LiveData<Boolean>
        get() = _errorStartMinutes
    private val _errorEndHours = MutableLiveData<Boolean>()
    val errorEndHours: LiveData<Boolean>
        get() = _errorEndHours
    private val _errorEndMinutes = MutableLiveData<Boolean>()
    val errorEndMinutes: LiveData<Boolean>
        get() = _errorEndMinutes
    private val _canGetData = MutableLiveData(false)
    val canGetData: LiveData<Boolean>
        get() = _canGetData
    private val _currentSleep = MutableLiveData<String>()
    val currentSleep: LiveData<String>
        get() = _currentSleep

    private val dao by lazy {
        TrackingDataDatabase(getApplication()).TrackingDataDao()
    }

    private val currentDate = Date(System.currentTimeMillis())

    fun getData() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val yearAndMonth = simpleDateFormat.format(currentDate)
        viewModelScope.launch(Dispatchers.IO) {
            _dateSleepLiveData.postValue(dao.getAllSleepRecordsPerMonth(yearAndMonth))
        }
    }

    fun setCurrentSleep(amountOfSleep: Int) {
        _currentSleep.value = convertMinutesToStringTime(amountOfSleep)
    }

    fun setSleep(
        inputStartHours: String?,
        inputStartMinutes: String?,
        inputEndHours: String?,
        inputEndMinutes: String?,
        currentTrackingData: TrackingData
    ) {
        val startHours = parseStartHours(inputStartHours)
        val startMinutes = parseStartMinutes(inputStartMinutes)
        val endHours = parseEndHours(inputEndHours)
        val endMinutes = parseEndMinutes(inputEndMinutes)
        val fieldValid = validateInput(startHours, startMinutes, endHours, endMinutes)
        if (fieldValid) {
            val amountOfSleep =
                calculateAmountOfSleep(startHours, startMinutes, endHours, endMinutes)
            _currentSleep.value = convertMinutesToStringTime(amountOfSleep)
            currentTrackingData.amountOfSleep = amountOfSleep
            viewModelScope.launch(Dispatchers.IO) {
                dao.update(currentTrackingData)
                _canGetData.postValue(!_canGetData.value!!)
            }
        }
    }

    private fun convertMinutesToStringTime(amountOfSleep: Int): String {
        val hours = amountOfSleep / 60
        val minutes = amountOfSleep % 60
        return "$hours : $minutes"
    }

    private fun calculateAmountOfSleep(
        startHours: Int,
        startMinutes: Int,
        endHours: Int,
        endMinutes: Int
    ): Int {
        var duration: Duration? = null
        if (startHours < endHours) {
            val beginningOfSleep = LocalTime.of(startHours, startMinutes)
            val endingOfSleep = LocalTime.of(endHours, endMinutes)
            duration = Duration.between(beginningOfSleep, endingOfSleep)
        }
        if (startHours > endHours){
            val beginningOfSleepTime = LocalTime.of(startHours, startMinutes)
            val endingOfSleepTime = LocalTime.of(endHours, endMinutes)
            val beginningOfSleepDate = LocalDate.of(currentDate.year, currentDate.month ,currentDate.day)
            val endingOfSleepDate = LocalDate.of(currentDate.year, currentDate.month ,currentDate.day + 1)
            val beginningOfSleepDateTime = LocalDateTime.of(beginningOfSleepDate, beginningOfSleepTime)
            val endingOfSleepDateTime = LocalDateTime.of(endingOfSleepDate, endingOfSleepTime)
            duration = Duration.between(beginningOfSleepDateTime, endingOfSleepDateTime)
        }
        return duration?.toMinutes()?.toInt()!!
    }

    private fun parseStartHours(inputStartHours: String?): Int {
        return try {
            inputStartHours?.trim()?.toInt() ?: 0
        } catch (e: java.lang.Exception) {
            0
        }
    }

    private fun parseStartMinutes(inputStartMinutes: String?): Int {
        return try {
            inputStartMinutes?.trim()?.toInt() ?: 0
        } catch (e: java.lang.Exception) {
            0
        }
    }

    private fun parseEndHours(inputEndHours: String?): Int {
        return try {
            inputEndHours?.trim()?.toInt() ?: 0
        } catch (e: java.lang.Exception) {
            0
        }
    }

    private fun parseEndMinutes(inputEndMinutes: String?): Int {
        return try {
            inputEndMinutes?.trim()?.toInt() ?: 0
        } catch (e: java.lang.Exception) {
            0
        }
    }

    private fun validateInput(
        startHours: Int,
        startMinutes: Int,
        endHours: Int,
        endMinutes: Int
    ): Boolean {
        var result = true
        if (startHours < 0 || startHours > 23) {
            _errorStartHours.value = true
            result = false
        }
        if (startMinutes < 0 || startMinutes > 60) {
            _errorStartMinutes.value = true
            result = false
        }
        if (endHours < 0 || endHours > 23) {
            _errorEndHours.value = true
            result = false
        }
        if (endMinutes < 0 || endMinutes > 60) {
            _errorEndMinutes.value = true
            result = false
        }
        return result
    }

    fun resetErrorStartHours() {
        _errorStartHours.value = false
    }

    fun resetErrorStartMinutes() {
        _errorStartMinutes.value = false
    }

    fun resetErrorEndHours() {
        _errorEndHours.value = false
    }

    fun resetErrorEndMinutes() {
        _errorEndMinutes.value = false
    }
}