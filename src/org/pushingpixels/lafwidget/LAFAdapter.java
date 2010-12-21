package org.pushingpixels.lafwidget;

import java.awt.EventQueue;
import java.util.Set;
import javax.swing.*;
import java.beans.*;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.lafwidget.*;

public class LAFAdapter {
    /**The listener for global LAF changes.*/
    private static ReinitListener reinitListener;
    /**The listener for component LAF updates.*/
    private static InternalUIListener internalListener;
    /**Have we already initialised?*/
    private static boolean initialised=false;
    /**The table of delegate classes for the current LAF.*/
    private static UIDefaults uiDelegates;
    /**The list of all UI classes.
     *Taken from https://laf-widget.dev.java.net/docs/all-component-ui-ids.txt
     */
    private final static String[] UI_CLASSNAMES={
        "ButtonUI","CheckBoxUI","CheckBoxMenuItemUI","ColorChooserUI","ComboBoxUI","DesktopIconUI",
        "DesktopPaneUI","EditorPaneUI","FormattedTextFieldUI","InternalFrameUI","LabelUI","ListUI","MenuUI",
        "MenuBarUI","MenuItemUI","OptionPaneUI","PanelUI","PasswordFieldUI","PopupMenuUI","PopupMenuSeparatorUI",
        "ProgressBarUI","RadioButtonUI","RadioButtonMenuItemUI","RootPaneUI","ScrollBarUI","ScrollPaneUI",
        "SeparatorUI","SliderUI","SpinnerUI","SplitPaneUI","TabbedPaneUI","TableUI","TableHeaderUI","TextAreaUI",
        "TextFieldUI","TextPaneUI","ToggleButtonUI","ToolBarUI","ToolBarSeparatorUI","ToolTipUI","TreeUI","ViewportUI"};
    private final static String LAF_PROPERTY = "Widgeted_LAFS";
    
    /**Returns the UI as normal, but intercepts the call, so a
     * listener can be attached. This is called internally from Swing code
     * to create the UI delegate for a component.
     *<b>THIS METHOD SHOULDN'T BE CALLED FROM USER CODE!</b>
     */
    public static ComponentUI createUI(JComponent c) {
        if (c == null || initialised == false) //something isn't right -> bail
            return null;
        //Now we use the same discovery mechanism to find the real createUI method
        ComponentUI uiObject = uiDelegates.getUI(c);
        //System.out.println("Creating UI: "+c.getClass()+" "+Integer.toHexString(System.identityHashCode(c)));
        uninstallLafWidgets(c);
        //here we need to check we don't add a second listener. This happens
        //if createUI gets called a second time, as when creating JDesktopIcons
        if (!isPropertyListening(c))
            c.addPropertyChangeListener("UI",internalListener);
        return uiObject; //return the actual UI delegate
    }
        
    /**Tests if the property listener has already been attached to a component
     *@param c the component to check
     *@return true if the listener is already present
     */
    private static boolean isPropertyListening(JComponent c) {
        PropertyChangeListener[] pc=c.getPropertyChangeListeners();
        if (pc.length==0) //common-case
            return false;
        for (int i=0,ilen=pc.length;i<ilen;i++) {
            //Property listeners are usually proxied but test explicitly just in case
            if (pc[i] instanceof PropertyChangeListenerProxy &&
                    ((PropertyChangeListenerProxy)pc[i]).getListener()==internalListener)
                return true;
            else if (pc[i]==internalListener)
                return true;
        }
        return false;
    }
    
    private static class InternalUIListener implements PropertyChangeListener {
        /**UI can change at any point, so we need to listen for these
         * events. This property fires AFTER the UI change.
         */
        public void propertyChange(PropertyChangeEvent evt) {
            JComponent c=(JComponent)evt.getSource();
            // Remove old listeners that was installed when createUI was called
            c.removePropertyChangeListener("UI",internalListener);
            installLafWidgets(c);  //here we do the install on the LAF
        }       
    }
    
    /**Installs the available LAF-Widgets onto the given JComponent
     *and adds a mapping to the set of widgeted components if any were found.
     *If the LAF indicates it is self-widgeting then nothing is done.
     *@param c the component to widgetise
     */
    private static void installLafWidgets(JComponent c) {
        //test if this LAF uses needs LAFWidget integration
        if (LafWidgetRepository.getRepository().getLafSupport()
        .getClass().equals(LafWidgetSupport.class)) {
            Set<LafWidget> lafWidgets=LafWidgetRepository.getRepository().getMatchingWidgets(c);
            if (lafWidgets.size()>0) {//if a new UI has been set
                for (LafWidget lw : lafWidgets) {
                    lw.installUI();
                    lw.installComponents();
                    lw.installDefaults();
                    lw.installListeners();
                }
                c.putClientProperty(LAF_PROPERTY,lafWidgets); //stash the list of installed widgets
            }
        }
    }
    
