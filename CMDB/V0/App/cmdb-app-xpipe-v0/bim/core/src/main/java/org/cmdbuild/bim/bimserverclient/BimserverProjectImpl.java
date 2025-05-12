package org.cmdbuild.bim.bimserverclient;

import org.cmdbuild.bim.bimserverclient.BimserverProject;
import org.joda.time.DateTime;

import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class BimserverProjectImpl implements BimserverProject {

	private final String name, projectId, ifcFormat, description;
	private final boolean isActive;
	private final DateTime lastCheckin;

	private BimserverProjectImpl(BimserverProjectBuilder builder) {
		this.name = checkNotBlank(builder.name);
		this.projectId = checkNotBlank(builder.projectId);
		this.ifcFormat = checkNotBlank(builder.ifcFormat);
		this.description = builder.description;
		this.isActive = builder.isActive;
		this.lastCheckin = builder.lastCheckin;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getProjectId() {
		return projectId;
	}

	@Override
	public String getIfcFormat() {
		return ifcFormat;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public DateTime getLastCheckin() {
		return lastCheckin;
	}

	public static BimserverProjectBuilder builder() {
		return new BimserverProjectBuilder();
	}

	public static BimserverProjectBuilder copyOf(BimserverProject source) {
		return new BimserverProjectBuilder()
				.withName(source.getName())
				.withProjectId(source.getProjectId())
				.withIfcFormat(source.getIfcFormat())
				.withDescription(source.getDescription())
				.withIsActive(source.isActive())
				.withLastCheckin(source.getLastCheckin());
	}

	public static class BimserverProjectBuilder implements Builder<BimserverProjectImpl, BimserverProjectBuilder> {

		private String name;
		private String projectId;
		private String ifcFormat;
		private String description;
		private boolean isActive = true;
		private DateTime lastCheckin;

		public BimserverProjectBuilder withName(String name) {
			this.name = name;
			return this;
		}

		public BimserverProjectBuilder withProjectId(String projectId) {
			this.projectId = projectId;
			return this;
		}

		public BimserverProjectBuilder withIfcFormat(String ifcFormat) {
			this.ifcFormat = ifcFormat;
			return this;
		}

		public BimserverProjectBuilder withDescription(String description) {
			this.description = description;
			return this;
		}

		public BimserverProjectBuilder withIsActive(boolean isActive) {
			this.isActive = isActive;
			return this;
		}

		public BimserverProjectBuilder withLastCheckin(DateTime lastCheckin) {
			this.lastCheckin = lastCheckin;
			return this;
		}

		@Override
		public BimserverProjectImpl build() {
			return new BimserverProjectImpl(this);
		}

	}
}
