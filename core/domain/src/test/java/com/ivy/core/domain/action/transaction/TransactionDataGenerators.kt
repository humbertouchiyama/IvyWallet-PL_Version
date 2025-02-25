package com.ivy.core.domain.action.transaction

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import java.time.LocalDateTime
import java.util.UUID

fun account(): Account =
    Account(
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

fun tag(): Tag =
    Tag(
        id = UUID.randomUUID().toString(),
        name = "Test tag",
        color = Color.Red.toArgb(),
        orderNum = 1.0,
        state = TagState.Default,
        sync = Sync(
            state = SyncState.Syncing,
            lastUpdated = LocalDateTime.now()
        ),
    )

fun attachment(associatedId: String): Attachment = Attachment(
    id = UUID.randomUUID().toString(),
    associatedId = associatedId,
    uri = "test",
    source = AttachmentSource.Local,
    filename = "test",
    type = AttachmentType.Image,
    sync = Sync(
        state = SyncState.Syncing,
        lastUpdated = LocalDateTime.now()
    )
)

fun transaction(account: Account): Transaction =
    Transaction(
        id = UUID.randomUUID(),
        account = account,
        type = TransactionType.Income,
        value = Value(5.0, "USD"),
        category = null,
        time = dummyTrnTimeActual(),
        title = "Test title",
        description = "Test description",
        state = TrnState.Default,
        purpose = null,
        tags = listOf(),
        attachments = listOf(),
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