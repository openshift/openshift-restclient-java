package com.openshift.internal.restclient.model;

import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.PredefinedResourceKind;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.INamespace;
import com.openshift.restclient.utils.Samples;
import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class NamespaceTest {

    private static final String VERSION = "v1";
    private INamespace namespace;

    @Before
    public void setUp(){
        IClient client = mock(IClient.class);
        ModelNode node = ModelNode.fromJSONString(Samples.V1_NAMESPACE.getContentAsString());
        namespace = new Namespace(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION,
                PredefinedResourceKind.NAMESPACE.getIdentifier()));
    }

    @Test
    public void testGetDisplayName() {
        assertEquals("OpenShift 3 Sample", namespace.getDisplayName());
    }

    @Test
    public void testGetDescription() {
        assertEquals("This is an example namespace to demonstrate OpenShift v3", namespace.getDescription());
    }

    @Test
    public void testGetRequester() {
        assertEquals("admin", namespace.getRequester());
    }
}