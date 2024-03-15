package org.cmdbuild.data.store;

public interface Groupable {

	/**
	 * Returns the name of the attribute that represents the group of the
	 * {@link Storable} objects, {@code null} if there is no grouping. Within a
	 * group the identifier must be unique. Implies a restriction over the
	 * {@link Store#read(Storable)}, {@link Store#update(Storable)} and
	 * {@link Store#readAll()} methods.
	 * 
	 * @return the name of the attribute or {@code null} if grouping is not
	 *         available.
	 */
	String getGroupAttributeName();

	/**
	 * Returns the name of the group. See
	 * {@link org.cmdbuild.data.store.dao.StorableConverter#getGroupAttributeName()}.
	 * 
	 * @return the name of the group.
	 */
	Object getGroupAttributeValue();

}