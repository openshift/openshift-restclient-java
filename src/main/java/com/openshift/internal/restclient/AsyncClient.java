/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient;

import com.openshift.internal.restclient.authorization.AuthorizationContext;
import com.openshift.internal.restclient.okhttp.WatchClient;
import com.openshift.restclient.*;
import com.openshift.restclient.UnsupportedOperationException;
import com.openshift.restclient.api.ITypeFactory;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.model.IList;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.JSONSerializeable;
import okhttp3.*;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.openshift.internal.restclient.capability.CapabilityInitializer.initializeClientCapabilities;
import static java.util.stream.Collectors.joining;

/**
 * @author Jeff Cantrill
 */
public class AsyncClient extends DefaultClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncClient.class);


	public AsyncClient(URL baseUrl, OkHttpClient client, IResourceFactory factory, IApiTypeMapper typeMapper, AuthorizationContext authContext){
		super(baseUrl, client, factory, typeMapper, authContext);
	}

	public <T extends IResource> void asyncList(String kind, String namespace,Consumer<OpenShiftException> errorConsumer, Consumer<List<T>> consumer) {

		Callback callback = new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				errorConsumer.accept(new OpenShiftException(e, "Unable to execute request to %s", call.request().url()));
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String body =  response.body().string();
				LOGGER.debug("Response: {}", body);

				IList resources = (IList) factory.createInstanceFrom(body);

				List<T> items = new ArrayList<>();
				items.addAll((Collection<? extends T>) resources.getItems());

				consumer.accept(items);


			}
		};
		Map<String, String> params = new HashMap<>();
		executeAsync(this.factory, HttpMethod.GET.toString(), kind, namespace, null, null, null, null,params, callback);
	}


	public void executeAsync(ITypeFactory factory, String method, String kind, String namespace, String name,
							  String subresource, String subContext, JSONSerializeable payload, Map<String, String> params, Callback callback) {
		if(factory == null) {
			throw new OpenShiftException("ITypeFactory is null while trying to call IClient#execute");
		}

		if(params == null){
			params = Collections.emptyMap();
		}

		if(ResourceKind.LIST.equals(kind)) 
			throw new UnsupportedOperationException("Generic create operation not supported for resource type 'List'");
		final URL endpoint = new URLBuilder(this.baseUrl, typeMapper)
				.kind(kind)
				.name(name)
				.namespace(namespace)
				.subresource(subresource)
				.subContext(subContext)
 				.addParameters(params)
				.build();
			
			Request request = newRequestBuilderTo(endpoint.toString())
					.method(method, getPayload(method, payload))
					.build();
			LOGGER.debug("About to make {} request: {}", request.method(), request);

			client.newCall(request).enqueue(callback);

	}

}
