package org.cmdbuild.model.data;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class EntryTypeTest {

	@Test
	public void shouldBeCreatedOnlyClassesWithoutSpecialCharacters() throws Exception {

		// given
		final String onlyLowerCase = "foo";
		final String lowerAndUpperCase = "FoO";
		final String lowerUpperAndDigits = "Foo98";
		final String nameWithUnderscore = "Foo_98";

		// when
		final EntryTypeBean onlyLowerCaseEntryType = EntryTypeBean.newClass().withName(onlyLowerCase).build();
		final EntryTypeBean lowerAndUpperCaseEntryType = EntryTypeBean.newClass().withName(lowerAndUpperCase).build();
		final EntryTypeBean lowerUpperAndDigitsEntryType = EntryTypeBean.newClass().withName(lowerUpperAndDigits).build();
		final EntryTypeBean withUnderscoreEntryType = EntryTypeBean.newClass().withName(nameWithUnderscore).build();

		// then
		assertNotNull(onlyLowerCaseEntryType);
		assertNotNull(lowerAndUpperCaseEntryType);
		assertNotNull(lowerUpperAndDigitsEntryType);
		assertNotNull(withUnderscoreEntryType);

	}

	@Test(expected = IllegalArgumentException.class)
	public void entryTypeNameWithSpecialCharacterShouldNotBeCreated() throws Exception {
		// given
		final String classNameWithSpaces = "foo<?%";

		// when
		final EntryTypeBean entryType = EntryTypeBean.newClass().withName(classNameWithSpaces).build();

		// then
		assertNull(entryType);
	}

	@Test(expected = IllegalArgumentException.class)
	public void entryTypeWithOnlyBlankSpacesNameShouldNotBeCreated() throws Exception {
		// given
		final String classNameWithSpaces = " ";

		// when
		final EntryTypeBean entryType = EntryTypeBean.newClass().withName(classNameWithSpaces).build();

		// then
		assertNull(entryType);
	}

	@Test(expected = IllegalArgumentException.class)
	public void entryTypeWithEmptyStringNameShouldNotBeCreated() throws Exception {
		// given
		final String classNameWithSpaces = "";

		// when
		final EntryTypeBean entryType = EntryTypeBean.newClass().withName(classNameWithSpaces).build();

		// then
		assertNull(entryType);
	}

	@Test(expected = IllegalArgumentException.class)
	public void entryTypeWithNotDefinedNameShouldNotBeCreated() throws Exception {
		// given
		final String classNameWithSpaces = "";

		// when
		final EntryTypeBean entryType = EntryTypeBean.newClass().withName(classNameWithSpaces).build();

		// then
		assertNull(entryType);
	}

}
