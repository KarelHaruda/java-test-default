package com.etnetera.hr.exceptions;

import com.etnetera.hr.data.ValidationResult;

public class InvalidObjectException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final ValidationResult validationResult;
	
	public InvalidObjectException(ValidationResult result) {
		this.validationResult = result;
	}	
	
	public ValidationResult getValidationResult() {
		return validationResult;
	}
}
