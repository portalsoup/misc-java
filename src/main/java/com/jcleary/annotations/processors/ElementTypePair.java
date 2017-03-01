package com.jcleary.annotations.processors;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

/**
 * Created by portalsoup on 2/28/17.
 */
public class ElementTypePair  {

    final TypeElement element;
    final DeclaredType type;

    public ElementTypePair(TypeElement element, DeclaredType type) {
        this.element = element;
        this.type = type;
    }
}
