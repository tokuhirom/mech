package me.geso.mech;

public class JsonValidatorViolationException extends Exception {
	private static final long serialVersionUID = 1L;

	public JsonValidatorViolationException(String message) {
		super(message);
	}
}
