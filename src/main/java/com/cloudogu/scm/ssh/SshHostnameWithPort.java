package com.cloudogu.scm.ssh;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = SshHostnameWithPortValidator.class)
@Documented
public @interface SshHostnameWithPort {
  String message() default "value must be a valid hostname or ip address with an optional port";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
