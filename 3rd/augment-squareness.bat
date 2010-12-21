mkdir TMP

cd TMP

"C:\Program Files\Java\jdk1.5.0_11\/bin/jar" xf ../squareness.jar

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar org.jvnet.lafwidget.ant.LafMainClassAugmenter -verbose -main net.beeger.squareness.SquarenessLookAndFeel -delegates CheckBoxMenuItemUI;ColorChooserUI;DesktopIconUI;DesktopPaneUI;EditorPaneUI;FormattedTextFieldUI;LabelUI;ListUI;MenuUI;MenuBarUI;MenuItemUI;OptionPaneUI;PanelUI;PasswordFieldUI;PopupMenuUI;PopupMenuSeparatorUI;RadioButtonMenuItemUI;ScrollBarUI;SeparatorUI;TableUI;TableHeaderUI;TextAreaUI;TextFieldUI;TextPaneUI;ToolBarSeparatorUI;ToolTipUI;TreeUI;ViewportUI -dir .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar org.jvnet.lafwidget.ant.UiDelegateAugmenter -verbose -pattern .*UI\u002Eclass .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.IconGhostingAugmenter -verbose -class net.beeger.squareness.delegate.SquarenessButtonUI -method paintIcon .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.IconGhostingAugmenter -verbose -class net.beeger.squareness.delegate.SquarenessToggleButtonUI -method paintIcon .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.beeger.squareness.__Forwarding__PanelUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.beeger.squareness.__Forwarding__MenuBarUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.beeger.squareness.__Forwarding__MenuUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.beeger.squareness.__Forwarding__ScrollBarUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.beeger.squareness.delegate.SquarenessToolBarUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.beeger.squareness.__Forwarding__DesktopPaneUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/jar" cfm ../augmented/squareness.jar META-INF/manifest.mf .

cd ..

rmdir TMP /s /q