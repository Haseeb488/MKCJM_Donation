package com.uniread.pos;

import android.app.Activity;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

import com.unipay.api.fields.FieldName;
import com.unipay.api.fields.Fields;
import unipay.exception.UniPayException;
import unipay.exception.names.ReferenceName;

public class TerminalAPI {

	public static final Integer REQUEST_CODE = 100;

	private static final String TERMINAL_APP_PATH = "com.tidypos.terminal";

	private static String credentials = "";
	private static Map<FieldName, Object> defaults = new HashMap<>();

	private TerminalAPI() {
	}

	public static void exec(Activity activity, Map<FieldName, Object> params) {
		try {
			Intent intent = activity.getPackageManager().getLaunchIntentForPackage(TERMINAL_APP_PATH);
			if (intent == null) {
				throw UniPayException.f21(ReferenceName.Intent);
			}
			if (params == null) {
				throw UniPayException.f21(ReferenceName.Params);
			}
			if (credentials == null) {
				throw UniPayException.f21(ReferenceName.Credentials);
			}
			if (defaults == null) {
				throw UniPayException.f21(ReferenceName.Defaults);
			}
			params.putAll(defaults);
			intent.putExtra(Fields.PARAMS, convertRequestMap(params));
			intent.putExtra(Fields.CREDENTIALS, credentials);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			activity.startActivityForResult(intent, REQUEST_CODE);
		} catch (Exception e) {
			throw UniPayException.s20(e);
		}
	}

	public static void setCredentials(String credentials) {
		TerminalAPI.credentials = credentials;
	}

	public static void setDefaults(Map<FieldName, Object> defaults) {
		TerminalAPI.defaults = defaults;
	}

	public static Map<FieldName, String> getResponseParameters(Intent intent) {
		try {
			if (intent != null) {
				return prepareResponse(intent);
			} else {
				throw UniPayException.f21(ReferenceName.Intent);
			}
		} catch (Exception e) {
			throw UniPayException.s20(e);
		}
	}

	private static Map<FieldName, String> prepareResponse(Intent intent) {
		return convertResponseMap((Map<String, String>) intent.getSerializableExtra(Fields.RESPONSE));
	}

	private static HashMap<String, String> convertRequestMap(Map<FieldName, Object> requestMap) {
		HashMap<String, String> newMap = new HashMap<>(requestMap.size());
		for (Map.Entry<FieldName, Object> entry : requestMap.entrySet()) {
			newMap.put(entry.getKey().name(), String.valueOf(entry.getValue()));
		}
		return newMap;
	}

	private static Map<FieldName, String> convertResponseMap(Map<String, String> requestMap) {
		Map<FieldName, String> newMap = new HashMap<>(requestMap.size());
		for (Map.Entry<String, String> entry : requestMap.entrySet()) {
			newMap.put(failureToResponse(FieldName.fromString(entry.getKey())), entry.getValue());
		}
		return newMap;
	}

	private static FieldName failureToResponse(FieldName fieldName) {
		if (Fields.TerminalRes.FailureCode.equals(fieldName)) {
			return Fields.TerminalRes.ResponseCode;
		}
		if (Fields.TerminalRes.FailureMessage.equals(fieldName)) {
			return Fields.TerminalRes.ResponseMessage;
		}
		return fieldName;
	}
}