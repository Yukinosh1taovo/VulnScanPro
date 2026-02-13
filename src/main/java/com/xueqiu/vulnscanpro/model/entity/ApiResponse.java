package com.xueqiu.vulnscanpro.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// 通用的 ApiResponse 类来包装所有接口的返回数据
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    private Integer code; //响应码，1 代表成功; 0 代表失败
    private String msg;  //响应信息 描述字符串
    private Object data;  //返回的数据

    public static ApiResponse success(){
        return new ApiResponse(1, "success", null);
    }

    public static ApiResponse success(Object data){
        return new ApiResponse(1, "success", data);
    }

    public static ApiResponse success(String msg, Object data){
        return new ApiResponse(1, msg, data);
    }

    public static ApiResponse error(String msg){
        return new ApiResponse(0, msg, null);
    }

}
