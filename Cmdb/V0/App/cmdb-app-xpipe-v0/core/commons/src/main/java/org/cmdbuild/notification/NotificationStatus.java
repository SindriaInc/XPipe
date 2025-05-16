/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.notification;

public enum NotificationStatus {
    NS_OUTGOING, NS_SENT, NS_ERROR;
    
    /**
     * 
     * @param other
     * @return  <code>NS_SENT</code> only if both this and other are <code>NS_SENT</code>; <code>NS_ERROR</code> otherwise.
     */
    public NotificationStatus merge(NotificationStatus other) {
        return switch (this) {
            case NS_SENT -> (other == NotificationStatus.NS_SENT) ? this : NS_ERROR; 
            case NS_ERROR, NS_OUTGOING -> NS_ERROR;
            default -> NS_ERROR;
        };
    }
    
}
