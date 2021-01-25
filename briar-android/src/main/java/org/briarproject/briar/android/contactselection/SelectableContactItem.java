package org.briarproject.briar.android.contactselection;

import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.briar.android.contact.ContactItem;
import org.briarproject.briar.api.identity.AuthorInfo;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
@NotNullByDefault
public class SelectableContactItem extends ContactItem {

	private boolean selected;
	private final boolean disabled;

	public SelectableContactItem(Contact contact, AuthorInfo authorInfo,
			boolean selected, boolean disabled) {
		super(contact, authorInfo);
		this.selected = selected;
		this.disabled = disabled;
	}

	boolean isSelected() {
		return selected;
	}

	void toggleSelected() {
		selected = !selected;
	}

	public boolean isDisabled() {
		return disabled;
	}

}
