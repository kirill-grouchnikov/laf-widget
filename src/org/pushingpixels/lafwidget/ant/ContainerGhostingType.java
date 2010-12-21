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
 * Ant type for storing <code>containerGhosting</code> elements of
 * {@link AugmentContainerGhostingTask} task.
 * 
 * 
 * <p>
 * Represents the following build snippet:
 * </p>
 * 
 * <pre>
 * <code>
 * &lt;containerGhosting className=&quot;org.pushingpixels.substance.internal.ui.SubstanceButtonUI&quot;
 * toInjectAfterOriginal=&quot;true&quot; /&gt;
 * </code>
 * </pre>
 * 
 * @author Kirill Grouchnikov
 */
public class ContainerGhostingType extends DataType {
	/**
	 * UI delegate class name.
	 */
	private String className;

	/**
	 * Indicates whether the ghosting should be injected before or after the
	 * original code.
	 */
	private boolean toInjectAfterOriginal;

	/**
	 * Creates new instance.
	 */
	public ContainerGhostingType() {
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
	 * Returns indication whether the ghosting should be injected before or
	 * after the original code.
	 * 
	 * @return <code>true</code> if the ghosting should be injected after the
	 *         original code, <code>false</code> if it should be injected before
	 *         the original code.
	 */
	public boolean isToInjectAfterOriginal() {
		return toInjectAfterOriginal;
	}

	/**
	 * Returns indication whether the ghosting should be injected before or
	 * after the original code.
	 * 
	 * @param toInjectAfterOriginal
	 *            <code>true</code> if the ghosting should be injected after the
	 *            original code, <code>false</code> if it should be injected
	 *            before the original code.
	 */
	public void setToInjectAfterOriginal(boolean toInjectAfterOriginal) {
		this.toInjectAfterOriginal = toInjectAfterOriginal;
	}
}
