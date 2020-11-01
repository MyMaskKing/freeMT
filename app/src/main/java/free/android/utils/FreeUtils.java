package free.android.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import free.android.enums.FormatEnum;

/**
 * Free工程帮助类
 */
public class FreeUtils {

    public static String getSysDate(String... format) {
        if (format == null || format.length == 0) {
            format = new String[]{FormatEnum.TIME_FORMAT_V1.getVal()};
        }
        Date date = new Date();
        SimpleDateFormat formatRule = new SimpleDateFormat(format[0]);
        String strSysDate = formatRule.format(date);
        return strSysDate;
    }
}
