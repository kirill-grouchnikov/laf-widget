package test.check.asm;

import javax.swing.UIDefaults;
import javax.swing.plaf.basic.BasicLookAndFeel;

public class TestLookAndFeel extends BasicLookAndFeel {

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getID() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean isNativeLookAndFeel() {
		return false;
	}

	@Override
	public boolean isSupportedLookAndFeel() {
		return false;
	}

	@Override
	protected void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);
//		
//		table.put("ButtonUI", "my.pack.ButtonUI");
//		table.put("CheckboxUI", "my.pack.CheckboxUI");
//		table.put("ToolbarUI", "my.pack.ToolbarUI");
//		table.put("PanelUI", "my.pack.PanelUI");
//		table.put("MenuBarUI", "my.pack.MenuBarUI");
	}
}
