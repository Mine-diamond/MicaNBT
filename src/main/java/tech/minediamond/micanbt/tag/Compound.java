package tech.minediamond.micanbt.tag;

public abstract class Compound extends Tag implements Iterable<Tag> {

    public Compound(String name) {
        super(name);
    }

    public abstract boolean isEmpty();
}
