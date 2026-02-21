package tech.minediamond.micanbt.path;

import tech.minediamond.micanbt.tag.Tag;

public interface PathToken {
    Tag navigate(Tag container);
    Object getAccessor(Tag container);
    String asString();
}
