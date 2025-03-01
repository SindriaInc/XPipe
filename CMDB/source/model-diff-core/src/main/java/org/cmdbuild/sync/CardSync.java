/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;

/**
 *
 * @author afelice
 */
public interface CardSync {

    Card read(Classe classe, Long cardId);

    Card insert(Classe classe, Map<String, Object> values);

    Card update(Classe classe, Card card);

    void remove(Classe classe, Card card);
}
