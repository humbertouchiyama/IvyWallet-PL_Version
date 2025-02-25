package com.ivy.core.domain.action.transaction

import androidx.sqlite.db.SupportSQLiteQuery
import com.ivy.core.persistence.dao.trn.AccountIdAndTrnTime
import com.ivy.core.persistence.dao.trn.TransactionDao
import com.ivy.core.persistence.entity.attachment.AttachmentEntity
import com.ivy.core.persistence.entity.trn.TransactionEntity
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.core.persistence.entity.trn.TrnTagEntity
import com.ivy.data.SyncState

class TransactionDaoFake: TransactionDao() {

    val transactions = mutableListOf<TransactionEntity>()
    val tags = mutableListOf<TrnTagEntity>()
    val attachments = mutableListOf<AttachmentEntity>()
    private val metadata = mutableListOf<TrnMetadataEntity>()

    override suspend fun saveTrnEntity(entity: TransactionEntity) {
        transactions.removeIf { it.id == entity.id }
        transactions.add(entity)
    }

    override suspend fun updateTrnTagsSyncByTrnId(trnId: String, sync: SyncState) {
        transactions.indexOfFirst { it.id == trnId }
            .takeIf { it != -1 }
            ?.let { index ->
                transactions[index] = transactions[index].copy(sync = sync)
            }
    }

    override suspend fun saveTags(entity: List<TrnTagEntity>) {
        entity.forEach {
            tags.removeIf { tag -> tag.tagId == it.tagId }
            tags.add(it)
        }
    }

    override suspend fun updateAttachmentsSyncByAssociatedId(
        associatedId: String,
        sync: SyncState
    ) {
        attachments.indexOfFirst { it.id == associatedId }
            .takeIf { it != -1 }
            ?.let { index ->
                attachments[index] = attachments[index].copy(sync = sync)
            }
    }

    override suspend fun saveAttachments(entity: List<AttachmentEntity>) {
        entity.forEach {
            attachments.removeIf { attachment -> attachment.id == it.id }
            attachments.add(it)
        }
    }

    override suspend fun updateMetadataSyncByTrnId(trnId: String, sync: SyncState) {
        metadata.indexOfFirst { it.id == trnId }
            .takeIf { it != -1 }
            ?.let { index ->
                metadata[index] = metadata[index].copy(sync = sync)
            }
    }

    override suspend fun saveMetadata(entity: List<TrnMetadataEntity>) {
        entity.forEach {
            metadata.removeIf { metadata -> metadata.id == it.id }
            metadata.add(it)
        }
    }

    override suspend fun findAllBlocking(): List<TransactionEntity> {
        return transactions.filter { it.sync != SyncState.Deleting }
    }

    // not supported for fake
    override suspend fun findBySQL(query: SupportSQLiteQuery): List<TransactionEntity> {
        return transactions
    }

    override suspend fun findAccountIdAndTimeById(trnId: String): AccountIdAndTrnTime? {
        val transaction = transactions.find {
            it.id == trnId && it.sync != SyncState.Deleting
        } ?: return null

        return AccountIdAndTrnTime(
            accountId = transaction.accountId,
            time = transaction.time,
            timeType = transaction.timeType
        )
    }

    override suspend fun updateTrnEntitySyncById(trnId: String, sync: SyncState) {
        transactions.indexOfFirst { it.id == trnId }
            .takeIf { it != -1 }
            ?.let { index ->
                transactions[index] = transactions[index].copy(sync = sync)
            }
    }
}