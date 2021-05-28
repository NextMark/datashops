package com.bigdata.datashops.api.utils;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import com.bigdata.datashops.api.exception.ValidationException;

public class ValidatorUtil {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static void validate(Object obj) {
        Set<ConstraintViolation<Object>> set = validator.validate(obj, Default.class);
        if (set != null && set.size() > 0) {
            for (ConstraintViolation cv : set) {
                throw new ValidationException(cv.getMessage());
            }
        }
    }
}
