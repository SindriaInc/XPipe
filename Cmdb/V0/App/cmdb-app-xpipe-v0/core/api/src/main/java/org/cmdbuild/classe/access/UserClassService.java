/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.ExtendedClassDefinition;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;

public interface UserClassService {

    ExtendedClass getExtendedClass(String classId, ClassQueryFeatures... features);

    ExtendedClass getExtendedClass(Classe classe, ClassQueryFeatures... features);

    ExtendedClass createClass(ExtendedClassDefinition definition);

    ExtendedClass updateClass(ExtendedClassDefinition definition);

    void invalidateCache();
    
    boolean userCanModify(String classId);

    boolean userCanRead(Classe classe);

    boolean isActiveAndUserCanRead(String classId);

    void deleteClass(String classId);

    List<Classe> getAllUserClasses();

    Classe getUserClass(String classId);

    @Nullable
    Classe getUserClassOrNull(String classId);

    Attribute getUserAttribute(String classId, String attrId);

    List<Attribute> getUserAttributes(String classId);
    
    List<Attribute> getActiveUserAttributes(String classId);

    Attribute createAttribute(Attribute attribute);

    Attribute updateAttribute(Attribute attribute);

    void deleteAttribute(String classId, String attrId);

    void updateAttributes(List<Attribute> attributes);

    default void checkUserCanModify(String classId, String message, Object... args) {
        checkArgument(userCanModify(classId), message, args);
    }

    default List<Classe> getActiveUserClasses() {
        return getAllUserClasses().stream().filter(Classe::isActive).collect(toList());
    }

    enum ClassQueryFeatures {
        CQ_FILTER_CEVICE, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER, CQ_INCLUDE_LOOKUP_VALUES
    }

}
