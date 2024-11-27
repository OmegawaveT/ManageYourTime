package com.example.manageyourtime.utils

import com.example.manageyourtime.MNTViewModel
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.time.temporal.TemporalAdjusters
import java.util.*

fun convertDateTimeToTimestamp(dateString: String): Long {
    // 使用 "yyyy-MM-dd H:mm" 来支持单数字小时
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m") // 允许时间部分是单位数
    val dateTime = LocalDateTime.parse(dateString, formatter) // 使用 LocalDateTime 解析
    val zoneId = ZoneId.systemDefault() // 使用系统默认时区
    return dateTime.atZone(zoneId).toInstant().toEpochMilli() // 转换为时间戳（毫秒）
}

fun convertDateToTimestamp(dateString: String): Long {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.parse(dateString, formatter)  // 解析字符串为 LocalDate
    val zoneId = ZoneId.systemDefault()  // 使用系统默认时区
    return date.atStartOfDay(zoneId).toEpochSecond()  // 获取时间戳（秒）
}

fun getWeekStartAndEnd(date: LocalDate): Pair<Long, Long> {
    val weekStart = date.with(DayOfWeek.MONDAY)
    val weekEnd = date.with(DayOfWeek.SUNDAY)

    val weekStartTimestamp = weekStart.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
    val weekEndTimestamp = weekEnd.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000

    return Pair(weekStartTimestamp, weekEndTimestamp)
}

fun getWeekDates(date: LocalDate): List<LocalDate> {
    val weekStart = date.with(DayOfWeek.MONDAY)
    return (0..6).map { weekStart.plusDays(it.toLong()) }
}

fun Long.toDateString(): String {
    val instant = Instant.ofEpochMilli(this) // 将时间戳转换为 Instant
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()) // 转换为 LocalDateTime
    return dateTime.toLocalDate().toString() // 获取日期部分（格式：YYYY-MM-DD）
}

fun Long.toTimeString(): String {
    val instant = Instant.ofEpochMilli(this) // 将时间戳转换为 Instant
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()) // 转换为 LocalDateTime
    return dateTime.toLocalTime().toString() // 获取时间部分（格式：HH:mm:ss）
}

fun getTimestampFromDateTime(dateTimeStr: String): Long {
    // 定义日期时间的格式
    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")

    // 解析字符串为 LocalDateTime
    val localDateTime = LocalDateTime.parse(dateTimeStr, formatter)

    // 转换为时间戳（毫秒）
    return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun main() {
    val currentDate = LocalDate.now()

    // 获取当前日期所在周的起止时间戳
    val (startTimestamp, endTimestamp) = getWeekStartAndEnd(currentDate)
    println("Week Start Timestamp: $startTimestamp")
    println("Week End Timestamp: $endTimestamp")

    // 获取当前周的日期列表
    val weekDates = getWeekDates(currentDate)

    weekDates.forEach { date ->
        val year = date.year
        val month = date.monthValue
        val day = date.dayOfMonth

        println("Year: $year, Month: $month, Day: $day")
    }
}