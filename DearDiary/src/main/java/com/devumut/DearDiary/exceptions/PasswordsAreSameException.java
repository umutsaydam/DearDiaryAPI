package com.devumut.DearDiary.exceptions;

public class PasswordsAreSameException extends RuntimeException{
    public PasswordsAreSameException(String message){
        super(message);
    }
}
