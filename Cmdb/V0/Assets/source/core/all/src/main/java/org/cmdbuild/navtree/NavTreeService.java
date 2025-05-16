package org.cmdbuild.navtree;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;

public interface NavTreeService {

    List<NavTree> getAll();

    @Nullable
    NavTree getTreeOrNull(String type);

    void removeTree(String treeType);

    NavTree create(NavTree tree);

    NavTree update(NavTree tree);

    void fixDirections(String treeId);

    default List<NavTree> getAllActive() {
        return getAll().stream().filter(NavTree::getActive).collect(toList());
    }

    default NavTree getTree(String type) {
        return checkNotNull(getTreeOrNull(type), "nav tree not found for name = %s", type);
    }

}
