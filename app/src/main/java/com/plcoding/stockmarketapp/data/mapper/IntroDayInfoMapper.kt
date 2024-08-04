package com.plcoding.stockmarketapp.data.mapper

import com.plcoding.stockmarketapp.data.remote.dto.IntroDayInfoDto
import com.plcoding.stockmarketapp.domain.model.IntroDayInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun IntroDayInfoDto.toIntroDayInfo(): IntroDayInfo {
    // приобразыем пришедшую дату и время в определённый формат 
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val localDateTime = LocalDateTime.parse(timestamp, formatter)

    return IntroDayInfo(
        data = localDateTime,
        close=close
    )
}