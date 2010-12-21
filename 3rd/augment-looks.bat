mkdir TMP

cd TMP

"C:\Program Files\Java\jdk1.5.0_11\/bin/jar" xf ../looks-2.1.3.jar

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../looks-2.1.3.jar;../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar org.jvnet.lafwidget.ant.LafMainClassAugmenter -verbose -main com.jgoodies.looks.plastic.PlasticXPLookAndFeel -delegates CheckBoxUI;ColorChooserUI;DesktopIconUI;DesktopPaneUI;EditorPaneUI;FormattedTextFieldUI;LabelUI;ListUI;PanelUI;ProgressBarUI;RadioButtonUI;RootPaneUI;SliderUI;TableUI;TableHeaderUI;TextFieldUI;TextPaneUI;ToolTipUI;ViewportUI; -dir .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar org.jvnet.lafwidget.ant.UiDelegateAugmenter -verbose -pattern .*UI\u002Eclass .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.IconGhostingAugmenter -verbose -class com.jgoodies.looks.plastic.PlasticButtonUI -method paintIcon .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.IconGhostingAugmenter -verbose -class com.jgoodies.looks.plastic.PlasticToggleButtonUI -method paintIcon .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class com.jgoodies.looks.plastic.__Forwarding__PanelUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class com.jgoodies.looks.plastic.PlasticMenuBarUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class com.jgoodies.looks.plastic.PlasticMenuUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class com.jgoodies.looks.plastic.PlasticScrollBarUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class com.jgoodies.looks.plastic.PlasticToolBarUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class com.jgoodies.looks.plastic.__Forwarding__DesktopPaneUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/jar" cfm ../augmented/looks-2.1.3.jar META-INF/manifest.mf .

cd ..

rmdir TMP /s /q