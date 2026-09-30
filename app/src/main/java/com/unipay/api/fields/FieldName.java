package com.unipay.api.fields;

import java.util.Objects;

/**
 * @author UniDoc generator
 * @company UnitedThinkers
 * @since 21 Mar, 2017
 */

public class FieldName {

	protected String name;
	protected String caption;
	protected String validationName;

	protected FieldName(String name, String caption, String validationName) {
		this.name = name;
		this.caption = caption;
		this.validationName = validationName;
	}

	protected FieldName(String name, String caption) {
		this(name, caption, null);
	}

	public FieldName(String name) {
		this(name, name, null);
	}

	public static FieldName fromString(String fieldName) {
		if (fieldName == null) {
			return null;
		}
		return new FieldName(fieldName, fieldName);
	}

	public String name() {
		return name;
	}

	public String caption() {
		return caption;
	}

	public String validationName() {
		return validationName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (getClass() != o.getClass()) return false;
		if (!(o instanceof FieldName)) return false;
		FieldName fieldName = (FieldName) o;
		return this.name.equals(fieldName.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
