/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.minions;

public interface MinionStatusInfo {

    String getName();

    String getDescription();

    String getConfigEnabler();

    MinionStatus getStatus();

}
