package com.jcleary.annotations;


/**
 * Created by portalsoup on 2/28/17.
 */
public @interface Hack {

    String expirationDate() default "";

    String expirationDateFormat() default "MM/dd/yyyy";

    boolean errorOnExpiration() default false;
}
