package os.component.upload.util;

public enum Status {

    /**
     * 请求执行成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 请求验证失败
     */
    BAD_REQUEST(400, "操作验证失败"),

    /**
     * 权限不足
     */
    UNAUTHORIZED(401, "操作未授权"),

    /**
     * 请求拒绝
     */
    FORBIDDEN(403, "操作被拒绝"),

    /**
     * 未知请求
     */
    NOT_FOUND(404, "未知操作"),

    /**
     * 未知请求
     */
    CONFLICT(409, "请求发生冲突"),

    /**
     * 请求执行失败
     */
    FAILURE(500, "操作失败");

    private int code;

    private String msg;

    Status(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode(){
        return this.code;
    }

    public static Status getInstance(int code) {
        Status[] values = Status.values();
        for (Status value : values) {
            if (value.code == code) {
                return value;
            }
        }
        return FAILURE;
    }

    public String getMsg(){
        return this.msg;
    }
}
