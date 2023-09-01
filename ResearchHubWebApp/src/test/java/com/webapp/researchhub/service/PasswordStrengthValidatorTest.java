package com.webapp.researchhub.service;

import com.webapp.researchhub.validator.PasswordStrengthValidator;
import org.junit.jupiter.api.Test;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * Class PasswordStrengthValidatorTest uses Spring test framework
 * Used to test 2 passwords for validation one following the rules/conditions one doesn't
 * Use assertTrue or assertFalse statement to check if passwords considered valid or not
 * */

@SpringBootTest
public class PasswordStrengthValidatorTest {

    @Test
    public void TestPasswordStrengthValidator(){
        PasswordStrengthValidator psv = new PasswordStrengthValidator();
        PasswordValidator validator = psv.getValidator();

        RuleResult result = validator.validate(new PasswordData("password"));
        assertFalse("password test failed with  weak password", result.isValid() );

        result = validator.validate(new PasswordData("Password123!"));
        assertTrue("password test failed with  weak password", result.isValid() );
    }

}
