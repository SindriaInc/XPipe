package org.cmdbuild.services.soap.test;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.lookup.LookupValueImpl;
import org.cmdbuild.services.soap.types.Filter;
import org.cmdbuild.services.soap.types.Query;
import org.cmdbuild.services.soap.utils.SoapToJsonUtilsService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.lookup.LookupRepository;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.lookup.LookupValue;

public class SoapToJsonUtilsTest {

	private static final String LOOKUP_ATTRIBUTE_NAME = "LookupAttribute";
	private static final String LOOKUP_TYPE = "foo";

	private static final String EQUALS_OPERATOR = "EQUALS";

	private Attribute attribute;
	private Classe targetClass;
	private LookupRepository lookupStore;

	private SoapToJsonUtilsService soapToJsonUtilsService;

	@Before
	public void setUp() throws Exception {
		attribute = mock(Attribute.class);
		when(attribute.getType()) //
				.thenReturn((CardAttributeType) new LookupAttributeType(LOOKUP_TYPE));

		targetClass = mock(Classe.class);
		when(targetClass.getAttributeOrNull(LOOKUP_ATTRIBUTE_NAME)) //
				.thenReturn(attribute);

		lookupStore = mock(LookupRepository.class);

		soapToJsonUtilsService = new SoapToJsonUtilsService();
	}

	@Test
	public void lookupValueHandledAsId() throws Exception {
		// given
		Query query = queryForLookupAttribute(LOOKUP_ATTRIBUTE_NAME, "42");

		// when
		JSONObject filter = soapToJsonUtilsService.createJsonFilterFrom(query, null, null, targetClass, lookupStore);

		// then
		assertThat(
				filter.toString(),
				equalTo(jsonOf(
						"{attribute: {simple: {attribute: \"LookupAttribute\", operator: \"equal\", value: [\"42\"]}}}")
						.toString()));
	}

//	@Test
//	public void lookupValueHandledAsDescription() throws Exception {
//		// given
//		Query query = queryForLookupAttribute(LOOKUP_ATTRIBUTE_NAME,
//				"Answer to the Ultimate Question of Life, the Universe, and Everything");
//		when(lookupStore.getAllByType(LookupType.newInstance() //
//				.withName(LOOKUP_TYPE) //
//				.build())) //
//				.thenReturn(asList( //
//						lookup(24L, "bar"), //
//						lookup(42L, "Answer to the Ultimate Question of Life, the Universe, and Everything") //
//				));
//
//		// when
//		JSONObject filter = soapToJsonUtilsService.createJsonFilterFrom(query, null, null, targetClass, lookupStore);
//
//		// then
//		assertThat(
//				filter.toString(),
//				equalTo(jsonOf(
//						"{attribute: {simple: {attribute: \"LookupAttribute\", operator: \"equal\", value: [\"42\"]}}}")
//						.toString()));
//	}
//
//	@Test
//	public void lookupValueNotFound() throws Exception {
//		// given
//		Query query = queryForLookupAttribute(LOOKUP_ATTRIBUTE_NAME,
//				"Answer to the Ultimate Question of Life, the Universe, and Everything");
//		when(lookupStore.getAllByType(LookupType.newInstance() //
//				.withName(LOOKUP_TYPE) //
//				.build())) //
//				.thenReturn(asList(lookup(24L, "bar")));
//
//		// when
//		JSONObject filter = soapToJsonUtilsService.createJsonFilterFrom(query, null, null, targetClass, lookupStore);
//
//		// then
//		assertThat(
//				filter.toString(),
//				equalTo(jsonOf(
//						"{attribute: {simple: {attribute: \"LookupAttribute\", operator: \"equal\", value: [\"0\"]}}}")
//						.toString()));
//	}

	private static Query queryForLookupAttribute(String name, String value) {
		Query query = new Query();
		query.setFilter(new Filter() {
			{
				setName(name);
				setOperator(EQUALS_OPERATOR);
				setValue(asList(value));
			}
		});
		return query;
	}

	private static LookupValue lookup(Long id, String description) {
		return LookupValueImpl.builder() //
				.withId(id) //
				.withDescription(description) //
				.build();
	}

	private static JSONObject jsonOf(String source) throws Exception {
		return new JSONObject(source);
	}

}
