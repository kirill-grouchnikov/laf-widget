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

import org.objectweb.asm.*;

/**
 * Bytecode writer for a forwarding UI delegate class with a single constructor
 * that gets one parameter.
 * 
 * @author Kirill Grouchnikov
 */
public class UiDelegateWriterOneParamCtr extends ClassWriter implements Opcodes {
	/**
	 * Creates a new bytecode writer.
	 */
	public UiDelegateWriterOneParamCtr() {
		super(false);
	}

	/**
	 * Creates a new class.
	 * 
	 * @param packageName
	 *            Package name.
	 * @param className
	 *            Class name.
	 * @param superClassName
	 *            Superclass name.
	 * @param paramClassDesc
	 *            Description of the parameter classes.
	 * @return Class bytecode contents.
	 */
	public static byte[] createClass(String packageName, String className,
			String superClassName, String paramClassDesc) {

		packageName = packageName.replace('.', '/');
		superClassName = superClassName.replace('.', '/');

		UiDelegateWriterOneParamCtr cw = new UiDelegateWriterOneParamCtr();

		MethodVisitor mv;

		cw.visit(Opcodes.V1_2, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
				packageName + "/" + className, null, superClassName, null);

		cw.visitSource(className + ".java", null);

		mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
				"createUI",
				"(Ljavax/swing/JComponent;)Ljavax/swing/plaf/ComponentUI;",
				null, null);
		mv.visitCode();
		mv.visitTypeInsn(Opcodes.NEW, packageName + "/" + className);
		mv.visitInsn(Opcodes.DUP);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitTypeInsn(Opcodes.CHECKCAST, paramClassDesc.substring(1,
				paramClassDesc.length() - 1));
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
				packageName + "/" + className, "<init>", "(" + paramClassDesc
						+ ")V");
		mv.visitInsn(Opcodes.ARETURN);
		mv.visitMaxs(3, 1);
		mv.visitEnd();

		mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(" + paramClassDesc
				+ ")V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassName, "<init>", "("
				+ paramClassDesc + ")V");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();

		cw.visitEnd();

		return cw.toByteArray();
	}
}
