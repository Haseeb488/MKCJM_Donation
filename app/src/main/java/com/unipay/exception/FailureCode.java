package unipay.exception;

public enum FailureCode {
	F21("%s is missing."),
	F22("%s is not well-formatted."),
	F23("%s length must be less or equal %d characters."),
	F99("%s unsupported validation."),

	I07("Identifier of the referenced %s is not unique."),
	I12("Unexpected type found. Expected: %s."),
	I08("Value specified for parameter %s is not among allowed values."),

	S20("%s error. Contact support."),
	S99("%s"),

	V22("%s is not among allowed values."),
	V24("Referenced %s is not found or not accessible to the current user."),
	V35("Identifier of the referenced %s is not unique."),

	U25("Operation is not supported for %s transactions");

	private final String errorMessage;

	FailureCode(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
