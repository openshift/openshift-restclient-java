/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package com.openshift.restclient.server;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Nicolas Spano
 * @author Andre Dietisheim
 */
public class WaitingHttpServerFake extends HttpServerFake {

    private long delay;

    @Override
    protected void write(byte[] text, OutputStream outputStream) throws IOException {

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            //Intentional ignore
            return ;
        }
    }

    public WaitingHttpServerFake(long delay){
        this.delay = delay;
    }
}
