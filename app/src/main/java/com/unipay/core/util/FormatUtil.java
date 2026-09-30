package unipay.core.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FormatUtil {

	private static final String DATE_FULL = "yyyyMMdd HHmmssSSS";
	private static final TimeZone SERVER_TIME_ZONE = TimeZone.getDefault();
	private static final Locale SERVER_LOCALE = Locale.getDefault();

	public static SimpleDateFormat getFullDateFormat() {
		return new SimpleDateFormat(DATE_FULL);
	}

	public static Date getDate() {
		return getCalendar().getTime();
	}

	public static Calendar getCalendar() {
		return getCalendar(SERVER_TIME_ZONE, SERVER_LOCALE);
	}

	public static Calendar getCalendar(TimeZone timeZone, Locale locale) {
		return Calendar.getInstance(timeZone, locale);
	}

	public static Date getDate(Timestamp value) {
		return new Date(value.getTime());
	}

	public static Date getDate(Long timeInMillis) {
		Calendar calendar = getCalendar();
		calendar.setTimeInMillis(timeInMillis);
		return calendar.getTime();
	}

	public static Date getCurrentDate() {
		return new Date();
	}

	public static Long getTimeMillis() {
		return getDate().getTime();
	}

	public static boolean isNullOrEmpty(String row) {
		return row == null || row.isEmpty();
	}

	public static String concat(Object... args) {
		return concatWithDelimiter(null, args);
	}

	public static String concatWithDelimiter(String delimiter, Object... args) {
		return concatWithDelimiter(delimiter, 128, args);
	}

	public static String concatWithDelimiter(String delimiter, Integer capacity, Object... args) {
		if (args == null || args.length == 0) {
			return null;
		}
		StringBuilder builder = new StringBuilder(capacity);
		concatInternal(builder, delimiter, args);
		if (builder.length() == 0) {
			return null;
		}
		if (delimiter == null) {
			return builder.toString();
		}
		return builder.delete(builder.length() - delimiter.length(), builder.length()).toString();
	}

	private static void concatInternal(StringBuilder builder, String delimiter, Object[] args) {
		for (Object obj : args) {
			if (obj != null) {
				if (obj instanceof Object[]) {
					concatInternal(builder, delimiter, (Object[]) obj);
				} else {
					builder.append(obj);
					if (delimiter != null) {
						builder.append(delimiter);
					}
				}
			}
		}
	}
}
