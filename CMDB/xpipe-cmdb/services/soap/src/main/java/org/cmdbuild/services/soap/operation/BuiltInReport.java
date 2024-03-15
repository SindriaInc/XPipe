package org.cmdbuild.services.soap.operation;

import static java.lang.String.format;

import org.cmdbuild.auth.login.AuthenticationStore;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.report.ReportProcessor;

public enum BuiltInReport {

	LIST("_list") {

		@Override
		public ReportFactoryBuilder<ReportProcessor> newBuilder(final DaoService dataView,
				//				final FilesStore filesStore,
				final AuthenticationStore authenticationStore, final CoreConfiguration configuration) {
//			return new ListReportFactoryBuilder(dataView, filesStore, authenticationStore, configuration);
//			return new ListReportFactoryBuilder(dataView, authenticationStore, configuration);
			throw new UnsupportedOperationException("unsupported");
		}

	},;

	private final String reportId;

	private BuiltInReport(final String reportId) {
		this.reportId = reportId;
	}

	public static BuiltInReport from(final String reportId) {
		for (final BuiltInReport report : values()) {
			if (report.reportId.equals(reportId)) {
				return report;
			}
		}
		throw new Error(format("undefined report '%s'", reportId));
	}

//	public abstract ReportFactoryBuilder<ReportFactory> newBuilder(DataView dataView, FilesStore filesStore,
	public abstract ReportFactoryBuilder<ReportProcessor> newBuilder(DaoService dataView,
			AuthenticationStore authenticationStore, CoreConfiguration configuration);

}
