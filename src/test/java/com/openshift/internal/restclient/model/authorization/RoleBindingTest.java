/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.model.authorization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

import com.openshift.internal.restclient.TypeMapperFixture;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.utils.Samples;

/**
 * @author Red Hat Developers
 *
 */
public class RoleBindingTest extends TypeMapperFixture {
  
    private static final String RBAC_OCP4_GROUP = "{\"kind\":\"APIGroupList\",\"apiVersion\":\"v1\",\"groups\":[{\"name\":\"rbac.authorization.k8s.io\",\"versions\":"
        + "[{\"groupVersion\":\"rbac.authorization.k8s.io/v1\",\"version\":\"v1\"},{\"groupVersion\":\"rbac.authorization.k8s.io/v1beta1\",\"version\":\"v1beta1\"}],"
        + "\"preferredVersion\":{\"groupVersion\":\"rbac.authorization.k8s.io/v1\",\"version\":\"v1\"}}]}";
  
    private static final String RBAC_OCP4_RESOURCE = "{\"kind\":\"APIResourceList\",\"apiVersion\":\"v1\",\"groupVersion\":\"rbac.authorization.k8s.io/v1\",\"resources\""
        + ":[{\"name\":\"clusterrolebindings\",\"singularName\":\"\",\"namespaced\":false,\"kind\":\"ClusterRoleBinding\",\"verbs\":[\"create\",\"delete\","
        + "\"deletecollection\",\"get\",\"list\",\"patch\",\"update\",\"watch\"],\"storageVersionHash\":\"48tpQ8gZHFc=\"},{\"name\":\"clusterroles\",\"singularName\":"
        + "\"\",\"namespaced\":false,\"kind\":\"ClusterRole\",\"verbs\":[\"create\",\"delete\",\"deletecollection\",\"get\",\"list\",\"patch\",\"update\",\"watch\"],"
        + "\"storageVersionHash\":\"bYE5ZWDrJ44=\"},{\"name\":\"rolebindings\",\"singularName\":\"\",\"namespaced\":true,\"kind\":\"RoleBinding\",\"verbs\":[\"create\","
        + "\"delete\",\"deletecollection\",\"get\",\"list\",\"patch\",\"update\",\"watch\"],\"storageVersionHash\":\"eGsCzGH6b1g=\"},{\"name\":\"roles\",\"singularName\":"
        + "\"\",\"namespaced\":true,\"kind\":\"Role\",\"verbs\":[\"create\",\"delete\",\"deletecollection\",\"get\",\"list\",\"patch\",\"update\",\"watch\"],"
        + "\"storageVersionHash\":\"7FuwZcIIItM=\"}]}";
  
    private static final String RBAC_OCP3_GROUP = "{\"kind\":\"APIGroupList\",\"apiVersion\":\"v1\",\"groups\":[{\"name\":\"authorization.openshift.io\",\"versions\":"
        + "[{\"groupVersion\":\"authorization.openshift.io/v1\",\"version\":\"v1\"}],\"preferredVersion\":{\"groupVersion\":\"authorization.openshift.io/v1\","
        + "\"version\":\"v1\"}}]}";
  
