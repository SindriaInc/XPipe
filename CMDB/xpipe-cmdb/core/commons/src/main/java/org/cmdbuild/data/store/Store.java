package org.cmdbuild.data.store;

import java.util.Collection;

@Deprecated
public interface Store<T extends Storable> {

	Storable create(T storable);

	T read(Storable storable);

	Collection<T> readAll();

	Collection<T> readAll(Groupable groupable);

	void update(T storable);

	void delete(Storable storable);

}
