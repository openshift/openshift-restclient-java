/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client.utils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

/**
 * @author Andre Dietisheim
 */
public class ExceptionCauseMatcher extends TypeSafeMatcher<Throwable> {

		private Matcher<?> causeMatcher;

		public ExceptionCauseMatcher(Matcher<?> matcher) {
			this.causeMatcher = matcher;
		}

		public void describeTo(Description description) {
			description.appendText("exception with cause ");
		}
	
		@Override
		public boolean matchesSafely(Throwable throwable) {
			return causeMatcher.matches(throwable.getCause());
		}
}

