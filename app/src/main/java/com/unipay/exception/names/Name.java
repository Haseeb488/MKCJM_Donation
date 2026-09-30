package unipay.exception.names;

import java.io.Serializable;

public class Name implements Serializable {

	private static final long serialVersionUID = 4845065719526009125L;

	protected String value;

	protected Name(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public final String toString() {
		return getValue();
	}

}
