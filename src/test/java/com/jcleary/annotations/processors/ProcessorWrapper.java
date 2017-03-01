package com.jcleary.annotations.processors;

import org.mockito.Mockito;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static org.mockito.Mockito.when;

/**
 * Created by portalsoup on 2/28/17.
 */
public class ProcessorWrapper implements Processor {

    private final Processor wrapped;
    private final Messager mockMessager;

    public ProcessorWrapper(Processor processor, Messager mockMessager) {
        this.wrapped = processor;
        this.mockMessager = mockMessager;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return wrapped.getSupportedOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return wrapped.getSupportedAnnotationTypes();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return wrapped.getSupportedSourceVersion();
    }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        ProcessingEnvironment spy = Mockito.spy(processingEnv);
        when(spy.getMessager()).thenReturn(mockMessager);
        wrapped.init(spy);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return wrapped.process(annotations, roundEnv);
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return wrapped.getCompletions(element, annotation, member, userText);
    }
}
