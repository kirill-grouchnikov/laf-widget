package test.check.asm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.plaf.basic.BasicButtonUI;

import org.pushingpixels.lafwidget.animation.effects.GhostingListener;

public class TestGhostListenerUI extends BasicButtonUI {
	/**
	 * Model change listener for ghost image effects.
	 */
	private GhostingListener ghostModelChangeListener;

	/**
	 * Property change listener. Listens on changes to the
	 * {@link AbstractButton#MODEL_CHANGED_PROPERTY} property.
	 */
	protected PropertyChangeListener ghostPropertyListener;

	protected void __icon__installListeners(AbstractButton b) {
		super.installListeners(b);
	}

	protected void installListeners(final AbstractButton b) {
		this.__icon__installListeners(b);

		this.ghostPropertyListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (AbstractButton.MODEL_CHANGED_PROPERTY.equals(evt
						.getPropertyName())) {
					if (ghostModelChangeListener != null)
						ghostModelChangeListener.unregisterListeners();
					ghostModelChangeListener = new GhostingListener(b, b
							.getModel());
					ghostModelChangeListener.registerListeners();
				}
			}
		};
		b.addPropertyChangeListener(this.ghostPropertyListener);

		this.ghostModelChangeListener = new GhostingListener(b, b
				.getModel());
		this.ghostModelChangeListener.registerListeners();
	}

	protected void __icon__uninstallListeners(AbstractButton b) {
		super.uninstallListeners(b);
	}

	@Override
	protected void uninstallListeners(AbstractButton b) {
		b.removePropertyChangeListener(this.ghostPropertyListener);
		this.ghostPropertyListener = null;

		this.ghostModelChangeListener.unregisterListeners();
		this.ghostModelChangeListener = null;

		this.__icon__uninstallListeners(b);
	}
}
