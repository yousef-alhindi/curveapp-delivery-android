package com.curve.delivery.new_architecture.helper

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

object CommonUtils {

    fun changeDateFormat(inputDate: String): String {
        val originalFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val targetFormat: SimpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val date: Date
        try {
            date = originalFormat.parse(inputDate)
            return targetFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
    }

    fun dateToTimestamp(dateStr: String): Long {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val date = dateFormat.parse(dateStr)
        return date?.time ?: 0L
    }

    fun convertDateToTimestampFrom(dateString: String): Long? {
        // Define the date format that matches the input date
        val dateFormat = SimpleDateFormat("dd/MM/yy | hh:mm a", Locale.getDefault())
        dateFormat.isLenient = false

        return try {
            // Parse the string into a Date object
            val date = dateFormat.parse(dateString+" | 12:00 AM")
            date?.time
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun convertDateToTimestampTo(dateString: String): Long? {
        // Define the date format that matches the input date
        val dateFormat = SimpleDateFormat("dd/MM/yy | hh:mm a", Locale.getDefault())
        dateFormat.isLenient = false

        return try {
            // Parse the string into a Date object
            val date = dateFormat.parse(dateString+" | 11:59 PM")
            date?.time
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun convertDateToTimestampRangeEnd(dateString: String): Pair<Long, Long>? {
        // Define the date format that matches the input date
        val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        dateFormat.isLenient = false // Ensure strict parsing

        return try {
            // Parse the string into a Date object
            val date = dateFormat.parse(dateString)

            // If parsing is successful, calculate the start and end timestamps
            date?.let {
                // Calendar instance to manipulate date
                val calendar = Calendar.getInstance().apply {
                    time = it

                    // Set the calendar to the start of the day (12:00 AM)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val startOfDay = calendar.timeInMillis

                // Set the calendar to the end of the day (11:59:59 PM)
                calendar.apply {
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }
                val endOfDay = calendar.timeInMillis

                // Return the range as a Pair
                Pair(startOfDay, endOfDay)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTimestampToDateString(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }
}