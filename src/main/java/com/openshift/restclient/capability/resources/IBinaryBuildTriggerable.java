/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.restclient.capability.resources;

import java.io.InputStream;

import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.model.IBuild;

/**
 * Capability to trigger a binary build based on the build configuration
 *
 */
public interface IBinaryBuildTriggerable extends ICapability {

    /**
     * Trigger a binary build based on a build config
     * 
     * @param payload the binary payload process by build
     * @return The build that was triggered
     */
    IBuild triggerBinary(InputStream payload);

    /**
     * Set the asFile parameter
     * 
     * @param asFile the asFile
     */
    void setAsFile(String asFile);
    
    /**
     * Get the asFile parameter
     * 
     * @return the asFile parameter
     */
    String getAsFile();
    
    /**
     * Set the SCM commit
     * 
     * @param commit
     *            the SCM commit string
     */
    void setCommit(String commit);

    /**
     * Get the SCM commit
     * 
     * @return the SCM commit string
     */
    String getCommit();
    
    /**
     * Set the author email
     * 
     * @param authorEmail the author email
     */
    void setAuthorEmail(String authorEmail);
    
    /**
     * Get the author email
     * 
     * @return the author email
     */
    String getAuthorEmail();
    
    /**
     * Set the author name
     * 
     * @param authorName the author name
     */
    void setAuthorName(String authorName);
    
    /**
     * Get the author name
     * 
     * @return the author name
     */
    String getAuthorName();
    
    /**
     * Set the committer email
     * 
     * @param committerEmail the committer email
     */
    void setCommitterEmail(String committerEmail);
    
    /**
     * Get the committer email
     * 
     * @return the committer email
     */
    String getCommitterEmail();

    /**
     * Set the committer name
     * 
     * @param committerName the committer name
     */
    void setCommitterName(String committerName);
    
    /**
     * Get the committer name
     * 
     * @return the committer name
     */
    String getCommitterName();
    
    /**
     * Set the message
     * 
     * @param message the message
     */
    void setMessage(String message);
    
    /**
     * Get the message
     * 
     * @return the message
     */
    String getMessage();
}
