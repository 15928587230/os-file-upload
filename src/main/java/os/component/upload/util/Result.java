package os.component.upload.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString(callSuper = false)
@NoArgsConstructor
public class Result implements Serializable {

    private static final long serialVersionUID = 8295075842951977226L;

    private int status;

    private String msg;

    @JsonProperty
    private Data data;

    public Result(Status status) {
        this.status = status.getCode();
        this.msg = status.getMsg();
        this.data = Data.getData();
    }

    public Result(Status status, Object data) {
        this.status = status.getCode();
        this.msg = status.getMsg();
        this.data = Data.getData(data);
    }

    public Result(Status status, Object data, Page page) {
        this.status = status.getCode();
        this.msg = status.getMsg();
        this.data = Data.getData(data, page);
    }

    public Result(Status status, String msg) {
        this.status = status.getCode();
        this.msg = msg;
        this.data = Data.getData();
    }


    public Result(Status status, String msg, Object data) {
        this.status = status.getCode();
        this.msg = msg;
        this.data = Data.getData(data);
    }


    public Result(Status status, String msg, Object data, Page page) {
        this.status = status.getCode();
        this.msg = msg;
        this.data = Data.getData(data, page);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.status == Status.SUCCESS.getCode();
    }

    @JsonIgnore
    public boolean nonSuccess() {
        return this.status != Status.SUCCESS.getCode();
    }

    public static Result success() {
        return new Result(Status.SUCCESS);
    }

    public static Result illegal() {
        return new Result(Status.BAD_REQUEST);
    }

    public static Result unauthorized() {
        return new Result(Status.UNAUTHORIZED);
    }

    public static Result forbidden() {
        return new Result(Status.FORBIDDEN);
    }

    public static Result notFound() {
        return new Result(Status.NOT_FOUND);
    }

    public static Result failure() {
        return new Result(Status.FAILURE);
    }

    public static Result conflict() {
        return new Result(Status.CONFLICT);
    }

    public static Result build(Status status, Object data) {
        return new Result(status, data);
    }

    public static Result build(Status status, Object data, Page page) {
        return new Result(status, data, page);
    }

    public static Result build(Status status, String msg) {
        return new Result(status, msg);
    }

    public static Result build(Status status, int msgCode) {
        return new Result(status, msgCode);
    }

    public static Result build(Status status, String msg, Object data) {
        return new Result(status, msg, data);
    }

    public static Result build(Status status, String msg, Object data, Page page) {
        return new Result(status, msg, data, page);
    }

}

