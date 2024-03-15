package org.cmdbuild.dao.beans;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Strings.nullToEmpty;
import java.util.Map.Entry;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;

import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.driver.PostgresService;

/**
 * a card 'handle', which includes card id, all data, and a dbdriver to operate
 * on card (save on database etc)
 */
@Deprecated //TODO cleanup and remove
public class CardDefinitionImpl extends DatabaseEntryImpl implements Card, CardDefinition {

    private static final Long NOT_EXISTING_CARD_ID = null;
    private Long currentId;

    private CardDefinitionImpl(PostgresService driver, Classe type, Long id) {
        super(driver, type, id);
    }

    @Override
    public CardDefinitionImpl set(String key, Object value) {
        setOnly(key, value);
        return this;
    }

    @Override
    public CardDefinitionImpl set(Iterable<? extends Entry<String, ? extends Object>> keysAndValues) {
        for (Entry<String, ? extends Object> entry : keysAndValues) {
            set(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public CardDefinitionImpl setUser(String user) {
        super.setUser(user);
        return this;
    }

    @Override
    public Classe getType() {
        return (Classe) super.getType();
    }

    @Override
    public String getCode() {
        return get(ATTR_CODE, String.class);
    }

    @Override
    public CardDefinition setCode(Object value) {
        return set(ATTR_CODE, value);
    }

    @Override
    public String getDescription() {
        return nullToEmpty(get(ATTR_DESCRIPTION, String.class));
    }

    @Override
    public CardDefinition setDescription(Object value) {
        return set(ATTR_DESCRIPTION, value);
    }

    @Override
    public CardDefinition setCurrentId(Long currentId) {
        this.currentId = currentId;
        return this;
    }

    @Override
    public Long getCurrentId() {
        return currentId;
    }

    @Override
    public CardDefinitionImpl save() {
        if (equal(getId(), NOT_EXISTING_CARD_ID)) {
            saveOnly();
        } else {
            updateOnly();
        }
        return this;
    }

    public static CardDefinitionImpl newInstance(PostgresService driver, Classe type) {
        return new CardDefinitionImpl(driver, type, NOT_EXISTING_CARD_ID);
    }

    public static CardDefinitionImpl newInstance(PostgresService driver, Classe type, Long id) {
        return new CardDefinitionImpl(driver, type, id);
    }

    @Override
    public String toString() {
        return "DBCard{" + "currentId=" + currentId + ",type=" + getType() + ",id=" + getId() + '}';
    }

}
