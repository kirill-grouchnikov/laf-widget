/*
 * ImageLabel.java
 *
 * Created on November 22, 2006, 9:53 AM
 *
 * Copyright 2006-2007 Nigel Hughes 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/
 * licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License.
 */

package org.pushingpixels.lafwidget.contrib.blogofbug.swing.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * A simple component that scales an image to fit inside the size of the component.
 * @author nigel
 */
public class ImageLabel extends JLabel{
    /**
     * The image that ends up getting scaled
     */
    protected ImageIcon imageIcon = null;

    /**
     * Creates a new instance of ImageLabel. The prefered width and height will 
     * be set to the dimensions of the image
     *
     * @param icon The image to display
     */
    public ImageLabel(ImageIcon icon) {
        super(icon);
        this.imageIcon = icon;
        setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
    }
    
    /**
     * Creates a new instance of ImageLabel, setting the preferred rendering size to 
     * the supplied dimensions
     *
     * @param icon The image to place on the label
     * @param width The prefered width
     * @param height The prefered height
     */
    public ImageLabel(ImageIcon icon, int width, int height){
        this(icon);
        setPreferredSize(new Dimension(width,height));
    }
    
    /**
     * Paints the label scaling the image to the appropriate size 
     * @param graphics The graphics context
     */
    public void paintComponent(Graphics graphics) {
        Image image = this.imageIcon.getImage();
        ImageObserver observer = imageIcon.getImageObserver();
        ((Graphics2D)graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D)graphics).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(image, 0,0,getWidth(),getHeight(),0,0,image.getWidth(observer),image.getHeight(observer),observer);
    }
}
