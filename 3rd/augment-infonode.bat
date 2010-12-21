mkdir TMP

cd TMP

"C:\Program Files\Java\jdk1.5.0_11\/bin/jar" xf ../ilf-gpl.jar

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar org.jvnet.lafwidget.ant.LafMainClassAugmenter -verbose -main net.infonode.gui.laf.InfoNodeLookAndFeel -delegates ButtonUI;CheckBoxUI;CheckBoxMenuItemUI;ColorChooserUI;DesktopIconUI;DesktopPaneUI;EditorPaneUI;FormattedTextFieldUI;LabelUI;ListUI;MenuUI;MenuBarUI;OptionPaneUI;PanelUI;PasswordFieldUI;PopupMenuUI;PopupMenuSeparatorUI;ProgressBarUI;RadioButtonUI;RadioButtonMenuItemUI;RootPaneUI;ScrollBarUI;ScrollPaneUI;SeparatorUI;SliderUI;SpinnerUI;TabbedPaneUI;TableUI;TableHeaderUI;TextAreaUI;TextFieldUI;TextPaneUI;ToggleButtonUI;ToolBarUI;ToolBarSeparatorUI;ToolTipUI;TreeUI;ViewportUI; -dir .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar org.jvnet.lafwidget.ant.UiDelegateAugmenter -verbose -pattern .*UI\u002Eclass .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.IconGhostingAugmenter -verbose -class net.infonode.gui.laf.__Forwarding__ButtonUI -method paintIcon .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.IconGhostingAugmenter -verbose -class net.infonode.gui.laf.__Forwarding__ToggleButtonUI -method paintIcon .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.infonode.gui.laf.__Forwarding__PanelUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.infonode.gui.laf.__Forwarding__MenuBarUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.infonode.gui.laf.__Forwarding__MenuUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.infonode.gui.laf.__Forwarding__ScrollBarUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.infonode.gui.laf.__Forwarding__ToolBarUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class net.infonode.gui.laf.__Forwarding__DesktopPaneUI .



"C:\Program Files\Java\jdk1.5.0_11\/bin/jar" cfm ../augmented/ilf-gpl.jar META-INF/manifest.mf .

cd ..

rmdir TMP /s /q