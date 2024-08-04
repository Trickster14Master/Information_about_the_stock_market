package com.plcoding.stockmarketapp.data.csv

import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanyListingsParser @Inject constructor() :CSVParser<CompanyListing> {
    override suspend fun parse(stream: InputStream): List<CompanyListing> {
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
                    val symbol = line.getOrNull(0)
                    val name = line.getOrNull(1)
                    val exchange = line.getOrNull(2)

                    // засовываем полученные данные в модель
                    CompanyListing(
                        name=name?: return@mapNotNull null,
                        symbol=symbol?: return@mapNotNull null,
                        exchange=exchange?: return@mapNotNull null,
                )
            }
                .also {
                    csvReader.close()
                }
        }
    }
}