    private static final String RBAC_OCP3_RESOURCE = "{\"kind\":\"APIResourceList\",\"apiVersion\":\"v1\",\"groupVersion\":\"authorization.openshift.io/v1\",\"resources\":"
        + "[{\"name\":\"clusterrolebindings\",\"singularName\":\"\",\"namespaced\":false,\"kind\":\"ClusterRoleBinding\",\"verbs\":[\"create\",\"delete\",\"get\",\"list\","
        + "\"patch\",\"update\"]},{\"name\":\"clusterroles\",\"singularName\":\"\",\"namespaced\":false,\"kind\":\"ClusterRole\",\"verbs\":[\"create\",\"delete\",\"get\","
        + "\"list\",\"patch\",\"update\"]},{\"name\":\"localresourceaccessreviews\",\"singularName\":\"\",\"namespaced\":true,\"kind\":\"LocalResourceAccessReview\","
        + "\"verbs\":[\"create\"]},{\"name\":\"localsubjectaccessreviews\",\"singularName\":\"\",\"namespaced\":true,\"kind\":\"LocalSubjectAccessReview\",\"verbs\":"
        + "[\"create\"]},{\"name\":\"resourceaccessreviews\",\"singularName\":\"\",\"namespaced\":false,\"kind\":\"ResourceAccessReview\",\"verbs\":[\"create\"]},{\"name\":"
        + "\"rolebindingrestrictions\",\"singularName\":\"\",\"namespaced\":true,\"kind\":\"RoleBindingRestriction\",\"verbs\":[\"create\",\"delete\",\"deletecollection\","
        + "\"get\",\"list\",\"patch\",\"update\",\"watch\"]},{\"name\":\"rolebindings\",\"singularName\":\"\",\"namespaced\":true,\"kind\":\"RoleBinding\",\"verbs\":"
        + "[\"create\",\"delete\",\"get\",\"list\",\"patch\",\"update\"]},{\"name\":\"roles\",\"singularName\":\"\",\"namespaced\":true,\"kind\":\"Role\",\"verbs\":"
        + "[\"create\",\"delete\",\"get\",\"list\",\"patch\",\"update\"]},{\"name\":\"selfsubjectrulesreviews\",\"singularName\":\"\",\"namespaced\":true,\"kind\":"
        + "\"SelfSubjectRulesReview\",\"verbs\":[\"create\"]},{\"name\":\"subjectaccessreviews\",\"singularName\":\"\",\"namespaced\":false,\"kind\":"
        + "\"SubjectAccessReview\",\"verbs\":[\"create\"]},{\"name\":\"subjectrulesreviews\",\"singularName\":\"\",\"namespaced\":true,\"kind\":\"SubjectRulesReview\","
        + "\"verbs\":[\"create\"]}]}";

    @Test
    public void checkRoleBindingListOCP4() throws Exception {
        getHttpClient().whenRequestTo(base + "/oapi", responseOf(""));
        getHttpClient().whenRequestTo(base + "/apis", responseOf(RBAC_OCP4_GROUP));
        getHttpClient().whenRequestTo(base + "/apis/rbac.authorization.k8s.io/v1", responseOf(RBAC_OCP4_RESOURCE));
        getHttpClient().whenRequestTo(base + "/apis/rbac.authorization.k8s.io/v1/namespaces/default/rolebindings",
            responseOf(Samples.RBAC_AUTHORIZATION_K8S_IO_ROLEBINDINGS.getContentAsString()));
        List<RoleBinding> roleBindings = getIClient().list(ResourceKind.ROLE_BINDING, "default");
        assertFalse(roleBindings.isEmpty());
        assertEquals(RoleBinding.class, roleBindings.get(0).getClass());
    }

    @Test
    public void checkRoleBindingListOCP3() throws Exception {
        getHttpClient().whenRequestTo(base + "/oapi", responseOf(""));
        getHttpClient().whenRequestTo(base + "/apis", responseOf(RBAC_OCP3_GROUP));
        getHttpClient().whenRequestTo(base + "/apis/authorization.openshift.io/v1", responseOf(RBAC_OCP3_RESOURCE));
        getHttpClient().whenRequestTo(base + "/apis/authorization.openshift.io/v1/namespaces/default/rolebindings",
            responseOf(Samples.AUTHORIZATION_OPENSHIFT_IO_ROLEBINDINGS.getContentAsString()));
        List<RoleBinding> roleBindings = getIClient().list(ResourceKind.ROLE_BINDING, "default");
        assertFalse(roleBindings.isEmpty());
        assertEquals(RoleBinding.class, roleBindings.get(0).getClass());
    }
}
