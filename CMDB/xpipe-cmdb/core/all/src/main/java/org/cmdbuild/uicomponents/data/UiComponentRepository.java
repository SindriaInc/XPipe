package org.cmdbuild.uicomponents.data;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;

public interface UiComponentRepository {

    List<UiComponentData> getAll();

    UiComponentData create(UiComponentData customPage);

    UiComponentData update(UiComponentData customPage);

    void delete(long id);

    @Nullable
    UiComponentData getByTypeAndNameOrNull(UiComponentType type, String name);

    UiComponentData getById(long id);

    default List<UiComponentData> getAllByType(UiComponentType type) {
        checkNotNull(type);
        return getAll().stream().filter(c -> equal(c.getType(), type)).collect(toList());
    }

    default UiComponentData getByTypeAndName(UiComponentType type, String name) {
        return checkNotNull(getByTypeAndNameOrNull(type, name), "ui component not found for type = %s name =< %s >", type, name);
    }
}
