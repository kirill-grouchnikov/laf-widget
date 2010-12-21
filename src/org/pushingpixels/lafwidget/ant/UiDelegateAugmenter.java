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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Augments the UI classes with laf-widget behaviour. Is based on sample adapter
 * from ASM distribution.
 * 
 * @author Kirill Grouchnikov
 */
public class UiDelegateAugmenter {
	/**
	 * Set of methods to change (augment).
	 */
	private Set<String> methodsToChange;

	/**
	 * Verbosity indication.
	 */
	private boolean isVerbose;

	/**
	 * Class adapter that augments the UI functionality.
	 * 
	 * @author Kirill Grouchnikov
	 */
	protected class AugmentClassAdapter extends ClassAdapter implements Opcodes {
		/**
		 * Contains all method names.
		 */
		private Set<String> existingMethods;

		/**
		 * Contains all field names.
		 */
		private Set<String> existingFields;

		/**
		 * Contains all methods that will be augmented. Key is {@link String},
		 * value is {@link Method}.
		 */
		private Map<String, Method> methodsToAugment;

		/**
		 * Prefix for delegate methods that will be added.
		 */
		private String prefix;

		/**
		 * Creates a new augmentor.
		 * 
		 * @param cv
		 *            Class visitor to recreate the non-augmented methods.
		 * @param existingMethods
		 *            Contains all method names.
		 * @param existingFields
		 *            Contains all field names.
		 * @param methodsToAugment
		 *            Contains all methods that will be augmented. Key is
		 *            {@link String}, value is {@link Method}.
		 */
		public AugmentClassAdapter(final ClassVisitor cv,
				Set<String> existingMethods, Set<String> existingFields,
				Map<String, Method> methodsToAugment) {
			super(cv);
			this.existingMethods = existingMethods;
			this.existingFields = existingFields;
			this.methodsToAugment = methodsToAugment;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.objectweb.asm.ClassAdapter#visit(int, int, java.lang.String,
		 * java.lang.String, java.lang.String, java.lang.String[])
		 */
		@Override
		public void visit(final int version, final int access,
				final String name, final String signature,
				final String superName, final String[] interfaces) {
			this.prefix = "__" + name.replaceAll("/", "__") + "__";
			super
					.visit(version, access, name, signature, superName,
							interfaces);

			// Check if need to add the lafWidgets field
			if (!this.existingFields.contains("lafWidgets")) {
				FieldVisitor fv = this.visitField(Opcodes.ACC_PROTECTED,
						"lafWidgets", "Ljava/util/Set;", null, null);
				fv.visitEnd();
			}

			// We have three separate cases for each function that we
			// want to augment:
			//
			// 1. The current .class has both function and the __ version -
			// already has been augmented. Can be ignored.
			//
			// 2. The current .class has function but doesn't have the __
			// version. Than, the original function has already been renamed to
			// __ (in the visitMethod). We need to create a new version for this
			// function that performs pre-logic, calls __ and performs the
			// post-logic.
			//
			// 3. The current .class doesn't have neither the function nor
			// the __ version. In this case we need to create the __ version
			// that calls super (with the original name) and the function that
			// performs pre-logic, calls __ and performs the post-logic.
			for (Iterator<String> it = UiDelegateAugmenter.this.methodsToChange
					.iterator(); it.hasNext();) {
				String methodName = it.next();

				if (!this.methodsToAugment.containsKey(methodName))
					continue;

				boolean hasOriginal = this.existingMethods.contains(methodName);
				boolean hasDelegate = this.existingMethods.contains(this.prefix
						+ methodName);

				Method method = this.methodsToAugment.get(methodName);
				String methodSignature = Utils.getMethodDesc(method);
				int paramCount = method.getParameterTypes().length;
				if (UiDelegateAugmenter.this.isVerbose)
					System.out.println("... Augmenting " + methodName + " "
							+ methodSignature + " : original - " + hasOriginal
							+ ", delegate - " + hasDelegate + ", " + paramCount
							+ " params");

				if (!hasDelegate) {
					if (methodName.equals("installUI")) {
						this.augmentInstallUIMethod(!hasOriginal, name,
								superName, methodSignature);
					} else {
						if (method.getParameterTypes().length == 0) {
							this.augmentVoidMethod(!hasOriginal, name,
									superName, methodName, method
											.getModifiers());
						} else {
							this.augmentSingleParameterMethod(!hasOriginal,
									name, superName, methodName, method
											.getModifiers(), methodSignature);
						}
					}
				}
			}
		}

