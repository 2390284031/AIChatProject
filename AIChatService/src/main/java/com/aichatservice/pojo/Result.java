package com.aichatservice.pojo;

import com.aichatservice.utils.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//统一响应结果
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result<T> {
    private Integer code; // 业务状态码  1-成功  0-失败
    private String msg; // 提示信息
    private T data; // 响应数据

    //快速返回操作成功响应结果(带响应数据)
    public static <E> Result<E> success(E data) {
        return new Result<>(CommonUtil.SUCCESS_CODE, "操作成功", data);
    }

    public static Result success() {
        return new Result<>(CommonUtil.SUCCESS_CODE, "操作成功", null);
    }

    public static Result error(String message) {
        return new Result<>(CommonUtil.ERROR_CODE, message, null);
    }
}
