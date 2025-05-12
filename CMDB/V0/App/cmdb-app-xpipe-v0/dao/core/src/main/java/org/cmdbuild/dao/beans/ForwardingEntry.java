package org.cmdbuild.dao.beans;

import java.time.ZonedDateTime;
import java.util.Map.Entry;

import org.cmdbuild.dao.entrytype.EntryType;

@Deprecated //TODO cleanup and remove
public abstract class ForwardingEntry extends ForwardingValueSet implements DatabaseRecord {

    /**
     * Usable by subclasses only.
     */
    protected ForwardingEntry() {
    }

    @Override
    protected abstract DatabaseRecord delegate();

    @Override
    public EntryType getType() {
        return delegate().getType();
    }

    @Override
    public Long getId() {
        return delegate().getId();
    }

    @Override
    public String getUser() {
        return delegate().getUser();
    }

    @Override
    public ZonedDateTime getBeginDate() {
        return delegate().getBeginDate();
    }

    @Override
    public ZonedDateTime getEndDate() {
        return delegate().getEndDate();
    }

    @Override
    public Iterable<Entry<String, Object>> getRawValues() {
        return delegate().getRawValues();
    }

}
