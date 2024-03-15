/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.xpdl;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import javax.activation.DataSource;
import org.cmdbuild.auth.login.AuthenticationService;
import org.cmdbuild.auth.role.Role;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.springframework.stereotype.Component;

@Component
public class XpdlTemplateServiceImpl implements XpdlTemplateService {

    private final AuthenticationService authenticationService;

    public XpdlTemplateServiceImpl(AuthenticationService authenticationService) {
        this.authenticationService = checkNotNull(authenticationService);
    }

    @Override
    public DataSource getTemplate(org.cmdbuild.workflow.model.Process process) {
        XpdlDocumentHelper xpdlDocumentHelper = XpdlDocumentHelper.createXpdlTemplateForProcess(process, list(authenticationService.getAllGroups()).map(Role::getName));
        return newDataSource(XpdlPackageFactory.xpdlByteArray(xpdlDocumentHelper.getPkg()), "application/x-xpdl", format("%s.xpdl", process.getName()));
    }
}
