package com.plcoding.stockmarketapp.data.repository

import com.plcoding.stockmarketapp.data.csv.CSVParser
import com.plcoding.stockmarketapp.data.csv.CompanyListingsParser
import com.plcoding.stockmarketapp.data.csv.IntraDayInfoParser
import com.plcoding.stockmarketapp.data.local.StockDataBase
import com.plcoding.stockmarketapp.data.mapper.toCompanyInfo
import com.plcoding.stockmarketapp.data.mapper.toCompanyListing
import com.plcoding.stockmarketapp.data.mapper.toCompanyListingEntity
import com.plcoding.stockmarketapp.data.remote.StockAPI
import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntroDayInfo
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api:StockAPI,
    val db:StockDataBase,
    val companyListingsParser: CSVParser<CompanyListing>,
    val intraDayInfoParser: CSVParser<IntroDayInfo>
):StockRepository{

    private val dao = db.dao

    override suspend fun getCompanyList(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow{
            // отправляем в поток состояние загрузки
            emit(Resource.Loading(true))
            // получаем данные
            val localListing = dao.searchCompanyListing(query)
            // выдаём результат после поиска
            emit(Resource.Success(
                // с помощью мапера переделываем возвращайвый тип данных
                data = localListing.map { it.toCompanyListing() }
            ))

            // узнаём пустая ли база
            val isDBEmpty = localListing.isEmpty() && query.isBlank()

            // проверяем не пустая ли база данных и не пытаются ли обновить данные
            if(!isDBEmpty && !fetchFromRemote){
                emit(Resource.Loading(false))
                return@flow
            }

            // обращаемся к API
            val remoteListing= try{
                // получаем данные с API
                val response = api.getListing()
                // полученные данные пепредаём в парсер
                companyListingsParser.parse(response.byteStream())
            }catch (e:IOException){
                e.printStackTrace()
                emit(Resource.Error("Ошибка "))
                null
            }catch (e:HttpException){
                e.printStackTrace()
                emit(Resource.Error("Ошибка "))
                null
            }

            remoteListing?.let { listing->
                // очищаем базу
                dao.clearCompanyListing()
                // засовываем последние данные с api
                dao.insertCompanyListing(
                    listing.map {
                        it.toCompanyListingEntity()
                    }
                )
                // возвращаем данные
                emit(Resource.Success(
                    data = dao.searchCompanyListing("").map { it.toCompanyListing() }
                ))
                // заканчиваем загрузку 
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getIntraDayInfo(symbols: String): Resource<List<IntroDayInfo>> {
        return try {
            val response = api.getIntradayInfo(symbols)
            val result = intraDayInfoParser.parse(response.byteStream())
            Resource.Success(result)
        }catch (e: IOException){
            e.printStackTrace()
            Resource.Error(message = "Ошибка ")
        }catch (e: HttpException){
            e.printStackTrace()
            Resource.Error(message = "Ошибка ")
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val result = api.getCompanyInfo(symbol)
            Resource.Success(result.toCompanyInfo())
        }catch (e: IOException){
            e.printStackTrace()
            Resource.Error(message = "Ошибка ")
        }catch (e: HttpException){
            e.printStackTrace()
            Resource.Error(message = "Ошибка ")
        }
    }
}