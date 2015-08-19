package org.jtwig.functions.builtin;

/**
 * Represents a string which should not be escaped at the end.
 *
 * @author Thomas Hunziker
 *
 */
public final class UnescaptedString {

	private final String content;

	public UnescaptedString(final String content) {
		this.content = content;
	}

	public String getContent() {
		return this.content;
	}

	@Override
	public String toString() {
		return this.content;
	}

}
