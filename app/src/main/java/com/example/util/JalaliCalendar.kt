package com.example.util

import com.example.R

import java.util.Calendar
import java.util.TimeZone

object JalaliCalendar {

    data class JalaliDate(val year: Int, val month: Int, val day: Int) {
        override fun toString(): String {
            return "$year/$month/$day"
        }
    }

    fun g2j(gy: Int, gm: Int, gd: Int): JalaliDate {
        var outYear: Int
        var outMonth: Int
        var outDay: Int

        val g_d_m = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        val gy2 = if (gm > 2) (gy + 1) else gy
        var days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400) + gd + g_d_m[gm - 1]
        
        var jy = -1595 + (33 * (days / 12053))
        days %= 12053
        
        jy += 4 * (days / 1461)
        days %= 1461
        
        if (days > 365) {
            jy += (days - 1) / 365
            days = (days - 1) % 365
        }
        
        outYear = jy
        outMonth = if (days < 186) 1 + (days / 31) else 7 + ((days - 186) / 30)
        outDay = 1 + if (days < 186) (days % 31) else ((days - 186) % 30)
        
        return JalaliDate(outYear, outMonth, outDay)
    }

    fun j2g(jy: Int, jm: Int, jd: Int): Calendar {
        var gy = if (jy <= 979) 621 else 1600
        val jy2 = jy - if (jy <= 979) 0 else 979
        
        var days = (365 * jy2) + (jy2 / 33) * 8 + (jy2 % 33 + 3) / 4 + 78 + jd + if (jm < 7) (jm - 1) * 31 else (jm - 7) * 30 + 186
        
        gy += 400 * (days / 146097)
        days %= 146097
        
        if (days > 36524) {
            gy += 100 * (--days / 36524)
            days %= 36524
            if (days >= 365) days++
        }
        
        gy += 4 * (days / 1461)
        days %= 1461
        
        if (days > 365) {
            gy += (days - 1) / 365
            days = (days - 1) % 365
        }
        
        var gd = days + 1
        val sal_a = intArrayOf(0, 31, if ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var gm = 0
        for (i in 1..12) {
            if (gd <= sal_a[i]) {
                gm = i
                break
            }
            gd -= sal_a[i]
        }
        
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, gy)
        cal.set(Calendar.MONTH, gm - 1)
        cal.set(Calendar.DAY_OF_MONTH, gd)
        return cal
    }

    fun getPersianMonthName(ctx: android.content.Context, month: Int): String {
        return when (month) {
            1 -> ctx.getString(com.example.R.string.month_1)
            2 -> ctx.getString(com.example.R.string.month_2)
            3 -> ctx.getString(com.example.R.string.month_3)
            4 -> ctx.getString(com.example.R.string.month_4)
            5 -> ctx.getString(com.example.R.string.month_5)
            6 -> ctx.getString(com.example.R.string.month_6)
            7 -> ctx.getString(com.example.R.string.month_7)
            8 -> ctx.getString(com.example.R.string.month_8)
            9 -> ctx.getString(com.example.R.string.month_9)
            10 -> ctx.getString(com.example.R.string.month_10)
            11 -> ctx.getString(com.example.R.string.month_11)
            12 -> ctx.getString(com.example.R.string.month_12)
            else -> ""
        }
    }

    fun getPersianDayOfWeekName(ctx: android.content.Context, dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.SATURDAY -> ctx.getString(com.example.R.string.saturday)
            Calendar.SUNDAY -> ctx.getString(com.example.R.string.sunday)
            Calendar.MONDAY -> ctx.getString(com.example.R.string.monday)
            Calendar.TUESDAY -> ctx.getString(com.example.R.string.tuesday)
            Calendar.WEDNESDAY -> ctx.getString(com.example.R.string.wednesday)
            Calendar.THURSDAY -> ctx.getString(com.example.R.string.thursday)
            Calendar.FRIDAY -> ctx.getString(com.example.R.string.friday)
            else -> ""
        }
    }

    fun formatShamsiDate(ctx: android.content.Context, timestamp: Long): String {
        if (java.util.Locale.getDefault().language != "fa") {
            val formatter = java.text.SimpleDateFormat("EEEE, MMM d, yyyy", java.util.Locale.getDefault())
            return formatter.format(java.util.Date(timestamp))
        }
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)
        val jDate = g2j(gy, gm, gd)
        val dayName = getPersianDayOfWeekName(ctx, cal.get(Calendar.DAY_OF_WEEK))
        val monthName = getPersianMonthName(ctx, jDate.month)
        return "$dayName، ${jDate.day} $monthName ${jDate.year}".toPersianDigits()
    }
    
    fun formatShamsiDateShort(timestamp: Long): String {
        if (java.util.Locale.getDefault().language != "fa") {
            val formatter = java.text.SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault())
            return formatter.format(java.util.Date(timestamp))
        }
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)
        val jDate = g2j(gy, gm, gd)
        return "${jDate.year}/${jDate.month}/${jDate.day}".toPersianDigits()
    }

    fun getNowJalali(): JalaliDate {
        val cal = Calendar.getInstance()
        return g2j(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }
}

fun String.toPersianDigits(): String {
    if (java.util.Locale.getDefault().language != "fa") return this
    return this.map { char ->
        when (char) {
            '0' -> '۰'
            '1' -> '۱'
            '2' -> '۲'
            '3' -> '۳'
            '4' -> '۴'
            '5' -> '۵'
            '6' -> '۶'
            '7' -> '۷'
            '8' -> '۸'
            '9' -> '۹'
            else -> char
        }
    }.joinToString("")
}

fun Int.toPersianDigits(): String = this.toString().toPersianDigits()
fun Long.toPersianDigits(): String = this.toString().toPersianDigits()
