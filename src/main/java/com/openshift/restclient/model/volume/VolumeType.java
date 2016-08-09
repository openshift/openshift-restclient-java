/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.restclient.model.volume;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Jeff Cantrill
 *
 * @see <a href="https://docs.openshift.com/enterprise/3.0/architecture/additional_concepts/storage.html#types-of-persistent-volumes">Persistent volume types</a>
 *
 */
public interface VolumeType {

	static final String EMPTY_DIR = "emptyDir";
	static final String HOST_PATH = "hostPath";
	static final String NFS = "nfs";
	static final String GCE_PERSISTENT_DISK = "gcePersistentDisk";
	static final String AWS_ELASTIC_BLOCK_STORAGE = "awsElasticBlockStore";
	static final String GLUSTERFS = "glusterfs";
	static final String ISCSI = "iscsi";
	static final String RBD = "rbd";
	static final String SECRET = "secret";
	static final String PERSISTENT_VOLUME_CLAIM = "persistentVolumeClaim";

	static List<String> getTypes() {
		return Arrays.asList(EMPTY_DIR,
							HOST_PATH,
							NFS,
							GCE_PERSISTENT_DISK,
							AWS_ELASTIC_BLOCK_STORAGE,
							GLUSTERFS,
							ISCSI,
							RBD);
	}

}
