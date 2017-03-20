package com.example.mobile.dagger2test.dependency.modules.network;

public class SomeResponse {
    public String getSomeResponse() {
        return someResponse;
    }

    public void setSomeResponse(String someResponse) {
        this.someResponse = someResponse;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private int code;
    private String someResponse;
}
