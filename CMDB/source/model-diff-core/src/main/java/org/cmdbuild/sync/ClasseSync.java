/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import java.util.List;
import java.util.Map;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.utils.lang.CmMapUtils;

/**
 *
 * @author afelice
 */
public interface ClasseSync {

    Classe read(String classeName);

    /**
     * Needed to calculate {@link Classe} props serialization, @see {@link #serializeClasseProps(org.cmdbuild.classe.ExtendedClass)
     * }.
     *
     * @param classeName
     * @return
     */
    ExtendedClass readExtended(String classeName);

    /**
     * Wrapper to read {@link ExtendedClass} from system. }.
     *
     * @param classe
     * @return
     */
    ExtendedClass readExtended(Classe classe);

    List<Classe> readAll();

    /**
     * @see {@link ClassWs#readAll()}
     *
     * @param includeInactiveElements
     * @param includeLookupValues
     * @param filterStr
     * @return
     */
    // @todo AFE TBC
    List<Classe> readAll(boolean includeInactiveElements, boolean includeLookupValues, String filterStr);

    ExtendedClass add(String classeName, Map<String, Object> classeCmdbSerialization);

    ExtendedClass update(String classeName, Map<String, Object> classeCmdbSerialization);

    ExtendedClass deactivate(Classe classe);

    // @todo AFE TBC
    void remove(Classe classe);

    CmMapUtils.FluentMap<String, Object> serializeClasseProps(ExtendedClass extendedClass);
}
