package pers.towdium.just_enough_calculation.util.annotation;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Author: Towdium
 * Date:   2016/6/28.
 */
@Target(value = {TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE, TYPE_PARAMETER, TYPE_USE })
public @interface Constant {
}
