package me.geso.mech;

import java.util.Optional;

@FunctionalInterface
public interface JsonValidator {
	/**
	 * Validate bean.
	 * 
	 * @param bean
	 * @return Optional.empty() if the bean is valid. Contains error message in
	 *         string if there is constraint violations.
	 */
	public Optional<String> validate(Object bean);
}
