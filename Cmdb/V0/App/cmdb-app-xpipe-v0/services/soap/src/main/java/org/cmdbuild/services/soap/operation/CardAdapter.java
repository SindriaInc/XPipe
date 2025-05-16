package org.cmdbuild.services.soap.operation;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import net.sf.jasperreports.engine.util.ObjectUtils;
import org.cmdbuild.dao.core.q3.DaoService;

import org.cmdbuild.dao.entrytype.attributetype.CMAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.ForwardingAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.NullAttributeTypeVisitor;
import org.cmdbuild.services.soap.types.Attribute;
import org.cmdbuild.services.soap.types.Card;
import org.springframework.stereotype.Component;
import org.cmdbuild.lookup.LookupRepository;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType; 
import org.cmdbuild.lookup.LookupValue;

@Component
public class CardAdapter {

	private final DaoService dataView;
	private final LookupRepository lookupStore;

	public CardAdapter(final DaoService dataView, final LookupRepository lookupStore) {
		this.dataView = dataView;
		this.lookupStore = lookupStore;
	}

	public void resolveAttributes(final Card card) {
		final EntryType entryType = dataView.getClasse(card.getClassName());
		for (final Attribute attribute : card.getAttributeList()) {
			final String name = attribute.getName();
			final CardAttributeType<?> attributeType = entryType.getAttributeOrNull(name).getType();
			attributeType.accept(new ForwardingAttributeTypeVisitor() {

				private final CMAttributeTypeVisitor DELEGATE = NullAttributeTypeVisitor.getInstance();

				@Override
				protected CMAttributeTypeVisitor delegate() {
					return DELEGATE;
				}

				@Override
				public void visit(final LookupAttributeType attributeType) {
					final String value = attribute.getValue();
					final String lookupTypeName = attributeType.getLookupTypeName();
					if (isNotBlank(value) && isNumeric(value)) {
						if (existsLookup(lookupTypeName, Long.parseLong(value))) {
							attribute.setValue(value);
						}
					} else {
						final Iterable<LookupValue> lookupList = lookupStore.getAll();
						for (final LookupValue lookup : lookupList) {
//							if (lookup.active() && //
							if (lookup.getType().getName().equals(lookupTypeName) && //
									lookup.getDescription() != null && //
									ObjectUtils.equals(lookup.getDescription(), value)) {
								attribute.setValue(lookup.getId().toString());
								break;
							}
						}
					}
				}

				private boolean existsLookup(final String lookupTypeName, final Long lookupId) {
					final Iterable<LookupValue> lookupList = lookupStore.getAll();
					for (final LookupValue lookup : lookupList) {
						if (lookup.getType().getName().equals(lookupTypeName) && lookup.getId().equals(lookupId)) {
							return true;
						}
					}
					return false;
				}

			});
		}
	}

}