package com.javanauta.agendadortarefas.infrastructure.exceptions;

public class ResounceNotFoundException extends RuntimeException{
    public ResounceNotFoundException(String message){
        super(message);
    }
    public ResounceNotFoundException(String message, Throwable throwable){
        super(message, throwable);
    }
}
