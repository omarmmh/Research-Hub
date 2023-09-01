package com.webapp.researchhub.validator;

import org.passay.*;

import javax.xml.validation.Validator;
import java.time.temporal.ValueRange;
import java.util.Arrays;

public class PasswordStrengthValidator {

    /**
     * Class named PasswordStrengthValidator.
     * Class contains single method getValidator which contains list of validation rules for a valid password
     *
     * returns validator
     *  */

    public PasswordValidator getValidator() {

         PasswordValidator validator = new PasswordValidator(Arrays.asList(
                // string must be between 8 and 30
                new LengthRule(8, 30),
                // string must have at least 1 uppercase character
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                // string must have at least 1 lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                // string must have at least 1 digit in
                new CharacterRule(EnglishCharacterData.Digit, 1),
                // String must have at least one symbol (special character)
                new CharacterRule(EnglishCharacterData.Special, 1),
                // no whitespace
                new WhitespaceRule()));

        return validator;
    }


}

