package com.plcoding.stockmarketapp.data.csv

import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.data.mapper.toIntroDayInfo
import com.plcoding.stockmarketapp.data.remote.dto.IntroDayInfoDto
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntroDayInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDateTime
import javax.inject.Inject

class IntraDayInfoParser @Inject constructor() :CSVParser<IntroDayInfo> {
    override suspend fun parse(stream: InputStream): List<IntroDayInfo> {
        // преобразуем полученные CSV данные
        val csvReader = CSVReader(InputStreamReader(stream))
        // возвращякм значение в нутри контекста курутины
        return withContext(Dispatchers.IO){
            csvReader
                // начинаем читать
                .readAll()
                // пропускаем первую строку
                .drop(1)
                // начинаем полузать данные
                .mapNotNull { line->
                    val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                    val close = line.getOrNull(1) ?: return@mapNotNull null

                    // засовываем полученные данные в модель
                    val dto = IntroDayInfoDto(timestamp, close.toDouble())
                    dto.toIntroDayInfo()

                }
                .filter {
                    it.data.dayOfMonth == LocalDateTime.now().minusDays(1).dayOfMonth
                }
                .sortedBy {
                    it.data.hour
                }
                .also {
                    csvReader.close()
                }
        }
    }
}