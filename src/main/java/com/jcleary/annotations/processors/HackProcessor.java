package com.jcleary.annotations.processors;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Optional;
import java.util.Set;

/**
 * Created by portalsoup on 2/28/17.
 */
@SupportedAnnotationTypes("com.jcleary.annotations.Hack")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class HackProcessor extends AbstractProcessor {

    private ElementTypePair hackType;

    private ExecutableElement expirationDate;
    private ExecutableElement expirationDateFormat;
    private ExecutableElement errorOnExpiration;

    // convenience delegations
    private Types typeUtils() {
        return processingEnv.getTypeUtils();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        hackType = getType("com.jcleary.annotations.Hack");

        expirationDate = getMethod(hackType.element, "expirationDate");
        expirationDateFormat = getMethod(hackType.element, "expirationDateFormat");
        errorOnExpiration = getMethod(hackType.element, "errorOnExpiration");

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        TypeElement hackTypeElement = processingEnv.getElementUtils().getTypeElement("com.jcleary.annotations.Hack");
        DeclaredType hackType = processingEnv.getTypeUtils().getDeclaredType(hackTypeElement);
        Set<? extends Element> hackAnnotated = roundEnv.getElementsAnnotatedWith(hackTypeElement);
        for (Element type : hackAnnotated) {
            checkHack(type);
        }
        return true;
    }

    private AnnotationMirror getAnnotationMirror(Element element, ElementTypePair etPair) {
        for (AnnotationMirror aMirror : element.getAnnotationMirrors()) {
            if (typeUtils().isSameType(aMirror.getAnnotationType(), etPair.type)) {
                return aMirror;
            }
        }
        return null;
    }

    private void checkHack(Element type) {
        AnnotationMirror hackMirror = getAnnotationMirror(type, hackType);

        Optional<String> maybeExpirationDate = checkHackExpirationDate(hackMirror);
        Optional<String> maybeExpirationDateFormat = checkHackExpirationDateFormat(hackMirror);
        Optional<Boolean> maybeErrorOnExpiration = checkHackErrorOnExpiration(hackMirror);

        Optional<DateTime> maybeHasExpiration = Optional.empty();

        // If an expiration date is declared, get the expected format and create a new DateTime
        if (maybeExpirationDate.isPresent()) {
            String format;
            if (maybeExpirationDateFormat.isPresent()) {
                format = maybeExpirationDateFormat.get();
            } else {
                format = (String) this.expirationDateFormat.getDefaultValue().getValue();
            }

            maybeHasExpiration = Optional.ofNullable(
                    DateTime.parse(
                            maybeExpirationDate.get(),
                            DateTimeFormat.forPattern(format)
                    )
            );
        }

        if (maybeHasExpiration.isPresent()) {
            if (maybeHasExpiration.get().isBeforeNow()) {
                String message = "An expired hack was found!";
                if (maybeErrorOnExpiration.isPresent() && maybeErrorOnExpiration.get()) {
                    printMessage(Diagnostic.Kind.ERROR, message, type);
                    return;
                } else {
                    printMessage(Diagnostic.Kind.WARNING, message, type);
                    return;
                }
            }
        }
        printMessage(Diagnostic.Kind.NOTE, "A hack was found!", type);
    }

    private void printMessage(Diagnostic.Kind kind, String message, Element e) {
        processingEnv.getMessager().printMessage(kind, message, e);

    }
    private Optional<String> checkHackExpirationDate(AnnotationMirror hackAnnotation) {
        try {
            return Optional.ofNullable((String) hackAnnotation.
                    getElementValues()
                    .get(expirationDate)
                    .getValue());
        } catch (NullPointerException npe) {
            return Optional.empty();
        }
    }

    private Optional<String> checkHackExpirationDateFormat(AnnotationMirror hackAnnotation) {
        try {
            return Optional.ofNullable((String) hackAnnotation
                    .getElementValues()
                    .get(expirationDateFormat)
                    .getValue());
        } catch (NullPointerException npe) {
            return Optional.empty();
        }
    }

    private Optional<Boolean> checkHackErrorOnExpiration(AnnotationMirror hackAnnotation) {
        try {
            return Optional.ofNullable((boolean) hackAnnotation
                    .getElementValues()
                    .get(errorOnExpiration)
                    .getValue());
        } catch (NullPointerException npe) {
            return Optional.empty();
        }
    }


    /**
     * Get the {@link TypeElement} and {@link DeclaredType} for a class
     * @param className the name of the class
     * @return the {@link TypeElement} and {@link DeclaredType} representing the class with name {@code className}
     */
    private ElementTypePair getType(String className) {
        TypeElement typeElement = processingEnv.getElementUtils().getTypeElement(className);
        DeclaredType declaredType = typeUtils().getDeclaredType(typeElement);
        return new ElementTypePair(typeElement, declaredType);
    }

    /**
     * Get the method in {@code element} named {@code methodName}
     * @param element an element representing an entity with a method name of {@code methodName}
     * @param methodName the method name
     * @return the method
     */
    private ExecutableElement getMethod(Element element, String methodName) {
        for (ExecutableElement executable: ElementFilter.methodsIn(element.getEnclosedElements())) {
            if (executable.getSimpleName().toString().equals(methodName)) {
                return executable;
            }
        }
        throw new IllegalArgumentException("no element named " + methodName + " + in element");
    }
}