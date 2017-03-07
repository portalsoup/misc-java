package com.jcleary.annotations.processors;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;

import static javax.tools.Diagnostic.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by portalsoup on 2/28/17.
 */
public class HackProcessorTest {

    private Messager mockMessager;
    private ProcessorWrapper processor;
    private Compiler compiler;

    @BeforeMethod
    public void setup() throws Exception {
        mockMessager = mock(Messager.class);
        processor = new ProcessorWrapper(new HackProcessor(), mockMessager);
        compiler = new Compiler();
    }

    @Test
    public void testHackNoExpiration() throws Exception {
        SourceFile[] sourceFiles = {
            new SourceFile("NoExpirationHack.java",
                    "@com.jcleary.annotations.Hack",
                    "public class NoExpirationHack {}")
        };

        assertThat(compiler.compileWithProcessor(processor, sourceFiles), equalTo(true));
        verify(mockMessager, times(1)).printMessage(
                Matchers.eq(Kind.NOTE),
                Matchers.anyString(),
                Matchers.isA(Element.class));
    }

    @Test
    public void testHackExpiredWithError() throws Exception {
        SourceFile[] sourceFiles = {
                new SourceFile("NotExpiredHack.java",
                        "@com.jcleary.annotations.Hack(expirationDate = \"01/01/2015\", errorOnExpiration = true)",
                        "public class NotExpiredHack {}")
        };

        assertThat(compiler.compileWithProcessor(processor, sourceFiles), equalTo(true));
        verify(mockMessager, times(1)).printMessage(
                Matchers.eq(Kind.ERROR),
                Matchers.anyString(),
                Matchers.isA(Element.class));
    }

    @Test
    public void testHackExpiredWithWarning() throws Exception {
        SourceFile[] sourceFiles = {
                new SourceFile("NotExpiredHack.java",
                        "@com.jcleary.annotations.Hack(expirationDate = \"01/01/2015\")",
                        "public class NotExpiredHack {}")
        };

        assertThat(compiler.compileWithProcessor(processor, sourceFiles), equalTo(true));
        verify(mockMessager, times(1)).printMessage(
                Matchers.eq(Kind.WARNING),
                Matchers.anyString(),
                Matchers.isA(Element.class));
    }

    @Test
    public void testHackNotExpired() throws Exception {
        SourceFile[] sourceFiles = {
                new SourceFile("NotExpiredHack.java",
                        "@com.jcleary.annotations.Hack(expirationDate = \"01/01/2100\")",
                        "public class NotExpiredHack {}")
        };

        assertThat(compiler.compileWithProcessor(processor, sourceFiles), equalTo(true));
        verify(mockMessager, times(1)).printMessage(
                Matchers.eq(Kind.NOTE),
                Matchers.anyString(),
                Matchers.isA(Element.class));
    }

    @Test
    public void testHackWithFormat() throws Exception {
        SourceFile[] sourceFiles = {
                new SourceFile("NotExpiredHack.java",
                        "@com.jcleary.annotations.Hack(expirationDate = \"01-01-2015\", expirationDateFormat = \"MM-dd-yyyy\", errorOnExpiration = true)",
                        "public class NotExpiredHack {}")
        };

        assertThat(compiler.compileWithProcessor(processor, sourceFiles), equalTo(true));
        verify(mockMessager, times(1)).printMessage(
                Matchers.eq(Kind.ERROR),
                Matchers.anyString(),
                Matchers.isA(Element.class));
    }
}