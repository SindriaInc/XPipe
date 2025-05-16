package org.cmdbuild.bim.bimserverclient;

import org.bimserver.interfaces.objects.SSerializerPluginConfiguration;

public class BimserverSerializer implements Serializer {

	private final SSerializerPluginConfiguration serializerByContentType;

	protected BimserverSerializer(final SSerializerPluginConfiguration serializerByContentType) {
		this.serializerByContentType = serializerByContentType;
	}

	@Override
	public Long getOid() {
		return serializerByContentType.getOid();
	}

}
