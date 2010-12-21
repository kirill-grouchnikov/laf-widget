mkdir TMP

cd TMP

"C:\Program Files\Java\jdk1.5.0_11\/bin/jar" xf ../napkinlaf.jar

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar org.jvnet.lafwidget.ant.UiDelegateAugmenter -verbose -pattern .*UI\u002Eclass .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.IconGhostingAugmenter -verbose -class net.sourceforge.napkinlaf.NapkinButtonUI -method paintIcon .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.IconGhostingAugmenter -verbose -class net.sourceforge.napkinlaf.NapkinToggleButtonUI -method paintIcon .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.sourceforge.napkinlaf.NapkinPanelUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.sourceforge.napkinlaf.NapkinMenuBarUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.sourceforge.napkinlaf.NapkinMenuUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.sourceforge.napkinlaf.NapkinScrollBarUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.sourceforge.napkinlaf.NapkinToolBarUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.sourceforge.napkinlaf.NapkinDesktopPaneUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/jar" cfm ../augmented/napkinlaf.jar META-INF/manifest.mf .

cd ..

rmdir TMP /s /q