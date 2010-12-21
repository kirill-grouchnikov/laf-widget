mkdir TMP

cd TMP

"C:\Program Files\Java\jdk1.5.0_11\/bin/jar" xf ../PgsLookAndFeel.jar

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar org.jvnet.lafwidget.ant.LafMainClassAugmenter -verbose -main com.pagosoft.plaf.PgsLookAndFeel -delegates ColorChooserUI;DesktopIconUI;DesktopPaneUI;InternalFrameUI;ListUI;OptionPaneUI;PanelUI;PopupMenuUI;PopupMenuSeparatorUI;RootPaneUI;SeparatorUI;SliderUI;SpinnerUI;TableUI;TableHeaderUI;TextPaneUI;ToolBarSeparatorUI;TreeUI;ViewportUI -dir .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar org.jvnet.lafwidget.ant.UiDelegateAugmenter -verbose -pattern .*UI\u002Eclass .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.IconGhostingAugmenter -verbose -class com.pagosoft.plaf.PgsButtonUI -method paintIcon .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.IconGhostingAugmenter -verbose -class com.pagosoft.plaf.PgsToggleButtonUI -method paintIcon .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class com.pagosoft.plaf.__Forwarding__PanelUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class com.pagosoft.plaf.PgsMenuBarUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class com.pagosoft.plaf.PgsMenuUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class com.pagosoft.plaf.PgsScrollBarUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class com.pagosoft.plaf.PgsToolBarUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/java" -classpath ../../drop/laf-widget.jar;../../lib/asm-all-2.2.2.jar;../../lib/ant.jar org.jvnet.lafwidget.ant.ContainerGhostingAugmenter -verbose -class com.pagosoft.plaf.__Forwarding__DesktopPaneUI .

"C:\Program Files\Java\jdk1.5.0_11\/bin/jar" cfm ../augmented/PgsLookAndFeel.jar META-INF/manifest.mf .

cd ..

rmdir TMP /s /q