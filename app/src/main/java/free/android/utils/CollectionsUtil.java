package free.android.utils;

import java.util.Collection;

/**
 * 集合帮助类
 */
public class CollectionsUtil {
    /**
     * 判断集合是否有数据
     */
    public static boolean isEmptyByCollection(Collection collection) {
        boolean result = true;
        if (collection != null && !collection.isEmpty()) {
            result = false;
        }
        return result;
    }

    /**
     * 判断字符串数组是否有数据
     * @param params
     *          <BR>
     *              1.指定索引(从1开始)<BR/>
                    2.TODO<BR/>
     * @return 数组中的指定元素
     */
    public static String isEmptyByStrArray(String[] strs, Object... params) {
        String result = StringUtil.EMPTY;
        int index = 0;
        // 获取参数中的指定索引
        if (params != null && params.length > 0) {
            index = (int)params[0];
        }
        // 判断目标数组是否有值并且大于指定索引
        if (strs != null && strs.length >= index) {
            result = strs[index - 1];
        }
        return result;
    }

}
