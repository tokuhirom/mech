package me.geso.mech;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import me.geso.tinyvalidator.ConstraintViolation;
import me.geso.tinyvalidator.Validator;

/**
 * Validate JSON with tinyvalidator.
 */
public class TinyValidatorJsonValidator implements JsonValidator {
	private final Validator validator = new Validator();

	@Override
	public Optional<String> validate(Object bean) {
		List<ConstraintViolation> violations = this.validator.validate(bean);
		if (violations.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(violations.stream()
					.map(it -> it.getName() + " " + it.getMessage())
					.collect(Collectors.joining("\n")));
		}
	}
}
