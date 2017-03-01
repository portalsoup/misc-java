package com.jcleary.annotations.processors;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

/**
 * Created by portalsoup on 2/28/17.
 */
public class SourceFile {
    private final String fileName;
    private final Collection<String> content;

    public SourceFile(String filename, String... content) {
        this.fileName = filename;
        this.content = ImmutableList.copyOf(content);
    }

    public String getFileName() {
        return fileName;
    }

    public Collection<String> getContent() {
        return content;
    }
}
