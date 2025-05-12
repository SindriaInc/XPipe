/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.virtual;

import org.cmdbuild.dao.beans.DatabaseRecord;

public interface VirtualAttributeService {

    <T extends DatabaseRecord> T loadVirtualAttributes(T card);

}
