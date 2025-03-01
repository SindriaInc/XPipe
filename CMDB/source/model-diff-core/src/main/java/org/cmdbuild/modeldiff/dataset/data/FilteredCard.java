/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import java.util.Set;
import org.cmdbuild.dao.beans.Card;

/**
 * {@link Card} with (in case) filtering on attributes.
 * 
 * <p>Used while serializing a {@link Card} to create <i>data</i> JSON, because a 
 * full {@link Card} is needed. A {@link CmCardAttributesData} wouldn't be enough
 * to pass it to {@link CardWsSerializationHelperv3}.
 * 
 * @author afelice
 */
public class FilteredCard {
    private final Card card;
    private final Set<String> selectedAttrs;

    public FilteredCard(Card card, Set<String> selectedAttrs) {
        this.card = card;
        this.selectedAttrs = selectedAttrs;
    }

    public Card getCard() {
        return card;
    }

    public Long getCardId() {
        return getCard().getId();
    }
    
    public Set<String> getSelectedAttrs() {
        return selectedAttrs;
    }
    
}
