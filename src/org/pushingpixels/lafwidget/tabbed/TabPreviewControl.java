package org.pushingpixels.lafwidget.tabbed;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.*;

import org.pushingpixels.lafwidget.animation.AnimationConfigurationManager;
import org.pushingpixels.lafwidget.utils.ShadowPopupBorder;
import org.pushingpixels.trident.Timeline;

/**
 * Control to display the a single tab preview.
 * 
 * @author Kirill Grouchnikov
 */
public class TabPreviewControl extends JPanel {
	/**
	 * Label for the tab icon.
	 */
	protected JLabel iconLabel;

	/**
	 * Label for the tab title.
	 */
	protected JLabel titleLabel;

	/**
	 * Panel for the tab preview image.
	 */
	protected JPanel previewImagePanel;

	/**
	 * The preview image itself.
	 */
	protected BufferedImage previewImage;

	/**
	 * The associated tabbed pane.
	 */
	protected JTabbedPane tabPane;

	private float alpha;

	private float zoom;

	/**
	 * Creates a tab preview control.
	 * 
	 * @param tabPane
	 *            Tabbed pane.
	 * @param tabIndex
	 *            Tab index.
	 */
	public TabPreviewControl(final JTabbedPane tabPane, final int tabIndex) {
		this.tabPane = tabPane;
		this.setLayout(new TabPreviewControlLayout());
		this.iconLabel = new JLabel(tabPane.getIconAt(tabIndex));
		this.titleLabel = new JLabel(tabPane.getTitleAt(tabIndex));
		this.titleLabel
				.setFont(this.titleLabel.getFont().deriveFont(Font.BOLD));

		// the panel with the preview image - perhaps use JLabel
		// instead?
		this.previewImagePanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				paintTabThumbnail(g);
			};
		};
		this.add(this.iconLabel);
		this.add(this.titleLabel);
		this.add(this.previewImagePanel);

		final boolean isSelected = (tabPane.getSelectedIndex() == tabIndex);
		Border innerBorder = isSelected ? new LineBorder(Color.black, 2)
				: new LineBorder(Color.black, 1);
		this
				.setBorder(new CompoundBorder(new ShadowPopupBorder(),
						innerBorder));

		this.alpha = 0.0f;
		this.zoom = 1.0f;
	}

	/**
	 * Paints the tab thumbnail on the specified graphics context.
	 * 
	 * @param g
	 *            Graphics context.
	 */
	public synchronized void paintTabThumbnail(Graphics g) {
		if (TabPreviewControl.this.previewImage != null) {
			int pw = TabPreviewControl.this.previewImage.getWidth();
			int ph = TabPreviewControl.this.previewImage.getHeight();
			int w = this.previewImagePanel.getWidth();
			int h = this.previewImagePanel.getHeight();

			Graphics2D g2 = (Graphics2D) g.create();
			g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
			int dx = (w - pw) / 2;
			int dy = (h - ph) / 2;
			g2.drawImage(TabPreviewControl.this.previewImage, dx, dy, null);
			g2.dispose();
		}
	}

	/**
	 * Stes the tab index.
	 * 
	 * @param tabIndex
	 *            Tab index.
	 */
	public void setTabIndex(int tabIndex) {
		this.iconLabel.setIcon(this.tabPane.getIconAt(tabIndex));
		this.titleLabel.setText(this.tabPane.getTitleAt(tabIndex));
		final boolean isSelected = (this.tabPane.getSelectedIndex() == tabIndex);
		Border innerBorder = isSelected ? new LineBorder(Color.black, 2)
				: new LineBorder(Color.black, 1);
		this
				.setBorder(new CompoundBorder(new ShadowPopupBorder(),
						innerBorder));
	}

	/**
	 * Layout for the tab preview control.
	 * 
	 * @author Kirill Grouchnikov
	 */
	protected class TabPreviewControlLayout implements LayoutManager {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String,
		 * java.awt.Component)
		 */
		public void addLayoutComponent(String name, Component comp) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
		 */
		public void removeLayoutComponent(Component comp) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
		 */
		public void layoutContainer(Container parent) {
			int width = parent.getWidth();
			int height = parent.getHeight();

			Insets insets = TabPreviewControl.this.getInsets();
			TabPreviewControl.this.iconLabel.setBounds(insets.left + 1,
					insets.top + 1, 16, 16);
			TabPreviewControl.this.titleLabel
					.setBounds(insets.left + 18, insets.top + 1, width - 18
							- insets.left - insets.right, 16);
			TabPreviewControl.this.previewImagePanel.setBounds(insets.left + 1,
					insets.top + 17, width - insets.left - insets.right - 2,
					height - 17 - insets.top - insets.bottom);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
		 */
		public Dimension minimumLayoutSize(Container parent) {
			return parent.getSize();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
		 */
		public Dimension preferredLayoutSize(Container parent) {
			return this.minimumLayoutSize(parent);
		}
	}

	/**
	 * Sets the tab preview thumbnail.
	 * 
	 * @param previewImage
	 *            Tab preview thumbnail.
	 * @param toAnimate
	 *            if <code>true</code>, the image will be faded-in.
	 */
	public void setPreviewImage(BufferedImage previewImage, boolean toAnimate) {
		this.previewImage = previewImage;
		if (toAnimate) {
			Timeline fadeTimeline = new Timeline(this);
			AnimationConfigurationManager.getInstance().configureTimeline(
					fadeTimeline);
			fadeTimeline.addPropertyToInterpolate("alpha", 0.0f, 1.0f);
			fadeTimeline.play();
		}
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
		this.repaint();
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	public float getZoom() {
		return zoom;
	}
}