package free.android.enums;

import free.android.utils.Constants;

/**
 * 所有画面信息
 */
public enum PageInfoEnum {
    INDEX_PAGE("index", "首页"),
    NOTE_PAGE("notePage", "便签画面"),
    NOTE_SUB_PAGE("noteSubPage", "(副)便签画面"),
    ;

    PageInfoEnum() {
    }

    private int id;
    private String key;
    private String value;

    PageInfoEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    PageInfoEnum(int id, String value) {
        this.id = id;
        this.value = value;
    }

    PageInfoEnum(String value) {
        this.value = value;
    }
    public String getVal() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public int getId() {
        return id;
    }

    public static String randomVal() {
        String result = "";
        // 10之内的随机数(四舍五入)
        int random = Integer.parseInt(new java.text.DecimalFormat("0").format(Math.random() * Constants.RANDOM_RANGE + 1));
        for (PageInfoEnum val : PageInfoEnum.values()) {
            System.out.println(random);
            if (val.getId() == random) {
                result = "SUCCESS";
                return val.getVal();
            }
        }
        if (!"SUCCESS".equals(result)) {
            randomVal();
        }
        return result;
    }
}

