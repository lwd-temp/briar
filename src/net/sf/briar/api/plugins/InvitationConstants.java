package net.sf.briar.api.plugins;

public interface InvitationConstants {

	long INVITATION_TIMEOUT = 30 * 1000; // Milliseconds

	int CODE_BITS = 19; // Codes must fit into six decimal digits

	int MAX_CODE = (1 << CODE_BITS) - 1; // 524287

	int HASH_LENGTH = 48; // Bytes

	int MAX_PUBLIC_KEY_LENGTH = 120; // Bytes
}
