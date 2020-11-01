package free.android.enums;

import free.android.utils.Constants;

public enum ColorEnum {
    BTN_1(1, "#1"),
    BTN_2(2, "#2"),;

    ColorEnum() {
    }

    private int id;
    private String value;

    ColorEnum(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getVal() {
        return value;
    }

    public int getId() {
        return id;
    }

    public static String randomVal() {
        String result = "";
        // 10之内的随机数(四舍五入)
        int random = Integer.parseInt(new java.text.DecimalFormat("0").format(Math.random() * Constants.RANDOM_RANGE + 1));
        for (ColorEnum val : ColorEnum.values()) {
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

