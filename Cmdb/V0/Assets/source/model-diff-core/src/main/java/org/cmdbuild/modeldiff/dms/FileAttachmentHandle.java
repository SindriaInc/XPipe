/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dms;

import java.util.Objects;
import org.cmdbuild.dao.entrytype.Classe;

/**
 *
 * @author afelice
 */
public abstract class FileAttachmentHandle implements Comparable<FileAttachmentHandle> {

    protected final Classe classe;
    protected final String cardId;

    /**
     * @param classe
     * @param cardId
     */
    protected FileAttachmentHandle(Classe classe, String cardId) {
        this.classe = classe;
        this.cardId = cardId;
    }

    public Classe getClasse() {
        return classe;
    }

    public String getClasseName() {
        return classe.getName();
    }

    public String getCardId() {
        return cardId;
    }

    @Override
    public int compareTo(FileAttachmentHandle other) {
        // First compare by class
        int classComparison = this.getClasseName().compareTo(other.getClasseName());
        if (classComparison != 0) {
            return classComparison;
        }

        // If same class, compare by card
        return this.cardId.compareTo(other.cardId);
    }

    // Override di equals
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileAttachmentHandle other = (FileAttachmentHandle) o;
        return Objects.equals(cardId, other.cardId)
                && Objects.equals(getClasseName(), other.getClasseName());
    }

    // Override di hashCode
    @Override
    public int hashCode() {
        return Objects.hash(getClasseName());
    }

    @Override
    public String toString() {
        return "FileAttributeHandle{classId=< %s >, cardId=< %s >}".formatted(getClasseName(), cardId);
    }

    public abstract FileAttachmentHandle copyWith(String newCcardId);
} // end FileAttachmentHandle class
