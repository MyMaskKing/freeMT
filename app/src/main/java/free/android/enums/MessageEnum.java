package free.android.enums;


public enum MessageEnum {

    ERROR("1"),
    INFO("2"),
    WARN("3"),
    ERROR_S1("S_1", "本页面无数据,将返回上一级页面.", ERROR.getType()),
    WARN_W1("W_1", "本页面无数据,请尝试添加数据.", WARN.getType()),;

    MessageEnum() {
    }

    private String messageId;
    private String messageInfo;
    private String messageType;

    MessageEnum(String messageId, String messageInfo, String messageType) {
        this.messageId = messageId;
        this.messageInfo = messageInfo;
        this.messageType = messageType;
    }

    MessageEnum(String messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return messageInfo;
    }

    public String getId() {
        return messageId;
    }

    public String getType() {
        return messageType;
    }

}

