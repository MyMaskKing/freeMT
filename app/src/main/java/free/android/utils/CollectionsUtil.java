package free.android.utils;

import java.util.Collection;

/**
 * 集合帮助类
 */
public class CollectionsUtil {
    /**
     * 判断集合是否有数据
     */
    public static boolean isEmpty(Collection collection) {
        boolean result = true;
        if (collection != null && !collection.isEmpty()) {
            result = false;
        }
        return result;
    }

}
