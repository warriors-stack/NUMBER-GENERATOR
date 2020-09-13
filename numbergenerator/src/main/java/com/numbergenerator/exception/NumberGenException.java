package com.numbergenerator.exception;

import org.springframework.http.ResponseEntity.BodyBuilder;

public class NumberGenException extends Exception{

	public NumberGenException() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	public NumberGenException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}



	public NumberGenException(BodyBuilder badRequest) {
		super();
	}



	
}
