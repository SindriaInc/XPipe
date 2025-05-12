package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.Collection;
import java.util.List;
import static org.cmdbuild.auth.grant.PrivilegeSubject.privilegeId;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_DOMAIN;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import static org.cmdbuild.dao.utils.DomainUtils.serializeDomainCardinality;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public interface Domain extends EntryType, ClassPermissions, DomainDefinition {

    static final String DOMAIN_ONE_TO_ONE = "1:1",
            DOMAIN_MANY_TO_ONE = "N:1",
            DOMAIN_ONE_TO_MANY = "1:N",
            DOMAIN_MANY_TO_MANY = "N:N";

    static final int DEFAULT_INDEX_VALUE = -1;
    
    static final String DOMAIN_SOURCE_CLASS_TOKEN = "sourceFilter";
    static final String DOMAIN_TARGET_CLASS_TOKEN = "targetFilter";

    Classe getSourceClass();

    Classe getTargetClass();

    Collection<Classe> getSourceClasses();

    Collection<Classe> getTargetClasses();

    @Override
    default String getDescription() {
        return firstNotBlank(getMetadata().getDescription(), getName());
    }

    @Override
    default String getSourceClassName() {
        return getSourceClass().getName();
    }

    @Override
    default String getTargetClassName() {
        return getTargetClass().getName();
    }

    default Collection<Classe> getSourceAndTargetClasses() {
        return set(getSourceClasses()).with(getTargetClasses());
    }

    @Override
    default void accept(CMEntryTypeVisitor visitor) {
        visitor.visit(this);
    }

    default String getDirectDescription() {
        return getMetadata().getDirectDescription();
    }

    default String getInverseDescription() {
        return getMetadata().getInverseDescription();
    }

    default DomainCardinality getCardinality() {
        return getMetadata().getCardinality();
    }

    default String getSourceCardinality() {
        return serializeDomainCardinality(getCardinality()).split(":")[0];
    }

    default String getTargetCardinality() {
        return serializeDomainCardinality(getCardinality()).split(":")[1];
    }

    default boolean hasCardinality(DomainCardinality cardinality) {
        return equal(cardinality, getCardinality());
    }

    default List<Attribute> getReferenceAttributesWithFilter(RelationDirection direction) {
        return list(this.getThisDomainWithDirection(direction).getTargetClasses()
                .stream()
                .flatMap(c -> c.getAllAttributes().stream())
                .filter(
                        a -> a.isOfType(REFERENCE) 
                             && equal(a.getType().as(ReferenceAttributeType.class).getDomainName(), this.getName()) 
                             && equal(a.getType().as(ReferenceAttributeType.class).getDirection(), direction.inverse())
                             && (a.hasFilter() || a.getMetadata().isUseDomainFilter()) // handle Class Attribute without an explicit filter
                ));
    }
    
    default boolean isMasterDetail() {
        return getMetadata().isMasterDetail();
    }

    default String getMasterDetailDescription() {
        return getMetadata().getMasterDetailDescription();
    }

    default String getMasterDetailFilter() {
        return getMetadata().getMasterDetailFilter();
    }

    default Collection<String> getMasterDetailAggregateAttrs() {
        return getMetadata().getMasterDetailAggregateAttrs();
    }

    default Collection<String> getMasterDetailDisabledCreateAttrs() {
        return getMetadata().getMasterDetailDisabledCreateAttrs();
    }

    @Override
    default boolean hasHistory() {
        return true;
    }

    default Collection<String> getDisabledSourceDescendants() {
        return getMetadata().getDisabledSourceDescendants();
    }

    default Collection<String> getDisabledTargetDescendants() {
        return getMetadata().getDisabledTargetDescendants();
    }

    default boolean isDisabledSourceDescendant(Classe classe) {
        return getDisabledSourceDescendants().contains(classe.getName());
    }

    default boolean isDisabledTargetDescendant(Classe classe) {
        return getDisabledTargetDescendants().contains(classe.getName());
    }

    /**
     * return optional ordering for domains, on the side of
     * {@link #getSourceClass()} (used to order instances of
     * {@link #getSourceClass()} as seen in the detail (relations) view of a
     * single instance of {@link #getTargetClass()})
     *
     * @return ordering, or {@link DEFAULT_INDEX_VALUE} if not set
     */
    default int getIndexForSource() {
        return getMetadata().getIndexForSource();
    }

    /**
     * return optional ordering for domains, on the side of
     * {@link #getTargetClass()} (used to order instances of
     * {@link #getTargetClass()} as seen in the detail (relations) view of a
     * single instance of {@link #getSourceClass()})
     *
     * @return ordering, or {@link DEFAULT_INDEX_VALUE} if not set
     */
    default int getIndexForTarget() {
        return getMetadata().getIndexForTarget();
    }

    @Override
    default Long getOid() {
        return getId();
    }

    @Override
    default String getPrivilegeId() {
        return privilegeId(PS_DOMAIN, getId());
    }

    @Override
    default EntryTypeType getEtType() {
        return ET_DOMAIN;
    }

    @Override
    default boolean isActive() {
        return EntryType.super.isActive();
    }

    default Classe getReferencedClass(Attribute attribute) {
        checkArgument(attribute.isOfType(REFERENCE));
        ReferenceAttributeType refAttribType = (ReferenceAttributeType) attribute.getType(); 
        
        return Domain.this.getReferencedClass(refAttribType);
    }
        
    default Classe getReferencedClass(ReferenceAttributeType refAttribType) {        
        return switch (refAttribType.getDirection()) {
            case RD_DIRECT ->
                getTargetClass();
            case RD_INVERSE ->
                getSourceClass();
            default ->
                throw new IllegalArgumentException("unsupported domain direction = " + refAttribType.getDirection());
        };
    }    
    
    /**
     * 
     * @param attribute
     * @return label <code>sourceClass</code> or <code>targetClass</code> to indicate domain edge to consider for reference filter
     */
    default String getReferencedClassToken(ReferenceAttributeType attribute) {
        return switch (attribute.getDirection()) {
            case RD_DIRECT ->
                DOMAIN_TARGET_CLASS_TOKEN;
            case RD_INVERSE ->
                DOMAIN_SOURCE_CLASS_TOKEN;
            default ->
                throw new IllegalArgumentException("unsupported domain direction = " + attribute.getDirection());
        };
    }

    default String getReferencedClassToken(Attribute attribute) {
        checkArgument(attribute.isOfType(REFERENCE));
        return getReferencedClassToken((ReferenceAttributeType) attribute.getType());
    }
    
    default String getSourceClassReferenceFilter() {
        return getMetadata().getClassReferenceFilters().get(DOMAIN_SOURCE_CLASS_TOKEN);
    }
    
    default String getTargetClassReferenceFilter() {
        return getMetadata().getClassReferenceFilters().get(DOMAIN_TARGET_CLASS_TOKEN);
    }

    default boolean isDomainForClasse(Classe classe) {
        return isDomainForSourceClasse(classe) || isDomainForTargetClasse(classe);
    }

    default boolean isDomainForSourceClasse(Classe classe) {
        return getSourceClass().equalToOrAncestorOf(classe) && !getDisabledSourceDescendants().contains(classe.getName());
    }

    default boolean isDomainForTargetClasse(Classe classe) {
        return getTargetClass().equalToOrAncestorOf(classe) && !getDisabledTargetDescendants().contains(classe.getName());
    }

    /**
     * @param classe
     * @return reoriented domain for this classe (so that this classe is on the
     * 'source' side); if this classe is both a valid source and target, return
     * two records (one for each direction); otherwise return one record
     */
    default List<Domain> getThisDomainDirectAndOrReversedForClass(Classe classe) {
        List<Domain> list = list();
        if (isDomainForSourceClasse(classe)) {
            list.add(this);
        }
        if (isDomainForTargetClasse(classe)) {
            list.add(ReverseDomain.of(this));
        }
        return checkNotEmpty(list, "this domain = %s is not a valid domain for class = %s", this, classe);
    }

    default List<Domain> getThisDomainDirectAndOrReversedForTargetClass(Classe classe) {
        List<Domain> list = list();
        if (isDomainForTargetClasse(classe)) {
            list.add(this);
        }
        if (isDomainForSourceClasse(classe)) {
            list.add(ReverseDomain.of(this));
        }
        return checkNotEmpty(list, "this domain = %s is not a valid domain for class = %s", this, classe);
    }

    default Domain getThisDomainForTargetClass(Classe classe) {
        return getOnlyElement(getThisDomainDirectAndOrReversedForTargetClass(classe));
    }

    default Domain getThisDomainWithDirection(RelationDirection direction) {
        return switch (direction) {
            case RD_DIRECT ->
                this;
            case RD_INVERSE ->
                ReverseDomain.of(this);
            default ->
                throw new UnsupportedOperationException();
        };
    }

    default Attribute getIdObjAttrAsFkAttr(String name) {
        return switch (name) {
            case ATTR_IDOBJ1 ->
                AttributeImpl.builder().withOwner(this).withName(name).withType(new ForeignKeyAttributeType(getSourceClass())).build();
            case ATTR_IDOBJ2 ->
                AttributeImpl.builder().withOwner(this).withName(name).withType(new ForeignKeyAttributeType(getTargetClass())).build();
            default ->
                throw new IllegalArgumentException("invalid attr name = " + name);
        };
    }

    default boolean hasDomainKeyAttrs() {
        return getAllAttributes().stream().anyMatch(Attribute::isDomainKey);
    }
}
