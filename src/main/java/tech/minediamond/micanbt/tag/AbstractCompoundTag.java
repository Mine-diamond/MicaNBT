package tech.minediamond.micanbt.tag;

public abstract class AbstractCompoundTag extends Tag implements Iterable<Tag> {

    public AbstractCompoundTag(String name) {
        super(name);
    }

    public abstract void put(Tag tag);

    public abstract boolean isEmpty();
}
