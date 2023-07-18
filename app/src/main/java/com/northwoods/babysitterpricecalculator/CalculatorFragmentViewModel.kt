package com.northwoods.babysitterpricecalculator

import android.widget.TimePicker
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class CalculatorFragmentViewModel : ViewModel() {

    companion object{
        const val START_TO_BEDTIME_RATE = 12
        const val BEDTIME_TO_MIDNIGHT_RATE = 8
        const val MIDNIGHT_TO_END_RATE = 16
    }

    private val _totalChargeData: MutableLiveData<Int> = MutableLiveData()
    val totalChargeData: LiveData<Int> =
        _totalChargeData

    private val _error: MutableLiveData<DataError> = MutableLiveData()
    val error: LiveData<DataError> =
        _error

    fun calculateCharge(
        startTimePicker: TimePicker,
        endTimePicker: TimePicker,
        bedTimePicker: TimePicker
    ) {
        val startTime = convertTo24HourFormat(startTimePicker.hour, startTimePicker.minute)
        val endTime = convertTo24HourFormat(endTimePicker.hour, endTimePicker.minute)
        val bedTime = convertTo24HourFormat(bedTimePicker.hour, bedTimePicker.minute)

        if (startTime < 19 || endTime > 28 || bedTime < 19 || bedTime > 28) {
            _error.value = DataError.TimeError
            return
        }
        var totalCharge = 0
        // Calculate charge from start-time to bedtime
        val startToBedtimeHours = minOf(bedTime, endTime) - maxOf(startTime, 17)
        totalCharge += startToBedtimeHours * START_TO_BEDTIME_RATE
        // Calculate charge from bedtime to midnight
        val bedtimeToMidnightHours = maxOf(0, minOf(endTime, 24) - bedTime)
        totalCharge += bedtimeToMidnightHours * BEDTIME_TO_MIDNIGHT_RATE
        // Calculate charge from midnight to end
        val midnightToEndHours = maxOf(0, endTime - 24)
        totalCharge += midnightToEndHours * MIDNIGHT_TO_END_RATE

        if (totalCharge < 0) {
            _error.value = DataError.CalculateError
        } else {
            _totalChargeData.value = totalCharge
        }
    }


    private fun convertTo24HourFormat(hour: Int, minute: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val hourValue = calendar.get(Calendar.HOUR_OF_DAY)
        return if (hourValue <= 4) {
            hourValue + 24
        } else {
            hourValue
        }
    }

    sealed class DataError {
        object TimeError : DataError()
        object CalculateError : DataError()
    }
}