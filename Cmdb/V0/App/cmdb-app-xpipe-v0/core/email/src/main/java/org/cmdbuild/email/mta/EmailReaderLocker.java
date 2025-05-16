/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.lock.LockService;

/**
 *
 * @author afelice
 */
public class EmailReaderLocker implements AutoCloseable {

    private final EmailAccount emailAccount;
    private final LockService lockService;

    public EmailReaderLocker(EmailAccount emailAccount, LockService lockService) {
        this.emailAccount = checkNotNull(emailAccount);
        this.lockService = lockService;
        acquireLock();
    }

    protected final void acquireLock() {
        lockService.aquireLockOrWait(getEmailAccountLockId()).getLock();
    }

    protected final void releaseLock() {
        lockService.releaseLock(getEmailAccountLockId());
    }

    private String getEmailAccountLockId() {
        return format("email_account_%s", emailAccount.getAddress());
    }

    @Override
    public void close() {
        releaseLock();
    }

}
