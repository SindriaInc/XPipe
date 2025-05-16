package org.cmdbuild.services.soap.operation;

import static java.lang.String.format;
import static java.util.regex.Pattern.quote;
import static org.apache.commons.lang3.StringUtils.isNotBlank; 
import static org.cmdbuild.data.store.Storables.storableOf;
import static org.cmdbuild.logic.mapping.json.Constants.FilterOperator.EQUAL;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.ATTRIBUTE_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.OPERATOR_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.VALUE_KEY;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

import org.cmdbuild.auth.login.AuthenticationStore;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.dao.entrytype.attributetype.CMAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.ForwardingAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.NullAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.data.store.Storable;
import org.cmdbuild.logic.mapping.json.JsonFilterHelper;
import org.cmdbuild.logic.mapping.json.JsonFilterHelper.FilterElementGetter;
import org.cmdbuild.auth.user.UserType;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl.DaoQueryOptionsImplBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.slf4j.LoggerFactory;

class GuestFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String METADATA_PORTLET_USER = "org.cmdbuild.portlet.user.id";
    private static final String CLASS_ATTRIBUTE_SEPARATOR = ".";

    private static final Storable METADATA_PORTLET_USER_STORABLE = storableOf(METADATA_PORTLET_USER);

    private final UserType userType;
    private final LoginUserIdentity login;
    private final DaoService dataView;

    public GuestFilter(AuthenticationStore authenticationStore, DaoService dataView) {
        this.userType = authenticationStore.getType();
        this.login = authenticationStore.getLogin();
        this.dataView = dataView;
    }

    public DaoQueryOptions apply(final Classe target, final DaoQueryOptions queryOptions) {
        DaoQueryOptionsImplBuilder builder = DaoQueryOptionsImpl.copyOf(queryOptions);
        if (userType == UserType.APPLICATION) {
            logger.warn("cannot apply filter, user is not guest");
        } else {
//			final MetadataStoreFactory metadataStoreFactory = applicationContext().getBean(MetadataStoreFactory.class);
            for (final Attribute attribute : target.getServiceAttributes()) {
                logger.debug("trying filtering attribute '{}'", attribute.getName());
                attribute.getType().accept(new ForwardingAttributeTypeVisitor() {

                    private final CMAttributeTypeVisitor DELEGATE = NullAttributeTypeVisitor.getInstance();

//					private final Store<Metadata> _store = metadataStoreFactory.storeForAttribute(attribute);
//					private final Store<Metadata> store = Stores.nullOnNotFoundRead(_store);
                    @Override
                    protected CMAttributeTypeVisitor delegate() {
                        return DELEGATE;
                    }

                    @Override
                    public void visit(final ReferenceAttributeType attributeType) {
                        /*
						 * absolutely ugly! QueryOptions needs to be refactored
						 * using Java objects instead of JSON
                         */
                        try {
                            final JSONObject original = CmFilterUtils.toJsonObject(queryOptions.getFilter());
                            final JSONObject originalWithAddidion = originalWithAddition(original, attribute);
                            builder.and(CmFilterUtils.fromJson(originalWithAddidion));
                        } catch (final Exception e) {
                            logger.warn("error applying guest filter, leaving original one", e);
                        }
                    }

                    private JSONObject originalWithAddition(final JSONObject original, final Attribute attribute)
                            throws JSONException {
                        final Entry<String, String> classAndAttribute = classAndAttributeOrNull(attribute);
                        if (classAndAttribute == null) {
                            return original;
                        }

                        final FilterElementGetter filterElementGetter = new FilterElementGetter() {

                            @Override
                            public boolean hasElement() {
                                return true;
                            }

                            @Override
                            public JSONObject getElement() throws JSONException {
                                final JSONArray jsonValues = new JSONArray();
                                jsonValues.put(idValue(classAndAttribute));

                                final JSONObject jsonObject = new JSONObject();
                                jsonObject.put(ATTRIBUTE_KEY, attribute.getName());
                                jsonObject.put(OPERATOR_KEY, EQUAL);
                                jsonObject.put(VALUE_KEY, jsonValues);

                                return jsonObject;
                            }

                            private Long idValue(final Entry<String, String> classAndAttribute) {
                                final Classe targetClass = dataView.getClasse(classAndAttribute.getKey());
                                final String attributeName = classAndAttribute.getValue();
                                final String attributeValue = login.getValue();
//								final Long id = dataView.select(attribute(targetClass, attributeName)) //
                                final Long id = dataView.selectAll()
                                        .from(targetClass) //
                                        .where(attributeName, EQ, attributeValue)
                                        //                                                condition( //
                                        //												attribute(targetClass, attributeName), //
                                        //												eq(attributeValue))) //
                                        .limit(1) //
                                        //										.skipDefaultOrdering() //
                                        //										.run() //
                                        //										.getOnlyRow() //
                                        //										.getCard(targetClass) //
                                        .getCard().getId();
                                return id;
                            }

                        };
                        return new JsonFilterHelper(original).merge(filterElementGetter);
                    }

                    private Entry<String, String> classAndAttributeOrNull(final Attribute attribute) {
                        logger.debug("parsing metadata  for attribute '{}'", attribute.getName());
                        String value = attribute.getMetadata().get(METADATA_PORTLET_USER);
//						Metadata metadata = applicationContext().getBean(MetadataRepository.class).getOrNull(attribute, METADATA_PORTLET_USER);
//						return classAndAttributeOrNull(metadata);
                        return (value == null) ? null : classAndAttributeOrNull(value);
                    }

//					private Entry<String, String> classAndAttributeOrNull(final Metadata metadata) {
//						logger.debug("parsing metadata '{}'", metadata);
//						return (metadata == null) ? null : classAndAttributeOrNull(metadata.getValue());
//					}
                    private Entry<String, String> classAndAttributeOrNull(final String metadataValue) {
                        logger.debug("parsing metadata value '{}'", metadataValue);
                        final Entry<String, String> entry;
                        if (isNotBlank(metadataValue) && metadataValue.contains(CLASS_ATTRIBUTE_SEPARATOR)) {
                            final String[] tokens = metadataValue.split(quote(CLASS_ATTRIBUTE_SEPARATOR));
                            entry = new SimpleImmutableEntry<String, String>(tokens[0], tokens[1]);
                            logger.debug(format("extracted attribute name is '%s'", entry));
                        } else {
                            logger.debug(format("cannot extract attribute name from '%s'", metadataValue));
                            entry = null;
                        }
                        return entry;
                    }

                });
            }
        }
        return builder.build();
    }

}
