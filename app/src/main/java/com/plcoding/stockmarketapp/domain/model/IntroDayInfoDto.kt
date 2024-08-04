package com.plcoding.stockmarketapp.domain.model

import java.time.LocalDateTime
import java.time.LocalTime

data class IntroDayInfo(
    val data: LocalDateTime,
    val close: Double
)
