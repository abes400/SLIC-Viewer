/*
 * SLIC Viewer - An application for viewing SLIC and BITMAP images and compressing BITMAP images
 * using SLIC compression standards.
 *
 * This file is part of SLIC Viewer.
 *
 * SLIC Viewer is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * SLIC Viewer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with SLIC Viewer. If not, see
 * <https://www.gnu.org/licenses/>.
 * */
// Written by İ.K. Bilir (Abes400)

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import static javax.swing.BorderFactory.createEmptyBorder;

/**
 *  Class for the dialog containing the image name, width and height.
 *  @author İ. K. Bilir (Abes400)
 *  @since 1.0
 */
public class CanvasWindow extends JFrame{

    private final static JLabel imageLabel = new JLabel();
    private final static ImageIcon icon = new ImageIcon();

    private final static JScrollPane scrollPane = new JScrollPane();
    private final static int screenColCount = Toolkit.getDefaultToolkit().getScreenSize().width,
                             screenRowCount = Toolkit.getDefaultToolkit().getScreenSize().height;

    /**
     * Creates a new CanvasWindow window.
     * @since 1.0
     */
    public CanvasWindow() {
        // Initiating Canvas Window Base
        setMinimumSize(new Dimension(300, 300));
        setSize(600, 500);
        setTitle("Open a BMP or SLC file and it will show up here.");
        setLayout(new BorderLayout());

        // Adding the scroll pane for scrollability
        scrollPane.setBorder(createEmptyBorder());
        imageLabel.setBounds(0, 0, 800, 800);
        scrollPane.getViewport().add(imageLabel);

        add(scrollPane, BorderLayout.CENTER);

        WindowActions.centerWindow(this);
        setVisible(true);
    }

    /**
     * Simply call this function and any Buffered Image shows up on the Canvas Window.
     * @param image The BufferedImage object you want the Canvas Window to view.
     * @since 1.0
     */
    public void showImage(BufferedImage image) {
        // Removing and re-adding are crucial so that user won't
        // have to resize the window for image to show up.

        // Viewing the buffered image to the scrollpane
        scrollPane.getViewport().remove(imageLabel);
        icon.setImage(image);
        imageLabel.setIcon(icon);
        scrollPane.getViewport().add(imageLabel);

        // Resizing the window according to the size of the image.
        if(SLICCodec.colCount <= screenColCount)
            setSize(SLICCodec.colCount + 50, getHeight());
        if(SLICCodec.rowCount <= screenRowCount)
            setSize(getWidth(), SLICCodec.rowCount + 50);

        WindowActions.centerWindow(this);

        if(FileOperations.path != null)
            setTitle(FileOperations.filename);
    }
}
