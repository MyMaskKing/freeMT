package free.android.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StringUtil {

	public static final String EMPTY = "";

	public static String isEmptyReturnString(String str) {
		if (str == null || EMPTY.equals(str)) {
			return EMPTY;
		}
		return str;
	}

	public static String isEmptyReturnZero(String str) {
		if (str == null || EMPTY.equals(str)) {
			return "0";
		}
		return str;
	}

	public static boolean isEmptyReturnBoolean(String str) {
		if (str == null || EMPTY.equals(str.trim()) || Constants.STR_NULL.toUpperCase().equals(str.trim().toUpperCase())) {
			return true;
		}
		return false;

	}

	public static Integer isEmptyReturnInteger(String str) {
		Integer result = new Integer(0);
		if(!isEmptyReturnBoolean(str)){
			result = Integer.parseInt(str);
		}
		return result;

	}

	public static BigDecimal isEmptyReturnBigDecimal(String str) {
		BigDecimal result = new BigDecimal(0);
		if(!isEmptyReturnBoolean(str)){
			try{
				result = new BigDecimal(str);
			}catch (Exception e){
				return result;
			}
		}
		return result;

	}

	public static boolean equaleReturnBoolean(String str1, String str2) {
		if (str1 == null && str2 == null) {
			return true;
		}
		if (str1 == null || str2 == null) {
			return false;
		}
		if (str1.equals(str2)) {
			return true;
		}
		return false;

	}

	public static String split(int getValIndex, String targetVal, String... splitVal) {
		String val = isEmptyReturnString(targetVal);
		List<String> listVal = new ArrayList<String>();
		for (int i = 0; i < splitVal.length; i++) {
			String[] split = targetVal.split(splitVal[i]);
			if (split.length > 0) {
				listVal.add(split[0]);
			}
			if (split.length > 1) {
				listVal.add(split[1]);
				targetVal = split[1];
			}
		}
		val = listVal.get(getValIndex - 1);
		return val;
	}

	private void splicRepeat() {

	}
}
