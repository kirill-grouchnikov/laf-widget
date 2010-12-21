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

import java.util.*;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.EmptyVisitor;

/**
 * Gathers information on all methods and fields of some class.
 * 
 * @author Kirill Grouchnikov
 */
public class InfoClassVisitor extends EmptyVisitor implements Opcodes {
	/**
	 * All method names.
	 */
	protected Set<String> methods;

	/**
	 * All field names.
	 */
	protected Set<String> fields;

	/**
	 * Creates a new visitor.
	 */
	public InfoClassVisitor() {
		this.methods = new HashSet<String>();
		this.fields = new HashSet<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.asm.commons.EmptyVisitor#visitMethod(int,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String[])
	 */
	public MethodVisitor visitMethod(final int access, final String name,
			final String desc, final String signature, final String[] exceptions) {
		this.methods.add(name);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.asm.commons.EmptyVisitor#visitField(int,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.Object)
	 */
	public FieldVisitor visitField(int access, String name, String desc,
			String signature, Object value) {
		this.fields.add(name);
		return this;
	}

	/**
	 * Returns all methods of the visited class.
	 * 
	 * @return Unmodifiable set of all methods of the visited class.
	 */
	public Set<String> getMethods() {
		return Collections.unmodifiableSet(this.methods);
	}

	/**
	 * Returns all fields of the visited class.
	 * 
	 * @return Unmodifiable set of all fields of the visited class.
	 */
	public Set<String> getFields() {
		return Collections.unmodifiableSet(this.fields);
	}
}
