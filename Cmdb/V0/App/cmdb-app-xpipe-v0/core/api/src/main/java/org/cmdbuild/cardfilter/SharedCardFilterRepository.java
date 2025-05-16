package org.cmdbuild.cardfilter;

import java.util.List;
import javax.annotation.Nullable;

public interface SharedCardFilterRepository {

    List<StoredFilter> readSharedFilters(@Nullable String className);

    List<StoredFilter> getAllSharedFilters();
}
