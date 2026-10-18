package com.gitgirls.cyclewise.logic.userlogin;

public final class AuthResult {
    private final boolean success;
    private final String message;

    private AuthResult(boolean success, String message){
        this.success = success;
        this.message = message;
    }

    public static AuthResult success(String message){
        return new AuthResult(true, message);
    }

    public static AuthResult failure(String message){
        return new AuthResult(false, message);
    }

    public boolean isSuccess(){ return success; }

    public String getMessage(){ return message; }
}
