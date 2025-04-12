package com.devumut.DearDiary.exceptions;

public class DiaryAlreadyExistException extends RuntimeException{
    public DiaryAlreadyExistException(String message){
        super(message);
    }
}
