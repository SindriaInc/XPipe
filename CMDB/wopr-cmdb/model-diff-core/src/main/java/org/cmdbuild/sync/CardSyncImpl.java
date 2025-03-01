/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import org.springframework.stereotype.Component;

/**
 * <b>Note</b>: can't be all based on ids, because if diffing different systems
 * ids won't match. The <i>name</i> (that is, the <code>code</code> in CMDBuild
 * model) is used as <i>distinguishing name</i>. A temporarily generated one has
 * to used for newly inserted data.
 *
 *
 * @author afelice
 */
@Component
public class CardSyncImpl implements CardSync {

    private final UserCardService cardService;

    public CardSyncImpl(UserCardService cardService) {
        this.cardService = checkNotNull(cardService);
    }

    @Override
    public Card read(Classe classe, Long cardId) {
        return cardService.getUserCard(classe.getName(), cardId);
    }

    @Override
    public Card insert(Classe classe, Map<String, Object> values) {
        return cardService.createCard(classe.getName(), values);
    }

    @Override
    public Card update(Classe classe, Card card) {
        return cardService.updateCard(classe.getName(), card.getId(), card.getAllValuesAsMap());
    }

    @Override
    public void remove(Classe classe, Card card) {
        cardService.deleteCard(classe.getName(), card.getId());
    }

}
