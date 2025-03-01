/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.common.serializationhelpers.card;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dms.DmsService;
import static org.cmdbuild.email.Email.EMAIL_ATTR_CARD;
import static org.cmdbuild.email.Email.EMAIL_CLASS_NAME;
import org.cmdbuild.service.rest.common.serializationhelpers.DataSerializerEnhancer;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

/**
 * Decorator to add stats on number of attachments and emails for a card.
 * 
 * @author afelice
 */
public class CardSerializerEnhancer_Stats extends DataSerializerEnhancer<Card> {

    private final DmsService dmsService;
    private final DaoService dao;
    
    public CardSerializerEnhancer_Stats(boolean condition, DmsService dmsService, DaoService dao) {
        super(condition);
        this.dmsService = checkNotNull(dmsService);
        this.dao = checkNotNull(dao);
    }
    
    @Override
    public void apply(FluentMap<String, Object> serialization, Card card) {
        serialization.put(
                "_attachment_count", dmsService.getCardAttachmentCountSafe(card),
                "_email_count", dao.selectCount().from(EMAIL_CLASS_NAME).where(EMAIL_ATTR_CARD, EQ, card.getId()).getCount()
        );    
    }
    
}
