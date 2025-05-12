package org.cmdbuild.services.soap;


import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.services.soap.operation.AuthenticationLogicHelper;
import org.cmdbuild.services.soap.operation.DataAccessLogicHelper;
import org.cmdbuild.services.soap.operation.DmsLogicHelper;
import org.cmdbuild.services.soap.operation.LookupLogicHelper;
import org.cmdbuild.services.soap.operation.WorkflowLogicHelper;
//import org.cmdbuild.services.store.report.ReportStore;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.cmdbuild.auth.user.OperationUserSupplier; 
import org.slf4j.LoggerFactory;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.workflow.shark.SharkEventService;
import org.cmdbuild.lookup.LookupRepository;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.menu.MenuService;

abstract class AbstractWebservice {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private OperationUserSupplier userStore;
//	@Autowired
//	private AuthenticationStore authenticationStore;
//	@Autowired
//	private MetadataStoreFactory metadataStoreFactory;
//	@Autowired
//	private WorkflowService workflowService;
//	@Autowired
//	private DataView userDataView;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private DataAccessLogicHelper dataAccessLogicHelper;
	@Autowired
	private MenuService menuStore;
	@Autowired
	private LookupService lookupService;
	@Autowired
	private SessionService sessionService;
	@Autowired
	private WorkflowLogicHelper workflowLogicHelper;

//	protected DataView userDataView() {
//		return userDataView;
//	}

	protected DmsLogicHelper dmsLogicHelper() {
		OperationUser operationUser = userStore.getUser();
		DmsService dmsLogic = applicationContext.getBean(DmsService.class);
		return new DmsLogicHelper(operationUser, dmsLogic);
	}

	protected LookupLogicHelper lookupLogicHelper() {
		return new LookupLogicHelper(lookupLogic());
	}

	protected WorkflowLogicHelper workflowLogicHelper() {
		return workflowLogicHelper;
	}

	protected DataAccessLogicHelper dataAccessLogicHelper() {
		return dataAccessLogicHelper;
	}

//	public CardAdapter cardAdapter() {
//		return new CardAdapter(userDataView(), lookupStore());
//	}

	protected SharkEventService workflowEventManager() {
		return applicationContext.getBean(SharkEventService.class);
	}

//	protected DataAccessService userDataAccessLogic() {
//		return applicationContext.getBean(DataAccessService.class);
//	}

	protected LookupRepository lookupStore() {
		return applicationContext.getBean(LookupRepository.class);
	}

//	protected LookupSerializer lookupSerializer() {
//		return applicationContext.getBean(LookupSerializer.class);
//	}

	protected ReportService reportStore() {
		return applicationContext.getBean(ReportService.class);
	}

	protected AuthenticationLogicHelper authenticationLogicHelper() {
		return applicationContext.getBean(AuthenticationLogicHelper.class);
//		OperationUser operationUser = userStore.getUser();
//		DataView dataView = applicationContext.getBean(DataView.class);
//		return new AuthenticationLogicHelper(operationUser, dataView, authenticationStore);
	}

	protected MenuService menuStore() {
		return menuStore;
	}

	protected LookupService lookupLogic() {
		return lookupService;
	}

	protected SessionService sessionLogic() {
		return sessionService;
	}

}
