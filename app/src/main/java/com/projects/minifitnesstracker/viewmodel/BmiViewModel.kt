package com.projects.minifitnesstracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.pow

class BmiViewModel : ViewModel() {
    private val _bmiScore = MutableLiveData<Float>()
    val bmiScore: LiveData<Float>
        get() = _bmiScore
    private val _bmiStatus = MutableLiveData<String>()
    val bmiStatus: LiveData<String>
        get() = _bmiStatus
    private val _errorHeight = MutableLiveData<Boolean>()
    val errorHeight: LiveData<Boolean>
        get() = _errorHeight
    private val _errorWeight = MutableLiveData<Boolean>()
    val errorWeight: LiveData<Boolean>
        get() = _errorWeight

    fun calculateBMI(inputHeight: String?, inputWeight: String?){
        val height = parseInput(inputHeight)
        val weight = parseInput(inputWeight)
        val fieldValid = validateInput(height, weight)
        if (fieldValid) {
            _bmiScore.value = calculateBMIScore(height, weight)
            _bmiStatus.value = getBMICategory(bmiScore.value?.toFloat() ?: 0f)
        }
    }

    private fun calculateBMIScore(height: Int, weight: Int): Float{
        return weight/((height/100.toFloat()).pow(2))
    }

    private fun getBMICategory(bmi: Float): String {
        return when (bmi) {
            in Double.MIN_VALUE..18.4 -> "Underweight"
            in 18.5..24.9 -> "Normal weight"
            in 25.0..29.9 -> "Overweight"
            in 30.0..34.9 -> "Obesity Class 1"
            in 35.0..39.9 -> "Obesity Class 2"
            else -> "Obesity Class 3"
        }
    }

    private fun parseInput(input: String?): Int {
        return try {
            input?.trim()?.toInt() ?: 0
        } catch (e: java.lang.Exception) {
            0
        }
    }

    private fun validateInput(height: Int, weight: Int): Boolean {
        var result = true
        if (height <= 0) {
            _errorHeight.value = true
            result = false
        }
        if (weight <= 0) {
            _errorWeight.value = true
            result = false
        }
        return result
    }

    fun resetErrorHeight() {
        _errorHeight.value = false
    }

    fun resetErrorWeight() {
        _errorWeight.value = false
    }
}