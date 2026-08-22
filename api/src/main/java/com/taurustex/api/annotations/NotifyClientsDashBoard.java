package com.taurustex.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // S'applique sur les méthodes
@Retention(RetentionPolicy.RUNTIME)
public @interface NotifyClientsDashBoard {
    String topic();
}