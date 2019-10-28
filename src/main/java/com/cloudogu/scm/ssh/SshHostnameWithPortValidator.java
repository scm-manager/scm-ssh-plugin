package com.cloudogu.scm.ssh;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SshHostnameWithPortValidator implements ConstraintValidator<SshHostnameWithPort, String> {

  private static final Pattern PATTERN = Pattern.compile("^(ssh://)?[^: /]+(:[0-9]+)?$");

  @Override
  public void initialize(SshHostnameWithPort constraintAnnotation) {
    // nothing to initialize
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    Matcher matcher = PATTERN.matcher(value);
    return matcher.matches();
  }
}
