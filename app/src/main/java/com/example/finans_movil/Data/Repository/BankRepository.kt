package com.example.finans_movil.Data.Repository

import com.example.finans_movil.Data.Local.Dao.AccountDao
import com.example.finans_movil.Data.Mapper.toDomain
import com.example.finans_movil.Data.Mapper.toEntity
import com.example.finans_movil.Model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BankRepository(
    private val accountDao: AccountDao
) {
    fun getAccounts(): Flow<List<Account>> {
        return accountDao.getAccounts().map { list ->
            list.map { it.toDomain() } }
    }

    suspend fun insertAccount(account: Account) {
        accountDao.insertAccount(account.toEntity())
    }

    suspend fun getAccountsOnce(): List<Account> {
        return accountDao.getAccountsOnce().map { it.toDomain() }
    }
}