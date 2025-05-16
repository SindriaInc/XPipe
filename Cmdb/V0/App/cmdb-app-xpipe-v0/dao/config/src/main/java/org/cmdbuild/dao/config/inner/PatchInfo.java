/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.time.ZonedDateTime;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public interface PatchInfo extends Patch {

	@Nullable
	Patch getPatchOnDb();

	@Nullable
	Patch getPatchOnFile();

	@Override
	default String getVersion() {
		return getPatch().getVersion();
	}

	@Override
	default String getDescription() {
		return getPatch().getDescription();
	}

	@Override
	default String getCategory() {
		return getPatch().getCategory();
	}

	@Override
	default boolean isApplied() {
		return hasPatchOnDb();
	}

	@Override
	@Nullable
	default ZonedDateTime getApplyDate() {
		return hasPatchOnDb() ? getPatchOnDb().getApplyDate() : null;
	}

	@Override
	@Nullable
	default String getContent() {
		return getPatch().getContent();
	}

	@Override
	@Nullable
	default String getHash() {
		return getPatch().getHash();
	}

	/**
	 * useful to access common data (version, description, etc)
	 *
	 * @return
	 */
	default Patch getPatch() {
		return MoreObjects.firstNonNull(getPatchOnFile(), getPatchOnDb());
	}

	default boolean hashMismatch() {
		return hasPatchOnDb() && hasPatchOnFile() && !StringUtils.isBlank(getPatchOnDb().getHash()) && !Objects.equal(getPatchOnDb().getHash(), Preconditions.checkNotNull(getPatchOnFile().getHash()));
	}

	default boolean hasPatchOnDb() {
		return getPatchOnDb() != null;
	}

	default boolean hasPatchOnFile() {
		return getPatchOnFile() != null;
	}

	default boolean onlyOnDb() {
		return hasPatchOnDb() && !hasPatchOnFile();
	}

	default boolean onlyOnFile() {
		return hasPatchOnFile() && !hasPatchOnDb();
	}

	@Override
	public default Map<String, String> getParams() {
		return getPatch().getParams();
	}

}
