package com.plcoding.stockmarketapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.plcoding.stockmarketapp.data.local.dao.StockDao
import com.plcoding.stockmarketapp.data.local.entity.CompanyListingEntity


@Database(
    entities = [CompanyListingEntity::class],
    version = 1
)
abstract class StockDataBase:RoomDatabase(){
    abstract val dao:StockDao
}