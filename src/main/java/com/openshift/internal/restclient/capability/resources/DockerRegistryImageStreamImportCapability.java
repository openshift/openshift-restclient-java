/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.capability.resources;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.model.ModelNodeBuilder;
import com.openshift.internal.restclient.model.image.ImageStreamImport;
import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.internal.restclient.okhttp.ResponseCodeInterceptor;
import com.openshift.internal.util.JBossDmrExtentions;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.PredefinedResourceKind;
import com.openshift.restclient.capability.resources.IImageStreamImportCapability;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.image.IImageStreamImport;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Retrieve metadata directly from docker.
 */
public class DockerRegistryImageStreamImportCapability
        implements IImageStreamImportCapability, IHttpConstants, ResourcePropertyKeys {

    private static final String TOKEN = "token";
    private static final String STATUS_STATUS = "status.status";
    private static final String ID = "id";
    private static final String PARENT = "parent";
    private static final String REALM = "realm";
    private static final Logger LOG = LoggerFactory.getLogger(IImageStreamImportCapability.class);
    private static final String DEFAULT_DOCKER_REGISTRY = "https://registry-1.docker.io/v2";
    private IResourceFactory factory;
    private IProject project;
    private OkHttpClient okClient;

    public DockerRegistryImageStreamImportCapability(IProject project, IResourceFactory factory, IClient client) {
        this.factory = factory;
        this.project = project;
        this.okClient = client.adapt(OkHttpClient.class);
        if (okClient != null) {
            okClient = okClient.newBuilder().followRedirects(true).build();
        }
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public String getName() {
        return DockerRegistryImageStreamImportCapability.class.getSimpleName();
    }

    private boolean registryExists(OkHttpClient client) throws Exception {
        Request req = new Request.Builder().url(DEFAULT_DOCKER_REGISTRY)
                .header(ResponseCodeInterceptor.X_OPENSHIFT_IGNORE_RCI, "true").build();
        Response response = client.newCall(req).execute();
        if (response == null) {
            return false;
        }
        return (response.code() == STATUS_UNAUTHORIZED || response.code() == STATUS_OK);
    }

    /**
     * @return the token required to pull docker metadata
     */
    private String retrieveAuthToken(OkHttpClient client, String details) throws Exception {
        if (StringUtils.isNotBlank(details)) {
            Map<String, String> auth = parseAuthDetails(details);
            if (auth.containsKey(REALM)) {
                Request request = createAuthRequest(auth);
                Response response = client.newCall(request).execute();
                LOG.debug("Auth response: " + response.toString());
                if (response.code() == STATUS_OK
                        && MEDIATYPE_APPLICATION_JSON.equals(response.headers().get(PROPERTY_CONTENT_TYPE))) {
                    ModelNode tokenNode = ModelNode.fromJSONString(response.body().string());
                    if (tokenNode.hasDefined(TOKEN)) {
                        return tokenNode.get(TOKEN).asString();
                    } else {
                        LOG.debug("No auth token was found on auth response: " + tokenNode.toJSONString(false));
                    }
                } else {
                    LOG.info(
                            "Unable to retrieve authentication token as response was not OK and/or unexpected content type");
                }
            } else {
                LOG.info("Unable to retrieve authentication token - 'realm' was not found in the authenticate header: "
                        + auth.toString());
            }
        }
        return null;
    }

    private Request createAuthRequest(Map<String, String> authParams) {
        HttpUrl.Builder builder = HttpUrl.parse(StringUtils.strip(authParams.get(REALM), "\"")).newBuilder();
        for (Entry<String, String> e : authParams.entrySet()) {
            if (!REALM.equals(e.getKey())) {
                builder.addQueryParameter(StringUtils.strip(e.getKey(), "\""), StringUtils.strip(e.getValue(), "\""));
            }
        }
        Request request = new Request.Builder().url(builder.build())
                .header(ResponseCodeInterceptor.X_OPENSHIFT_IGNORE_RCI, "true").build();
        LOG.debug("Auth request uri: " + request.url());
        return request;
    }

    private Map<String, String> parseAuthDetails(String auth) {
        LOG.debug("Auth details header: " + auth);
        Map<String, String> map = new HashMap<>();
        String[] authAndValues = auth.split(" ");
        if (authAndValues.length == 2 && AUTHORIZATION_BEARER.equals(authAndValues[0])) {
            String[] params = authAndValues[1].split(",");
            for (String p : params) {
                String[] knv = p.split("=");
                if (knv.length >= 2) {
                    map.put(knv[0], knv[1]);
                }
            }
        }
        return map;
    }

    private DockerResponse retrieveMetaData(OkHttpClient client, String token, DockerImageURI uri) throws Exception {
        String regUri = String.format("%s/%s/%s/manifests/%s", DEFAULT_DOCKER_REGISTRY,
                StringUtils.defaultIfBlank(uri.getUserName(), "library"), uri.getName(), uri.getTag());
        Request.Builder builder = new Request.Builder().url(regUri)
                .header(ResponseCodeInterceptor.X_OPENSHIFT_IGNORE_RCI, "true");
        if (token != null) {
            builder.header(PROPERTY_AUTHORIZATION, String.format("%s %s", AUTHORIZATION_BEARER, token));

        }
        LOG.debug("retrieveMetaData uri: " + regUri);
        Response response = client.newCall(builder.build()).execute();
        LOG.debug("retrieveMetaData response: " + response.toString());
        switch (response.code()) {
        case STATUS_OK:
            return new DockerResponse(DockerResponse.DATA, response.body().string());
        case STATUS_UNAUTHORIZED:
            return new DockerResponse(DockerResponse.AUTH,
                    response.headers().get(IHttpConstants.PROPERTY_WWW_AUTHENTICATE));
        }
        LOG.info("Unable to retrieve docker meta data: " + response.toString());
        return null;
    }

    private static class DockerResponse {
        public static final String DATA = "data";
        public static final String AUTH = "auth";
        String responseType;
        String data;

        DockerResponse(String responseType, String data) {
            this.responseType = responseType;
            this.data = data;
        }

        public Object getResponseType() {
            return responseType;
        }

        public String getData() {
            return data;
        }
    }

    @Override
    public IImageStreamImport importImageMetadata(DockerImageURI uri) {
        if (okClient != null) {
            try {
                if (registryExists(okClient)) {
                    String token = null;
                    DockerResponse response = retrieveMetaData(okClient, token, uri);
                    if (DockerResponse.AUTH.equals(response.getResponseType())) {
                        LOG.debug("Unauthorized.  Trying to retrieve token...");
                        token = retrieveAuthToken(okClient, response.getData());
                        response = retrieveMetaData(okClient, token, uri);
                    }
                    if (DockerResponse.DATA.equals(response.getResponseType())) {
                        String meta = response.getData();
                        LOG.debug("Raw Docker image metadata: " + meta);
                        return buildResponse(meta, uri);
                    } else {
                        LOG.info("Unable to retrieve image metadata from docker registry");
                        return buildErrorResponse(uri);
                    }
                }
            } catch (Exception e) {
                LOG.error("Exception while trying to retrieve image metadata from docker", e);
            }
        }
        return buildErrorResponse(uri);
    }

    private IImageStreamImport buildErrorResponse(DockerImageURI uri) {
        ModelNodeBuilder builder = new ModelNodeBuilder().set(STATUS_STATUS, "Failure")
                .set("status.message",
                        String.format("you may not have access to the Docker image \"%s\"", uri.getUriWithoutHost()))
                .set("status.reason", "Unauthorized").set("status.code", IHttpConstants.STATUS_UNAUTHORIZED);

        return buildImageStreamImport(uri, builder.build());
    }

    private IImageStreamImport buildResponse(String meta, DockerImageURI uri) {
        ModelNode raw = ModelNode.fromJSONString(meta);
        ModelNode last = findNewestHistoryEntry(raw);
        ModelNode containerConfig = last.remove("container_config");
        last.get("ContainerConfig").set(containerConfig);

        ModelNodeBuilder builder = new ModelNodeBuilder().set(STATUS_STATUS, "Success").set("tag", uri.getTag())
                .set("image.metadata.name", uri.getName())
                .set(ImageStreamImport.IMAGE_DOCKER_IMAGE_REFERENCE, uri.getUriUserNameAndName())
                .set("image.dockerImageMetadata", last).set("status.code", IHttpConstants.STATUS_OK);

        return buildImageStreamImport(uri, builder.build());
    }

    private ImageStreamImport buildImageStreamImport(DockerImageURI uri, ModelNode node) {
        ImageStreamImport isImport = factory.stub(PredefinedResourceKind.IMAGE_STREAM_IMPORT.getIdentifier(), uri.getName(),
                this.project.getName());
        ModelNode root = isImport.getNode();
        ModelNode images = JBossDmrExtentions.get(root, null, ImageStreamImport.STATUS_IMAGES);
        images.add(node);

        return isImport;
    }

    private ModelNode findNewestHistoryEntry(ModelNode root) {
        ModelNode history = root.get("history");
        List<ModelNode> entries = history.asList().stream()
                .map(n -> ModelNode.fromJSONString(n.get("v1Compatibility").asString())).collect(Collectors.toList());
        entries.sort(new ManifestComparator());

        ModelNode last = entries.get(entries.size() - 1);
        LOG.debug("newest history: " + last.toJSONString(false));
        return last;
    }

    /**
     * Sorts history entries ordering from oldest to newest by comparing the entry
     * id to its referenced parent
     * 
     * @author jeff.cantrill
     *
     */
    static class ManifestComparator implements Comparator<ModelNode> {

        @Override
        public int compare(ModelNode one, ModelNode two) {
            String parent1 = one.has(PARENT) ? one.get(PARENT).asString() : null;
            String parent2 = two.has(PARENT) ? one.get(PARENT).asString() : null;
            if (parent1 == null && parent2 != null) {
                return -1;
            } else if (parent1 != null && parent2 == null) {
                return 1;
            } else if (parent1 == null && parent2 == null) {
                return 0; // we should never get here
            }
            String id1 = one.get(ID).asString();
            String id2 = two.get(ID).asString();

            if (parent2.equals(id1)) {
                return -1;
            } else if (parent1.equals(id2)) {
                return 1;
            }

            return 0; // we should never get here
        }
    }
}
