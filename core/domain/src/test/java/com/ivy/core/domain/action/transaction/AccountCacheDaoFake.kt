package com.ivy.core.domain.action.transaction

import com.ivy.core.persistence.algorithm.accountcache.AccountCacheDao
import com.ivy.core.persistence.algorithm.accountcache.AccountCacheEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.Instant

class AccountCacheDaoFake: AccountCacheDao {

    private val cacheFlow = MutableStateFlow<List<AccountCacheEntity>>(emptyList())

    override fun findAccountCache(accountId: String): Flow<AccountCacheEntity?> {
        return cacheFlow.map { entities ->
            entities.firstOrNull { it.accountId == accountId }
        }
    }

    override suspend fun findTimestampById(accountId: String): Instant? {
        return cacheFlow.firstOrNull()?.firstOrNull { it.accountId == accountId }?.timestamp
    }

    override suspend fun save(cache: AccountCacheEntity) {
        cacheFlow.value = cacheFlow.value.filterNot { it.accountId == cache.accountId } + cache
    }

    override suspend fun delete(accountId: String) {
        cacheFlow.value = cacheFlow.value.filterNot { it.accountId == accountId }
    }

    override suspend fun deleteAll() {
        cacheFlow.value = emptyList()
    }
}