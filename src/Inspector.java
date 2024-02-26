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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;

import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 *  Class for the dialog containing the image name, width and height.
 *  @author İ. K. Bilir (Abes400)
 *  @since 1.0
 */
public class Inspector extends JDialog {
    // Initializing the strings
    private final static JLabel currentFile = new JLabel("Select a file..."), // Shows the file open.
                                width = new JLabel(""),
                                height = new JLabel("");

    // Initializing the buttons
    public static JButton open = new JButton("Open..."),
                          saveAsSLC = new JButton("Save as SLIC"),
                          saveAsBMP = new JButton("Save as BITMAP");

    /**
     * Creates a new Inspector dialog.
     * @since 1.0
     */
    public Inspector() {
        // Initializing the dialog base
        setTitle("Inspector");
        setPreferredSize(new Dimension(280, 190));
        setResizable(false);

        // Initializing Layouts
        JPanel subGri = new JPanel(), innerGri = new JPanel(), middle = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        subGri.setLayout(new GridLayout(2, 1));
        innerGri.setLayout(new GridLayout(1, 2));
        middle.setLayout(new GridBagLayout());
        setLayout(new BorderLayout());


        // Initiating a new Label with the image file containing the application's logo.

        try {
            JLabel logo = new JLabel( new ImageIcon(
                    ImageIO.read(Objects.requireNonNull(
                            getClass().getResource("/Artworks/emblem.png")))
                            .getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
            logo.setBounds(0, 0, 25, 25);
            add(logo);
        } catch (IOException e) { throw new RuntimeException(e); }


        // Setting Button props.
        open.setFocusable(false);

        saveAsSLC.setFocusable(false);
        saveAsSLC.setEnabled(false);

        saveAsBMP.setFocusable(false);
        saveAsBMP.setEnabled(false);

        add(middle, BorderLayout.CENTER);
        add(subGri, BorderLayout.SOUTH);

        // Adding components
        gbc.gridx = 1;
        gbc.gridy = 1;
        middle.add(currentFile, gbc);       gbc.gridy = 2;
        middle.add(new JPanel(), gbc);      gbc.gridy = 3;
        middle.add(width, gbc);             gbc.gridy = 4;
        middle.add(height, gbc);

        subGri.add(open);
        subGri.add(innerGri);
        innerGri.add(saveAsSLC);
        innerGri.add(saveAsBMP);
        pack();
    }

    /**
     * Simply call this function so that the inspector can view the filename, width and height.
     * @param name Name of the file you've just opened
     * @param columns Width of the Column count of the image. (You can pass colCount from the SLICCodec class)
     * @param rows Height of the Row count of the image. (You can pass rowCount from the SLICCodec class)
     * @since 1.0
     */
    public void setInformation(String name, int columns, int rows) {
        currentFile.setText("Currently viewing: " + name);

        width.setText("Width: " + columns);
        height.setText("Height: " + rows);

        saveAsSLC.setEnabled(true);
        saveAsBMP.setEnabled(true);
        open.setEnabled(true);
    }

    /**
     * Simply call this function to avoid malfunctioning by not allowing user to open another file while the .slc file
     * is being uncompressed.
     * @param name Name of the file opening at the moment
     * @since 1.0
     */
    public void waitOpening(String name) {
        currentFile.setText("Opening " + name + "...");

        width.setText("");
        height.setText("");

        saveAsSLC.setEnabled(false);
        saveAsBMP.setEnabled(false);
        open.setEnabled(false);
    }

    /**
     * Simply call this function to avoid malfunctioning by not allowing user to open another file while the .slc file
     * is being compressed.
     * @since 1.0
     */
    public void waitCompressing() {
        currentFile.setText("Compressing...");

        width.setText("");
        height.setText("");

        saveAsSLC.setEnabled(false);
        saveAsBMP.setEnabled(false);
        open.setEnabled(false);
    }

    /**
     * Simply call this function to avoid malfunctioning by not allowing user to open save unsupported file.
     * @since 1.0
     */
    public void error() {
        currentFile.setText("Unsupported file.");

        width.setText("");
        height.setText("");

        saveAsSLC.setEnabled(false);
        saveAsBMP.setEnabled(false);
        open.setEnabled(true);
    }
}
