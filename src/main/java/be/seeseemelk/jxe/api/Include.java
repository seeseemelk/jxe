package be.seeseemelk.jxe.api;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(CLASS)
@Target(TYPE)
@Repeatable(Includes.class)
public @interface Include
{
	String value();
}
