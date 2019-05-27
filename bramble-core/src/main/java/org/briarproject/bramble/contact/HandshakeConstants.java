package org.briarproject.bramble.contact;

interface HandshakeConstants {

	/**
	 * The current version of the handshake protocol.
	 */
	byte PROTOCOL_VERSION = 0;

	/**
	 * Label for deriving the master key.
	 */
	String MASTER_KEY_LABEL = "org.briarproject.bramble.handshake/MASTER_KEY";

	/**
	 * Label for deriving Alice's proof of ownership from the master key.
	 */
	String ALICE_PROOF_LABEL = "org.briarproject.bramble.handshake/ALICE_PROOF";

	/**
	 * Label for deriving Bob's proof of ownership from the master key.
	 */
	String BOB_PROOF_LABEL = "org.briarproject.bramble.handshake/BOB_PROOF";

}
