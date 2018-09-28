package com.etnetera.hr.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

public class ValidationResult implements Serializable {
	private static final long serialVersionUID = 1L;

	public static enum EValidationResultSeverity {
		HINT, WARNING, ERROR
	}
	
	private List<ValidationResultItem> errors = new ArrayList<>();
	
	public class ValidationResultItem implements Serializable {
		private static final long serialVersionUID = 1L;

		private String field;
		private String message;
		private EValidationResultSeverity severity;
		
		public String getField() {
			return field;
		}
		
		public String getMessage() {
			return message;
		}
		
		public EValidationResultSeverity getSeverity() {
			return severity;
		}
		
		public ValidationResultItem(String field, String message, EValidationResultSeverity severity) {
			this.field = field;
			this.message = message;
			this.severity = severity;
		}
		
	}
	
	public ValidationResult() {
	}
	
	public void addValidationHint(String fieldName, String message) {
		errors.add(new ValidationResultItem(fieldName, message, EValidationResultSeverity.HINT));
	}
	
	public void addValidationWarning(String fieldName, String message) {
		errors.add(new ValidationResultItem(fieldName, message, EValidationResultSeverity.WARNING));
	}
	
	public void addValidationError(String fieldName, String message) {
		errors.add(new ValidationResultItem(fieldName, message, EValidationResultSeverity.WARNING));
	}
	
	@Transient
	public boolean isValid() {
		for (ValidationResultItem item: errors) {
			if (EValidationResultSeverity.ERROR.equals(item.getSeverity())) {}
			return false;
		}
		return true;
	}	
	
	public List<ValidationResultItem> getErrors() {
		return errors;
	}
}
