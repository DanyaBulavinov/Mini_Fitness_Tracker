package com.projects.minifitnesstracker.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.projects.minifitnesstracker.model.Sleep
import com.projects.minifitnesstracker.model.TrackingData
import com.projects.minifitnesstracker.model.TrackingDataDatabase
import com.projects.minifitnesstracker.utils.SharedPreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.sql.Date
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.min
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _trackingLiveData = MutableLiveData<TrackingData>()
    val trackingLiveData: LiveData<TrackingData>
        get() = _trackingLiveData
    private val _percentageOfGoal = MutableLiveData<Int>()
    val percentageOfGoal: LiveData<Int>
        get() = _percentageOfGoal
    private val _stepsGoal = MutableLiveData<Int>()
    val stepsGoal: LiveData<Int>
        get() = _stepsGoal
//    private val _amountOfSleep = MutableLiveData<Pair<Int, Int>>()
//    val amountOfSleep: LiveData<Pair<Int, Int>>
//        get() = _amountOfSleep

    private val currentDate = Date(System.currentTimeMillis())

    private var prefHelper = SharedPreferencesHelper(getApplication())

    private val dao by lazy {
        TrackingDataDatabase(getApplication()).TrackingDataDao()
    }

    fun init(onInitComplete: () -> Unit) {
//        val dayDate = Date.valueOf("2023-04-26")
        _stepsGoal.value = prefHelper.getStepsGoal()

        viewModelScope.async(Dispatchers.IO) {
            _percentageOfGoal.postValue(
                countPercentageOfGoal(
                    getTrackingLiveDataAsync(
                        currentDate
                    ).await().value?.amountOfSteps!!, stepsGoal.value!!
                )
            )
        }
        onInitComplete()
    }

    fun calculateAmountOfSleep(data: TrackingData): Pair<Int, Int>{
        val beginningOfSleep = data.sleep?.beginningOfSleep
        val endingOfSleep = data.sleep?.endingOfSleep
//        val beginningOfSleep = LocalDateTime.of(2023, 5, 16, 22,35)
//        val endingOfSleep = LocalDateTime.of(2023, 5, 17, 6,57)
        if (beginningOfSleep == null || endingOfSleep == null){
            return Pair(0, 0)
        }
        val duration = Duration.between(beginningOfSleep, endingOfSleep)
        val hours = duration.toHours().toInt()
        val minutes = (duration.toMinutes() % 60).toInt()
        return Pair(hours, minutes)
    }

    fun convertToHoursAndMinutes(amountOfSleep: Int): Pair<Int, Int>{
        val hours = (amountOfSleep / 60)
        val minutes = (amountOfSleep % 60)
        return Pair(hours, minutes)
    }

    private suspend fun getTrackingLiveDataAsync(date: Date) =
        viewModelScope.async {
            _trackingLiveData.postValue(dao.getOneRecord(date) ?: createCurrentDayRecord())
            trackingLiveData
        }

    private fun createCurrentDayRecord(): TrackingData {
        val currentDate = Date(System.currentTimeMillis())
        val trackingData = TrackingData(date = currentDate)
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(trackingData)
        }
        return trackingData
    }

    private fun countPercentageOfGoal(stepsWalked: Int, stepsGoal: Int): Int {
        return ((stepsWalked.toDouble() / stepsGoal.toDouble()) * 100).toInt()
    }

    fun onPlusButtonClicked() {
        val currentTrackingData = trackingLiveData.value
        val newGlassesOfWater = currentTrackingData?.glassesOfWater?.plus(1)
        val newTrackingData = currentTrackingData?.copy(glassesOfWater = newGlassesOfWater)
        _trackingLiveData.value = newTrackingData!!
        viewModelScope.launch(Dispatchers.IO) {
            trackingLiveData.value?.let { dao.update(it) }
        }
    }

    fun onMinusButtonClicked() {
        val currentTrackingData = trackingLiveData.value
        if (currentTrackingData?.glassesOfWater!! > 0) {
            val newGlassesOfWater = currentTrackingData.glassesOfWater?.minus(1)
            val newTrackingData = currentTrackingData.copy(glassesOfWater = newGlassesOfWater)
            _trackingLiveData.value = newTrackingData!!
            viewModelScope.launch(Dispatchers.IO) {
                trackingLiveData.value?.let { dao.update(it) }
            }
        }
    }
}