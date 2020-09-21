package com.hr.publishedco;

public class CommonResult<T> {
	
	private String code;
	private T data;
	private String message;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
	
	public CommonResult() {
		super();
	}
	public CommonResult(String code, T data, String message) {
		super();
		this.code = code;
		this.data = data;
		this.message = message;
	}
	@Override
	public String toString() {
		return "{\"code\":\"" + code +  "\", \"data\":" + data + ", \"message\":\"" + message + "\"}";
	}
	
	public static <T> CommonResult<T> success(T data) {
		return new CommonResult<T>("Y",data,"成功");
	}
	public static <T> CommonResult<T> success() {
		return new CommonResult<T>("Y",null,"成功");
	}
	
	public static <T> CommonResult<T> success(T data,String message) {
		return new CommonResult<T>("Y", data, message);
	}
	
	public static <T> CommonResult<T> success(Integer code,T data, String message) {
		return new CommonResult<T>("Y", data, message);
	}
	
	public static <T> CommonResult<T> failed(T data,String message) {
		return new CommonResult<T>("N", data, message);
	}
	
	public static <T> CommonResult<T> failed(String message) {
		return new CommonResult<T>("N", null, message);
	}
	
	public static <T> CommonResult<T> failed(Integer code,T data, String message) {
		return new CommonResult<T>("N", data, message);
	}
	public static <T> CommonResult<T> failed() {
		return new CommonResult<T>("N",null,"失败");
	}

}
