/*
 * Copyright (c) 2005-2010 Laf-Widget Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of Laf-Widget Kirill Grouchnikov nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package org.pushingpixels.lafwidget.ant;

import org.apache.tools.ant.types.DataType;

/**
 * Ant type for storing <code>iconGhosting</code> elements of
 * {@link AugmentIconGhostingTask} task.
 * 
 * 
 * <p>
 * Represents the following build snippet:
 * </p>
 * 
 * <pre>
 * <code>
 * &lt;iconGhosting className=&quot;org.pushingpixels.substance.internal.ui.SubstanceButtonUI&quot;
 * methodName=&quot;paintIcon&quot; /&gt;
 * </code>
 * </pre>
 * 
 * @author Kirill Grouchnikov
 */
public class IconGhostingType extends DataType {
	/**
	 * UI delegate class name.
	 */
	private String className;

	/**
	 * Method name to augment.
	 */
	private String methodName;

	/**
	 * Creates new instance.
	 */
	public IconGhostingType() {
		super();
	}

	/**
	 * Sets the UI delegate class name.
	 * 
	 * @param name
	 *            UI delegate class name.
	 */
	public void setClassName(String name) {
		this.className = name;
	}

	/**
	 * Returns the UI delegate class name.
	 * 
	 * @return UI delegate class name.
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * Returns the method name to augment.
	 * 
	 * @return Method name to augment.
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Sets the method name to augment.
	 * 
	 * @param methodName
	 *            Method name to augment.
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
}
