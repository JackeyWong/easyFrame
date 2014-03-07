package com.easy4j.easydao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

	/**
	 * the value for the column specified with the SQL AS clause. 
	 * If the SQL AS clause was not specified, 
	 * then the value is the name of the column.
	 * @return
	 */
	String value();

}
