package com.app2waf.Model;

public class Response {
    public int StatusCode;
    public String Body;


    public Response(int code, String Body){
        this.StatusCode = code;
        this.Body = Body;
    }
}
