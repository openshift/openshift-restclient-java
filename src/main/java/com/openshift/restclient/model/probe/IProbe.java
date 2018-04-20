/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package com.openshift.restclient.model.probe;

/**
 * @author Andre Dietisheim
 */
public interface IProbe {

    void setInitialDelaySeconds(int delay);

    int getInitialDelaySeconds();

    void setPeriodSeconds(int period);

    int getPeriodSeconds();

    void setSuccessThreshold(int threshold);

    int getSuccessThreshold();

    void setFailureThreshold(int failureThreshold);

    int getFailureThreshold();

    void setTimeoutSeconds(int timeout);

    int getTimeoutSeconds();
}
