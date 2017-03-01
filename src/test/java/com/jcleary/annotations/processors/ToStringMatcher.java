package com.jcleary.annotations.processors;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import javax.lang.model.element.Element;

/**
 * Created by portalsoup on 2/28/17.
 */
public class ToStringMatcher<T> extends BaseMatcher<T> {
    public static <T> Matcher<T> hasToString(String elementName, Class<T> elementClass) {
        return new ToStringMatcher<T>(elementName);
    }

    private final String expectedToString;

    public ToStringMatcher(String expectedToString) {
        this.expectedToString = expectedToString;
    }

    @Override
    public boolean matches(Object obj) {
        return obj != null && expectedToString.equals(obj.toString());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("An object with a toString of " + expectedToString);
    }
}
