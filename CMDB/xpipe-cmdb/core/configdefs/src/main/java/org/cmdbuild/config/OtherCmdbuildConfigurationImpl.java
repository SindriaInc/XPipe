/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import org.cmdbuild.config.GisConfiguration;
import org.cmdbuild.config.OtherCmdbuildConfiguration;
import static com.google.common.base.Preconditions.checkNotNull;
import org.springframework.stereotype.Component;

/**
 *
 * @deprecated TODO used for legacy ui, remove for 30
 */
@Component
@Deprecated
public class OtherCmdbuildConfigurationImpl implements OtherCmdbuildConfiguration {

	private final GisConfiguration gisConfiguration;

	public OtherCmdbuildConfigurationImpl(GisConfiguration gisConfiguration) {
		this.gisConfiguration = checkNotNull(gisConfiguration);
	}

	@Override
	public boolean isGisEnabled() {
		return gisConfiguration.isEnabled();
	}

	@Override
	public boolean isGoogleServiceOn() {
		return gisConfiguration.isGoogleEnabled();
	}

	@Override
	public boolean isYahooServiceOn() {
		return gisConfiguration.isYahooEnabled();
	}

	@Override
	public String getYahooKey() {
		return gisConfiguration.getYahooKey();
	}
}
