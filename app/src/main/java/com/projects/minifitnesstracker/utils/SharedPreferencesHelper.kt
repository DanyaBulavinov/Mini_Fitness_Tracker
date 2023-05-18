package com.projects.minifitnesstracker.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.core.content.edit

class SharedPreferencesHelper {
    companion object {
        private const val PREF_STEPS_GOAL = "Steps goal"
        private var prefs: SharedPreferences? = null

        @Volatile
        private var instance: SharedPreferencesHelper? = null

        operator fun invoke(context: Context): SharedPreferencesHelper =
            instance ?: synchronized(this) {
                instance ?: buildHelper(context).also {
                    instance = it
                }
            }

        private fun buildHelper(context: Context): SharedPreferencesHelper {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return SharedPreferencesHelper()
        }
    }

    fun updateStepsGoal(stepsGoal: Int) {
        prefs?.edit(commit = true){
            putInt(PREF_STEPS_GOAL, stepsGoal)
        }
    }

    fun getStepsGoal() = prefs?.getInt(PREF_STEPS_GOAL, 8000)



}