		/**
		 * Augments void UI method (w/o parameters).
		 * 
		 * @param toSynthOriginal
		 *            Indication whether we need to create an empty (only call
		 *            to super()) implementation.
		 * @param className
		 *            Class name.
		 * @param superClassName
		 *            Super class name (relevant for generating empty
		 *            implementation).
		 * @param methodName
		 *            Method name.
		 */
		public void augmentVoidMethod(boolean toSynthOriginal,
				String className, String superClassName, String methodName,
				int methodModifiers) {
			int modifierOpcode = Opcodes.ACC_PUBLIC;
			if (Modifier.isProtected(methodModifiers))
				modifierOpcode = Opcodes.ACC_PROTECTED;

			// Some ASM woodoo. The code below was generated by using
			// ASMifierClassVisitor.
			if (toSynthOriginal) {
				MethodVisitor mv = this.cv.visitMethod(modifierOpcode,
						this.prefix + methodName, "()V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassName,
						methodName, "()V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}

			MethodVisitor mv = this.cv.visitMethod(modifierOpcode, methodName,
					"()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, this.prefix
					+ methodName, "()V");
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitFieldInsn(Opcodes.GETFIELD, className, "lafWidgets",
					"Ljava/util/Set;");
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Set",
					"iterator", "()Ljava/util/Iterator;");
			mv.visitVarInsn(Opcodes.ASTORE, 1);
			Label l0 = new Label();
			mv.visitJumpInsn(Opcodes.GOTO, l0);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitVarInsn(Opcodes.ALOAD, 1);
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator",
					"next", "()Ljava/lang/Object;");
			mv.visitTypeInsn(Opcodes.CHECKCAST,
					"org/pushingpixels/lafwidget/LafWidget");
			mv.visitVarInsn(Opcodes.ASTORE, 2);
			mv.visitVarInsn(Opcodes.ALOAD, 2);
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE,
					"org/pushingpixels/lafwidget/LafWidget", methodName, "()V");
			mv.visitLabel(l0);
			mv.visitVarInsn(Opcodes.ALOAD, 1);
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator",
					"hasNext", "()Z");
			mv.visitJumpInsn(Opcodes.IFNE, l1);
			mv.visitInsn(Opcodes.RETURN);
			mv.visitMaxs(1, 3);
			mv.visitEnd();
		}

		/**
		 * Augments single-parameter UI method.
		 * 
		 * @param toSynthOriginal
		 *            Indication whether we need to create an empty (only call
		 *            to super()) implementation.
		 * @param className
		 *            Class name.
		 * @param superClassName
		 *            Super class name (relevant for generating empty
		 *            implementation).
		 * @param methodName
		 *            Method name.
		 * @param functionDesc
		 *            Function signature (using JNI style declaration). Example
		 *            for <code>void installUI(JButton button)</code>:
		 *            <code>(Ljavax/swing/JButton;)V</code>.
		 */
		public void augmentSingleParameterMethod(boolean toSynthOriginal,
				String className, String superClassName, String methodName,
				int methodModifiers, String functionDesc) {

			int modifierOpcode = Opcodes.ACC_PUBLIC;
			if (Modifier.isProtected(methodModifiers))
				modifierOpcode = Opcodes.ACC_PROTECTED;

			// Some ASM woodoo. The code below was generated by using
			// ASMifierClassVisitor.
			if (toSynthOriginal) {
				if (UiDelegateAugmenter.this.isVerbose) {
					System.out.println("... Creating empty '" + methodName
							+ functionDesc + "' forwarding to super '"
							+ superClassName + "'");
				}
				MethodVisitor mv = this.cv.visitMethod(modifierOpcode,
						this.prefix + methodName, functionDesc, null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassName,
						methodName, functionDesc);
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(2, 2);
				mv.visitEnd();
			}

			if (UiDelegateAugmenter.this.isVerbose) {
				System.out.println("... Augmenting '" + methodName
						+ functionDesc + "'");
			}
			MethodVisitor mv = this.cv.visitMethod(modifierOpcode, methodName,
					functionDesc, null, null);
			mv.visitCode();
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitVarInsn(Opcodes.ALOAD, 1);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, this.prefix
					+ methodName, functionDesc);
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitFieldInsn(Opcodes.GETFIELD, className, "lafWidgets",
					"Ljava/util/Set;");
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Set",
					"iterator", "()Ljava/util/Iterator;");
			mv.visitVarInsn(Opcodes.ASTORE, 2);
			Label l0 = new Label();
			mv.visitJumpInsn(Opcodes.GOTO, l0);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitVarInsn(Opcodes.ALOAD, 2);
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator",
					"next", "()Ljava/lang/Object;");
			mv.visitTypeInsn(Opcodes.CHECKCAST,
					"org/pushingpixels/lafwidget/LafWidget");
			mv.visitVarInsn(Opcodes.ASTORE, 3);
			mv.visitVarInsn(Opcodes.ALOAD, 3);
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE,
					"org/pushingpixels/lafwidget/LafWidget", methodName, "()V");
			mv.visitLabel(l0);
			mv.visitVarInsn(Opcodes.ALOAD, 2);
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator",
					"hasNext", "()Z");
			mv.visitJumpInsn(Opcodes.IFNE, l1);
			mv.visitInsn(Opcodes.RETURN);
			mv.visitMaxs(2, 4);
			mv.visitEnd();
		}

		/**
		 * Augments the <code>installUI</code> method that is assumed to always
		 * have a single parameter.
		 * 
		 * @param toSynthOriginal
		 *            Indication whether we need to create an empty (only call
		 *            to super()) implementation.
		 * @param className
		 *            Class name.
		 * @param superClassName
		 *            Super class name (relevant for generating empty
		 *            implementation).
		 * @param functionDesc
		 *            Function signature (using JNI style declaration). Example
		 *            for <code>void installUI(JButton button)</code>:
		 *            <code>(Ljavax/swing/JButton;)V</code>.
		 */
		public void augmentInstallUIMethod(boolean toSynthOriginal,
				String className, String superClassName, String functionDesc) {
			// Some ASM woodoo. The code below was generated by using
			// ASMifierClassVisitor.
			if (toSynthOriginal) {
				MethodVisitor mv = this.cv.visitMethod(Opcodes.ACC_PUBLIC,
						this.prefix + "installUI",
						"(Ljavax/swing/JComponent;)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassName,
						"installUI", "(Ljavax/swing/JComponent;)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(2, 2);
				mv.visitEnd();
			}

			MethodVisitor mv = this.cv.visitMethod(Opcodes.ACC_PUBLIC,
					"installUI", "(Ljavax/swing/JComponent;)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC,
					"org/pushingpixels/lafwidget/LafWidgetRepository",
					"getRepository",
					"()Lorg/pushingpixels/lafwidget/LafWidgetRepository;");
			mv.visitVarInsn(Opcodes.ALOAD, 1);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
					"org/pushingpixels/lafwidget/LafWidgetRepository",
					"getMatchingWidgets",
					"(Ljavax/swing/JComponent;)Ljava/util/Set;");
			mv.visitFieldInsn(Opcodes.PUTFIELD, className, "lafWidgets",
					"Ljava/util/Set;");
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitVarInsn(Opcodes.ALOAD, 1);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, this.prefix
					+ "installUI", "(Ljavax/swing/JComponent;)V");
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitFieldInsn(Opcodes.GETFIELD, className, "lafWidgets",
					"Ljava/util/Set;");
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Set",
					"iterator", "()Ljava/util/Iterator;");
			mv.visitVarInsn(Opcodes.ASTORE, 2);
			Label l0 = new Label();
			mv.visitJumpInsn(Opcodes.GOTO, l0);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitVarInsn(Opcodes.ALOAD, 2);
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator",
					"next", "()Ljava/lang/Object;");
			mv.visitTypeInsn(Opcodes.CHECKCAST,
					"org/pushingpixels/lafwidget/LafWidget");
			mv.visitVarInsn(Opcodes.ASTORE, 3);
			mv.visitVarInsn(Opcodes.ALOAD, 3);
			mv
					.visitMethodInsn(Opcodes.INVOKEINTERFACE,
							"org/pushingpixels/lafwidget/LafWidget",
							"installUI", "()V");
			mv.visitLabel(l0);
			mv.visitVarInsn(Opcodes.ALOAD, 2);
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator",
					"hasNext", "()Z");
			mv.visitJumpInsn(Opcodes.IFNE, l1);
			mv.visitInsn(Opcodes.RETURN);
			mv.visitMaxs(3, 4);
			mv.visitEnd();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.objectweb.asm.ClassAdapter#visitMethod(int,
		 * java.lang.String, java.lang.String, java.lang.String,
		 * java.lang.String[])
		 */
		@Override
		public MethodVisitor visitMethod(final int access, final String name,
				final String desc, final String signature,
				final String[] exceptions) {
			if (UiDelegateAugmenter.this.methodsToChange.contains(name)) {
				// possible candidate for weaving. Check if has __ already
				if (!this.existingMethods.contains(this.prefix + name)) {
					// effectively renames the existing method prepending __
					// to the name
					if (UiDelegateAugmenter.this.isVerbose)
						System.out.println("... renaming '" + name + "(" + desc
								+ ")' to '" + (this.prefix + name) + "'");
					return this.cv.visitMethod(access, this.prefix + name,
							desc, signature, exceptions);
				}
			}
			// preserve the existing method as is
			return this.cv.visitMethod(access, name, desc, signature,
					exceptions);
		}
	}

	/**
	 * Augments a single class with additional UI behaviour.
	 * 
	 * @param dir
	 *            Root directory for the library that contains the class.
	 * @param name
	 *            Fully-qualified class name.
	 * @throws AugmentException
	 *             If the augmentation process failed.
	 */
	protected synchronized void augmentClass(String dir, final String name) {
		if (this.isVerbose)
			System.out.println("Working on " + name);
		// gets an input stream to read the bytecode of the class
		String resource = dir + File.separator + name.replace('.', '/')
				+ ".class";

		Map<String, Method> methodsToAugment = new HashMap<String, Method>();
		try {
			ClassLoader cl = new URLClassLoader(new URL[] { new File(dir)
					.toURL() }, UiDelegateAugmenter.class.getClassLoader());
			Class<?> clazz = cl.loadClass(name);
			if (!ComponentUI.class.isAssignableFrom(clazz)) {
				if (this.isVerbose)
					System.out
							.println("Not augmenting resource, doesn't extend ComponentUI");
				return;
			}
			// Start iterating over all methods and see what do we
			// need to augment
			while (clazz != null) {
				Method[] methods = clazz.getDeclaredMethods();
				for (int i = 0; i < methods.length; i++) {
					Method method = methods[i];

					// this check is very important - store only the
					// first occurence (in the closest ancestor).
					if (methodsToAugment.containsKey(method.getName()))
						continue;

					if (this.methodsToChange.contains(method.getName())) {
						// check if it can be supported
						boolean isSupportedRetType = (void.class == method
								.getReturnType());
						Class<?>[] paramTypes = method.getParameterTypes();
						boolean isSupportedParamList = (paramTypes.length == 0)
								|| ((paramTypes.length == 1) && (JComponent.class
										.isAssignableFrom(paramTypes[0])));
						if (isSupportedRetType && isSupportedParamList)
							if (Modifier.isProtected(method.getModifiers())
									|| Modifier.isPublic(method.getModifiers())) {
								methodsToAugment.put(method.getName(), method);
							} else {
								if (isVerbose) {
									System.out
											.println("Not augmenting private "
													+ name + "."
													+ method.getName());
								}
							}
						else
							throw new AugmentException("Method '"
									+ method.getName() + "' in class '" + name
									+ "' has unsupported signature");
					}
				}
				clazz = clazz.getSuperclass();
			}
		} catch (Exception e) {
			throw new AugmentException(name, e);
		}

		Set<String> existingMethods = null;
		Set<String> existingFields = null;
		InputStream is = null;
		try {
			is = new FileInputStream(resource);
			ClassReader cr = new ClassReader(is);
			InfoClassVisitor infoAdapter = new InfoClassVisitor();
			cr.accept(infoAdapter, false);
			existingMethods = infoAdapter.getMethods();
			existingFields = infoAdapter.getFields();
		} catch (Exception e) {
			throw new AugmentException(name, e);
		} finally {
			try {
				is.close();
			} catch (IOException ioe) {
			}
		}

		// See if the' lafWidgets' field is already defined. In this
		// case the class is *not* augmented
		if (existingFields.contains("lafWidgets")) {
			if (this.isVerbose)
				System.out
						.println("Not augmenting resource, field 'lafWidgets' is present");
			return;
		}

		// Augment the class (overriding the existing file).
		byte[] b;
		try {
			is = new FileInputStream(resource);
			ClassReader cr = new ClassReader(is);
			ClassWriter cw = new ClassWriter(false);
			ClassVisitor cv = new AugmentClassAdapter(cw, existingMethods,
					existingFields, methodsToAugment);
			cr.accept(cv, false);
			b = cw.toByteArray();
		} catch (Exception e) {
			throw new AugmentException(name, e);
		} finally {
			try {
				is.close();
			} catch (IOException ioe) {
			}
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(resource);
			fos.write(b);
			if (this.isVerbose)
				System.out.println("Updated resource " + resource);
		} catch (Exception e) {
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {
				}
			}
		}
	}

	/**
	 * Creates the new augmenter.
	 */
	public UiDelegateAugmenter() {
		super();

		this.methodsToChange = new HashSet<String>();
		this.methodsToChange.add("installUI");
		this.methodsToChange.add("installDefaults");
		this.methodsToChange.add("installComponents");
		this.methodsToChange.add("installListeners");
		this.methodsToChange.add("uninstallUI");
		this.methodsToChange.add("uninstallDefaults");
		this.methodsToChange.add("uninstallComponents");
		this.methodsToChange.add("uninstallListeners");
	}

	/**
	 * Processes a single file or a directory, augmenting all relevant classes.
	 * 
	 * @param toStrip
	 *            The leading prefix to strip from the file names. Is used to
	 *            create fully-qualified class name.
	 * @param file
	 *            File resource (can point to a single file or to a directory).
	 * @param pattern
	 *            Pattern to apply to the file name (of the single file). If the
	 *            file name matches the pattern, the relevant class is
	 *            augmented.
	 * @throws AugmentException
	 *             If the augmentation process failed.
	 */
	public void process(String toStrip, File file, Pattern pattern)
			throws AugmentException {
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (int i = 0; i < children.length; i++) {
				this.process(toStrip, children[i], pattern);
			}
		} else {
			Matcher m = pattern.matcher(file.getName());
			if (m.matches()) {
				String className = file.getAbsolutePath().substring(
						toStrip.length() + 1);
				className = className.replace(File.separatorChar, '.');
				this.augmentClass(toStrip, className.substring(0, className
						.length() - 6));
			}
		}
	}

	/**
	 * Sets the verbosity.
	 * 
	 * @param isVerbose
	 *            New value for augmentation process verbosity.
	 */
	public void setVerbose(boolean isVerbose) {
		this.isVerbose = isVerbose;
	}

	/**
	 * Test method.
	 * 
	 * @param args
	 * @throws AugmentException
	 */
	public static void main(final String args[]) throws AugmentException {
		if (args.length == 0) {
			System.out
					.println("Usage : java ... UiDelegateAugmenter [-verbose] [-pattern class_pattern] file_resource");
			System.out
					.println("\tIf -verbose option is specified, the augmenter prints out its actions.");
			System.out
					.println("\tIf -pattern option is specified, its value is used as a wildcard "
							+ "for matching the classes for augmentation.");
			System.out
					.println("\tThe last parameter can point to either a file or a directory. "
							+ "The directory should be the root directory for classes.");
			return;
		}

		UiDelegateAugmenter uiDelegateAugmenter = new UiDelegateAugmenter();

		int argNum = 0;
		String pattern = ".*UI\u002Eclass";
		while (true) {
			String currArg = args[argNum];
			if ("-verbose".equals(currArg)) {
				uiDelegateAugmenter.setVerbose(true);
				argNum++;
				continue;
			}
			if ("-pattern".equals(currArg)) {
				argNum++;
				pattern = args[argNum];
				argNum++;
				continue;
			}
			break;
		}

		Pattern p = Pattern.compile(pattern);

		File starter = new File(args[argNum]);
		uiDelegateAugmenter.process(starter.getAbsolutePath(), starter, p);
	}
}
