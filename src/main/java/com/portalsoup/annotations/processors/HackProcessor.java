package com.portalsoup.annotations.processors;

import com.portalsoup.annotations.Hack;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * Created by portalsoup on 2/28/17.
 */
@SupportedAnnotationTypes("com.portalsoup.annotations.Hack")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class HackProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (Element element : roundEnv.getElementsAnnotatedWith(Hack.class)) {
            Hack hack = element.getAnnotation(Hack.class);

            DateTime expirationDate = DateTime.parse(
                    hack.expirationDate(),
                    DateTimeFormat.forPattern(
                            hack.expirationDateFormat()
                    )
            );

            if (expirationDate.isBeforeNow()) {
                String message = "Hack found";
                if (hack.errorOnExpiration()) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
                } else {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, message);
                }
            }
        }
        return true;
    }
}
