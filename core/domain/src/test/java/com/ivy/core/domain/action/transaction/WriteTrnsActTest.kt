package com.ivy.core.domain.action.transaction

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.ivy.core.domain.algorithm.accountcache.InvalidateAccCacheAct
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.account.AccountState
import com.ivy.data.attachment.Attachment
import com.ivy.data.attachment.AttachmentSource
import com.ivy.data.attachment.AttachmentType
import com.ivy.data.tag.Tag
import com.ivy.data.tag.TagState
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnMetadata
import com.ivy.data.transaction.TrnState
import com.ivy.data.transaction.dummyTrnTimeActual
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

internal class WriteTrnsActTest {

    private lateinit var writeTrnsAct: WriteTrnsAct
    private lateinit var transactionDaoFake: TransactionDaoFake
    private lateinit var timeProviderFake: TimeProviderFake
    private lateinit var invalidateAccCacheAct: InvalidateAccCacheAct
    private lateinit var accountCacheDaoFake: AccountCacheDaoFake

    @BeforeEach
    fun setUp() {
        transactionDaoFake = TransactionDaoFake()
        timeProviderFake = TimeProviderFake()
        accountCacheDaoFake = AccountCacheDaoFake()
        invalidateAccCacheAct = InvalidateAccCacheAct(
            accountCacheDao = accountCacheDaoFake,
            timeProvider = timeProviderFake
        )
        writeTrnsAct = WriteTrnsAct(
            transactionDao = transactionDaoFake,
            trnsSignal = TrnsSignal(),
            timeProvider = timeProviderFake,
            invalidateAccCacheAct = invalidateAccCacheAct,
            accountCacheDao = accountCacheDaoFake
        )
    }

    @Test
    fun `Test create new transaction with income`() = runBlocking<Unit> {
        val account = Account(
            id = UUID.randomUUID(),
            name = "Test name",
            currency = "USD",
            color = Color.Red.toArgb(),
            excluded = false,
            orderNum = 1.0,
            state = AccountState.Default,
            sync = Sync(
                state = SyncState.Synced,
                lastUpdated = LocalDateTime.now()
            ),
            icon = null,
            folderId = null
        )
        val tag = Tag(
            id = "1",
            name = "Test tag",
            color = Color.Red.toArgb(),
            orderNum = 1.0,
            state = TagState.Default,
            sync = Sync(
                state = SyncState.Syncing,
                lastUpdated = LocalDateTime.now()
            ),
        )
        val transactionId = UUID.randomUUID()
        val attachment = Attachment(
            id = UUID.randomUUID().toString(),
            associatedId = transactionId.toString(),
            uri = "test",
            source = AttachmentSource.Local,
            filename = "test",
            type = AttachmentType.Image,
            sync = Sync(
                state = SyncState.Syncing,
                lastUpdated = LocalDateTime.now()
            )
        )
        val transaction = Transaction(
            id = transactionId,
            account = account,
            type = TransactionType.Income,
            value = Value(5.0, "USD"),
            category = null,
            time = dummyTrnTimeActual(),
            title = "Test title",
            description = "Test description",
            state = TrnState.Default,
            purpose = null,
            tags = listOf(tag),
            attachments = listOf(attachment),
            metadata = TrnMetadata(
                recurringRuleId = null,
                loanId = null,
                loanRecordId = null
            ),
            sync = Sync(
                state = SyncState.Syncing,
                lastUpdated = LocalDateTime.now()
            )
        )
        writeTrnsAct(WriteTrnsAct.Input.CreateNew(transaction))

        val cachedTransaction = transactionDaoFake.transactions.find {
            it.id == transaction.id.toString()
        }
        val cachedTag = transactionDaoFake.tags.find {
            it.tagId == tag.id
        }
        val cachedAttachment = transactionDaoFake.attachments.find {
            it.id == attachment.id
        }

        assertThat(cachedTransaction).isNotNull()
        assertThat(cachedTransaction?.type).isEqualTo(TransactionType.Income)
        assertThat(cachedTransaction?.amount).isEqualTo(5.0)
        assertThat(cachedTransaction?.title).isEqualTo("Test title")
        assertThat(cachedTransaction?.description).isEqualTo("Test description")
        assertThat(cachedTransaction?.currency).isEqualTo("USD")

        assertThat(cachedTag).isNotNull()
        assertThat(cachedTag?.trnId).isEqualTo(transactionId.toString())

        assertThat(cachedAttachment).isNotNull()
        assertThat(cachedAttachment?.associatedId).isEqualTo(transactionId.toString())
    }
}