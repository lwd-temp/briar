package org.briarproject.briar.api.socialbackup;

import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.db.Transaction;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.ClientId;
import org.briarproject.briar.api.conversation.ConversationManager;
import org.briarproject.briar.api.conversation.ConversationMessageHeader;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

@NotNullByDefault
public interface SocialBackupManager extends
		ConversationManager.ConversationClient {

	/**
	 * The unique ID of the social backup client.
	 */
	ClientId CLIENT_ID = new ClientId("pw.darkcrystal.backup");

	/**
	 * The current major version of the social backup client.
	 */
	int MAJOR_VERSION = 0;

	/**
	 * The current minor version of the social backup client.
	 */
	int MINOR_VERSION = 0;

	/**
	 * Returns the metadata for this device's backup, or null if no backup has
	 * been created.
	 */
	@Nullable
	BackupMetadata getBackupMetadata(Transaction txn) throws DbException;

	/**
	 * Creates a backup for this device using the given custodians and
	 * threshold. The encrypted backup and a shard of the backup key will be
	 * sent to each custodian.
	 *
	 * @throws BackupExistsException If a backup already exists
	 */
	void createBackup(Transaction txn, List<ContactId> custodianIds,
			int threshold) throws DbException;

	@Override
	Collection<ConversationMessageHeader> getMessageHeaders(
			Transaction txn, ContactId contactId) throws DbException;

	boolean amCustodian(Transaction txn, ContactId contactId);

	ReturnShardPayload getReturnShardPayload(Transaction txn, ContactId contactId)
			throws DbException;

	byte[] getReturnShardPayloadBytes(Transaction txn, ContactId contactId)
			throws DbException;
}