    /**Removes any installed widgets from the component shown. If a mapping
     *exists which indicates this component had widgets installed then they
     *are removed here and the mapping also removed.
     *@param c the component to uninstall widgets from
     */
    private static void uninstallLafWidgets(JComponent c) {
        //if there were previously widgets installed, then uninstall them
        Set<LafWidget> lafWidgets=(Set<LafWidget>)c.getClientProperty(LAF_PROPERTY);
        if (lafWidgets!=null) {
            for (LafWidget lw : lafWidgets) {
                lw.uninstallListeners();
                lw.uninstallDefaults();
                lw.uninstallComponents();
                lw.uninstallUI();
            }
            c.putClientProperty(LAF_PROPERTY,null); //remove widget set
        }
    }
    
    /**This initialises the Widgeting system. All future components created will
     *be widgeted. This code maybe called from any thread and ensures that the
     *actual initialisation is done on the Event Thread. If called from another thread,
     *this method will block until the initialisation is complete.
     */
    public static void startWidget() {
        widget(true);
    }
    
    /**This tears-down the widgeting system. Calling this method prevents any
     *further components being widgetised. Any existing components with widgets
     *will continue to have them. All system hooks will be removed and the widgeting
     *system will stop attaching to new components. This method is safe to call
     *from any thread, as the work will be done on the Event Thread. If called from
     *another thread, this method will block until the initialisation is complete.
     */
    public static void stopWidget() {
        widget(false);
    }
    
    /**Private helper to start or stop the widgeting. EDT checks present here.
     *@param enable whether to start or stop the widgeting
     */
    private static void widget(boolean enable) {
        Init init = new Init(enable);
        if (EventQueue.isDispatchThread()) {
            init.run();
        } else {// This code must run on the EDT for data visibility reasons
            try {
                EventQueue.invokeAndWait(init);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    /**Inserts all the substitute LAF pairs into the UIDefaults table. This also
     *places a LAF change listener on the UIManager. This is to cause a re-initialisation
     *when the LAF changes so new components created after a global LAF change will
     *still get widgets.
     */
    private static class Init implements Runnable {
        /**Sets whether we should be setting up or tearing down the widgeting.*/
        private final boolean enable; //final so it doesn't matter where this initialises from (JSR-133)
        
        private Init(final boolean enable) {
            this.enable=enable;
        }
        
        public void run() {
            if (!EventQueue.isDispatchThread())
                throw new IllegalStateException("This must be run on the EDT");
            try { //do the setup process or removal process
                if (enable)
                    setup();
                else
                    tearDown();
            } catch (Exception e) {
                initialised=false;
                uiDelegates=null;
                internalListener=null;
                reinitListener=null;
                throw new RuntimeException(e);
            }
        }
        
        public static void setup() throws Exception {
            //check we don't initialise twice
            if (initialised)
                return;
            //We use the LAF defaults table so we don't mess with the developer
            //table
            reinitListener = new ReinitListener();
            internalListener = new InternalUIListener();
            uiDelegates=new UIDefaults(); //stores the actual UI delegates
            UIDefaults defaults = UIManager.getLookAndFeelDefaults();
            //Store the class for each delegate locally and replace the Swing delegate
            for (String uiClassID : UI_CLASSNAMES) {
                uiDelegates.put(uiClassID,defaults.getString(uiClassID));
                defaults.put(uiClassID,LAFAdapter.class.getName());
            }
            //listen for global LAF changes
            UIManager.addPropertyChangeListener(reinitListener);
            initialised=true;
        }
        
        public static void tearDown() throws Exception {
            if (!initialised)
                return;
            UIManager.removePropertyChangeListener(reinitListener);
            //reset the current UI which will overwrite all our values
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
            uiDelegates=null;
            reinitListener=null;
            internalListener=null;
            initialised=false;
        }
    }
    
    /**
     * Listens for Look and Feel changes and re-initialises the
     * class. This fires from UIManager after a LAF change.
     */
    private static class ReinitListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if ("lookAndFeel".equals(evt.getPropertyName())) {
                // The look and feel was changed so we need to re-insert
                // our hook into the new UIDefaults map, first de-initialise
                // Don't want to call tearDown() as we're already in a LAF change.
                UIManager.removePropertyChangeListener(reinitListener);
                initialised=false;
                uiDelegates=null;
                reinitListener=null;
                internalListener=null;
                startWidget();
            }
        }
    }
}