package com.example.finans_movil.Data.Local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.finans_movil.Data.Local.Dao.AccountDao
import com.example.finans_movil.Data.Local.Dao.MonthlyBillDao
import com.example.finans_movil.Data.Local.Dao.TransactionDao
import com.example.finans_movil.Data.Local.Entities.AccountEntity
import com.example.finans_movil.Data.Local.Entities.MonthlyBillEntity
import com.example.finans_movil.Data.Local.Entities.TransactionEntity

@Database(
    entities = [
        AccountEntity::class,
        TransactionEntity::class,
        MonthlyBillEntity::class
    ],
    version = 6
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao

    abstract fun transactionDao(): TransactionDao

    abstract fun monthlyBillDao(): MonthlyBillDao

}