package org.cmdbuild.services.soap.operation;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

import org.cmdbuild.lookup.LookupValueImpl;
import org.cmdbuild.lookup.LookupService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.services.soap.types.Lookup;

public class LookupLogicHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LookupService lookupService;

    public LookupLogicHelper(LookupService lookupService) {
        this.lookupService = checkNotNull(lookupService);
    }

    public long createLookup(Lookup lookup) {
        LookupValueImpl lookupDto = transform(lookup);
        return lookupService.createOrUpdateLookup(lookupDto).getId();
    }

    public boolean updateLookup(Lookup lookup) {
        LookupValueImpl lookupDto = transform(lookup);
        lookupService.createOrUpdateLookup(lookupDto);
        return true;
    }

    public boolean disableLookup(long id) {
//		logic.disableLookup(Long.valueOf(id));
        throw new UnsupportedOperationException("TODO");
//		return true;
    }

    public Lookup getLookupById(long id) {
        LookupValue lookup = lookupService.getLookup(id);
        return transform(lookup, true);
    }

    public Lookup[] getLookupListByCode(String type, String code, boolean parentList) {
        return getLookupListByAttribute(type, (LookupValue input) -> code.equals(input.getCode()), parentList);
    }

    public Lookup[] getLookupListByDescription(String type,
            String description, boolean parentList) {
        return getLookupListByAttribute(type, (LookupValue input) -> (description == null) || description.equals(input.getDescription()), parentList);
    }

    private Lookup[] getLookupListByAttribute(String type,
            AttributeChecker attributeChecker, boolean parentList) {
//		LookupType lookupType = LookupType.newInstance() //
//				.withName(type) //
//				.build();
        Iterable<LookupValue> lookupList = lookupService.getAllLookup(type);
        return from(lookupList) //
                .filter((LookupValue input) -> attributeChecker.check(input)) //
                .transform((LookupValue input) -> transform(input, parentList)) //
                .toArray(Lookup.class);
    }

    private LookupValueImpl transform(Lookup from) {
        return LookupValueImpl.builder() //
                .withType(lookupService.getLookupTypeCreateIfMissing(from.getType())) //
                .withCode(defaultIfEmpty(from.getCode(), EMPTY)) //
                .withId(from.getId()) //
                .withDescription(from.getDescription()) //
                .withNotes(from.getNotes()) //
                .withParentId(from.getParentId()) //
                .withIndex(from.getPosition()) //
                //				.withActiveStatus(true) //
                .build();
    }

    private Lookup transform(LookupValue from, boolean parentList) {
        logger.debug("serializing lookup '{}'", from);
        Lookup to = new Lookup();
        to.setId(from.getId());
        to.setCode(from.getCode());
        to.setDescription(from.getDescription());
        to.setNotes(from.getNotes());
        to.setType(from.getType().getName());
        to.setPosition(from.getIndex());
        if (from.hasParent()) {
            to.setParentId(from.getParentId());
        }
        if (parentList && from.hasParent()) {
            to.setParent(transform(lookupService.getLookup(from.getParentId()), true));
        }
        return to;
    }

    private static interface AttributeChecker {

        boolean check(LookupValue input);

    }
}
