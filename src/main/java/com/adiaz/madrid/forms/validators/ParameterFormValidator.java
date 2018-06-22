package com.adiaz.madrid.forms.validators;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ParameterFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return ParameterFormValidator.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "key", "field_required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "value", "field_required");
    }
}
