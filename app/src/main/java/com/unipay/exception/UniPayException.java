package unipay.exception;

import android.util.Log;

import java.text.SimpleDateFormat;

import unipay.exception.names.ReferenceName;
import unipay.core.util.FormatUtil;

public class UniPayException extends RuntimeException {

	private static final String DOT = ". ";
	private static final String RESPONSE_CODE = "responseCode=";
	private static final int NODE = 1;
	private final FailureCode failureCode;
	private final Integer fieldLength;
	private final String fieldName;
	private final String eventId;

	private UniPayException(FailureCode failureCode, String fieldName, Integer fieldLength, Throwable cause) {
		super(cause);
		this.failureCode = failureCode;
		this.fieldName = fieldName;
		this.fieldLength = fieldLength;
		this.eventId = generateEventId();
		Log.e(failureCode.name(), getMessage(), cause);
	}

	private UniPayException(FailureCode failureCode, String fieldName) {
		this(failureCode, fieldName, null, null);
	}

	public static UniPayException f21(ReferenceName fieldName) {
		return new UniPayException(FailureCode.F21, fieldName.getValue());
	}

	public static UniPayException f22(String fieldName) {
		return new UniPayException(FailureCode.F22, fieldName);
	}

	public static UniPayException f23(String fieldName, Integer fieldLength) {
		return new UniPayException(FailureCode.F23, fieldName, fieldLength, null);
	}

	public static UniPayException s20(Exception e) {
		return isUniTerminalException(e) ? (UniPayException) e : new UniPayException(FailureCode.S20, null, null, e);
	}

	public static UniPayException s20(String fieldName) {
		return new UniPayException(FailureCode.S20, fieldName, null, null);
	}

	public static UniPayException s20() {
		return new UniPayException(FailureCode.S20, "System", null, null);
	}

	public static String generateEventId() {
		return String.format("%d:%s", NODE, new SimpleDateFormat("ddhhmmssSSS").format(FormatUtil.getDate()));
	}

	public static boolean isUniTerminalException(Throwable thr) {
		return thr instanceof UniPayException;
	}

	public FailureCode getFailureCode() {
		return failureCode;
	}

	@Override
	public String getMessage() {
		if (fieldLength == null) {
			return FormatUtil.concat(RESPONSE_CODE, failureCode, DOT, String.format(failureCode.getErrorMessage(), fieldName));
		} else {
			return FormatUtil.concat(RESPONSE_CODE, failureCode, DOT, String.format(failureCode.getErrorMessage(), fieldName, fieldLength));
		}
	}
}