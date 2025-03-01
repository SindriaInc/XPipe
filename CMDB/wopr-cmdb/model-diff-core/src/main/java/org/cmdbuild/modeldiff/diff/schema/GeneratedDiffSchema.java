/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_NAME_SERIALIZATION;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 * Represents a <i>diff</i> on modified <i>schema</i>.
 *
 * @author afelice
 */
public class GeneratedDiffSchema {

    public String name;
    public String description;

    public List<GeneratedDiffSchema_Classe> insertedClasses = list();
    public List<GeneratedDiffSchema_Classe> removedClasses = list();
    public List<GeneratedDiffSchema_ChangedClasse> changedClasses = list();

    public List<GeneratedDiffSchema_Lookup> insertedLookups = list();
    public List<GeneratedDiffSchema_Lookup> removedLookups = list();
    public List<GeneratedDiffSchema_ChangedLookup> changedLookups = list();

    public boolean hasInsertedClasse(String curClasseName) {
        return insertedClasses.stream().filter(c -> curClasseName.equals(getName(c.getItemProperties()))).findAny().isPresent();
    }

    public GeneratedDiffSchema_Classe getInsertedClasse(String curClasseName) {
        return insertedClasses.stream().filter(c -> curClasseName.equals(getName(c.getItemProperties()))).findAny().get();
    }

    public synchronized boolean hasChangedClasse(String curClasseName) {
        return changedClasses.stream().filter(c -> curClasseName.equals(c.getItemProps().getName())).findAny().isPresent();
    }

    public synchronized GeneratedDiffSchema_ChangedClasse getChangedClasse(String curClasseName) {
        return changedClasses.stream().filter(c -> curClasseName.equals(c.getItemProps().getName())).findAny().get();
    }

    /**
     * Used when updating a parent {@link Classe} before a derived one: once
     * done, remove it to avoid processing this change again later on.
     *
     * @param curClasseName
     * @return
     */
    public synchronized GeneratedDiffSchema_ChangedClasse popChangedClasse(String curClasseName) {
        // Return removing from changed classes        
        GeneratedDiffSchema_ChangedClasse result = null;

        int curPos = 0;
        for (; curPos < changedClasses.size(); curPos++) {
            if (changedClasses.get(curPos).getName().equals(curClasseName)) {
                result = changedClasses.get(curPos);
                changedClasses.remove(curPos);
                break; // Esci dal loop dopo la riomozione
            }
        }

        return result;
    }

    private String getName(Map<String, Object> props) {
        return (String) props.get(ATTR_NAME_SERIALIZATION);
    }

    // @todo AFE non dovrebbe servire, con lo schema, visto che il file di diff non è deserializzato in più parti
//    /**
//     * Used if adding incrementally {@link Classe} deserialized <i>diff data</i>.
//     * 
//     * @param curClasseDiffSchema
//     * @throws IOException if found <i>diff</i> name is mismatching.
//     */
//    public void add(GeneratedDiffSchema curClasseDiffSchema) throws IOException {
//        checkDataset(curClasseDiffSchema);
//        
//        insertedClasses = addComponentData(curClasseDiffSchema, insertedCards, curClasseDiffSchema.insertedCards);
//        removedClasses = addComponentData(curClasseDiffSchema, removedCards, curClasseDiffSchema.removedCards);
//        changedClasses = addComponentData(curClasseDiffSchema, changedCards, curClasseDiffSchema.changedCards);
//    }
//    
//    /**
//     * Removes changes that are now empty for some sanitizing operation (like removing 
//     * newly to add document metadata.
//     */
//    public void removeEmptyChanges() {
//        changedCards = list(changedCards).without(c -> c.changedAttribs.isEmpty() && c.addedAttribs.isEmpty());
//    }    
//
//    protected List addComponentData(GeneratedDiffSchema curClasseData, List thisComponentData, List otherComponentData) {
//        List result = thisComponentData;
//        if (result == null) {
//            result = list();
//        }
//        
//        if (otherComponentData != null) {
//            result.addAll(otherComponentData);
//        }
//        
//        return result;
//    } 
//    
//    protected void checkDataset(GeneratedDiffSchema curClasseData) throws IOException {
//        if (CmNullableUtils.isBlank(name)) {
//            this.name = curClasseData.name;
//        } else {
//            if (!this.name.equals(curClasseData.name)) {
//                throw new IOException("mismatching related dataset, found =< %s > but expected =< %s >".formatted(curClasseData.name, this.name));
//            }
//        }
//    }    
    
}
