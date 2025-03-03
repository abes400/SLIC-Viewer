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

import com.formdev.flatlaf.icons.*;

import java.awt.*;
import java.awt.Font;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javax.swing.*;


/**
 *  Class for the dialog containing the image name, width and height.
 *  @author İ. K. Bilir (Abes400)
 *  @since 1.0
 */
public class Inspector extends JDialog {
    // Initializing the strings

    private static ResourceBundle resourceBundle = StringBundle.getInstance();
    private final static JLabel fillLBL = new JLabel("5");

    // Initializing the buttons
    public static JButton open = new JButton(),
                          newFile = new JButton("+"),
                          saveAsSLC = new JButton(".slc"),
                          saveAsBMP = new JButton(".bmp"),
                          currentColor = new JButton(),
                          pen = new JButton("1"),
                          line = new JButton("2"),
                          rectangle = new JButton("3"),
                          circle = new JButton("4");


    public final static int PEN = 0, LINE = 1, RECTANGLE = 2, CIRCLE = 3;

    public static int mode;
    public final static int[][] presetColors = {{0, 16711680, 16749824, 11206400, 2555648, 65386},
                                                {5987419, 8848388, 9391104, 5476096, 36610, 36691},
                                                {11119530, 65531, 40447, 19711, 13369599, 16711803},
                                                {16777215, 3700346, 936071, 925831, 5574279, 8851027}};

    public static Color color1;

    public static JSlider thicknessControl = new JSlider(1, 50, 1);
    public static JCheckBox fill = new JCheckBox();



    /**
     * Creates a new Inspector dialog.
     * @since 1.0
     */
    public Inspector() throws IOException, FontFormatException{
        // Initializing the dialog base
        setTitle(resourceBundle.getString("INSPECTOR_TITLE"));
        if(System.getProperty("os.name").toLowerCase().contains("mac"))
            setPreferredSize(new Dimension(210, 270));
        else
            setPreferredSize(new Dimension(225, 270));

        setResizable(false);

        // Initializing Layouts
        JPanel subGri = new JPanel(), innerGri = new JPanel(), colorGri = new JPanel(), innerGri2 = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        innerGri.setLayout(new GridLayout(1, 2));
        innerGri2.setLayout(new GridLayout(1, 2));
        subGri.setLayout(new GridLayout(2, 1));
        colorGri.setLayout(null);
        setLayout(new BorderLayout());


        // Initializing button Icons
        Font toolIcons = Font.createFont(Font.TRUETYPE_FONT, new File("src/Artworks/slc-icons.ttf"))
                .deriveFont(Font.PLAIN, 12f);
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(toolIcons);


        // Setting Button props.
        open.setFocusable(false);
        open.setIcon(new FlatFileViewFileIcon());

        newFile.setFocusable(false);

        saveAsSLC.setFocusable(false);
        saveAsSLC.setEnabled(false);

        saveAsBMP.setFocusable(false);
        saveAsBMP.setEnabled(false);

        pen.setFocusable(false);
        pen.setBounds(140, 60, 30, 30);
        pen.setFont(toolIcons);

        rectangle.setFocusable(false);
        rectangle.setBounds(140, 90, 30, 30);
        rectangle.setFont(toolIcons);

        circle.setFocusable(false);
        circle.setBounds(170, 90, 30, 30);
        circle.setFont(toolIcons);

        line.setFocusable(false);
        line.setBounds(170, 60, 30, 30);
        line.setFont(toolIcons);

        thicknessControl.setBounds(140, 120, 60, 30);
        thicknessControl.setFocusable(false);

        fill.setBounds(140, 150, 30, 30);
        fill.setFocusable(false);
        fillLBL.setBounds(170, 150, 30, 30);
        fillLBL.setFont(toolIcons);

        saveAsSLC.setIcon(new FlatFileViewFloppyDriveIcon());
        saveAsBMP.setIcon(new FlatFileViewFloppyDriveIcon());

        add(subGri, BorderLayout.NORTH);

        // Adding components
        gbc.gridx = 1;
        gbc.gridy = 1;

        colorGri.setBounds(10, 0, 180, 120);

        for(int y = 0; y < 6; y ++) {
            for(int x = 0; x < 4 ; x ++) {
                JButton tempButton = new JButton();
                tempButton.setBounds(x * 30, y * 30, 30, 30);
                tempButton.setFocusable(false);
                tempButton.setBackground(new Color(presetColors[x][y]));
                tempButton.addActionListener(e -> setCurrentColor(tempButton.getBackground()));
                colorGri.add(tempButton);
            }
        }



        currentColor.setBounds(140, 0, 60, 60);
        currentColor.setFocusable(false);

        add(colorGri, BorderLayout.CENTER);

        colorGri.add(pen);
        colorGri.add(line);
        colorGri.add(rectangle);
        colorGri.add(circle);
        colorGri.add(currentColor);

        colorGri.add(thicknessControl);
        colorGri.add(fill);
        colorGri.add(fillLBL);


        innerGri2.add(newFile);
        innerGri2.add(open);
        innerGri.add(saveAsSLC);
        innerGri.add(saveAsBMP);
        subGri.add(innerGri2);
        subGri.add(innerGri);

        pack();

        // Assigning functions
        pen.addActionListener(e -> getPenTool());
        line.addActionListener(e -> getLineTool());
        rectangle.addActionListener(e -> getRectTool());
        circle.addActionListener(e -> getCircleTool());

        getPenTool();
        setCurrentColor(Color.BLACK);
    }

    private void setCurrentColor(Color newColor) {
        currentColor.setBackground(newColor);
        color1 = newColor;
    }

    private void getCircleTool() {
        mode = CIRCLE;
        circle.setBackground(Color.LIGHT_GRAY);
        line.setBackground(Color.WHITE);
        rectangle.setBackground(Color.WHITE);
        pen.setBackground(Color.WHITE);
        fill.setVisible(true);
        fillLBL.setVisible(true);
    }

    private void getRectTool() {
        mode = RECTANGLE;
        rectangle.setBackground(Color.LIGHT_GRAY);
        line.setBackground(Color.WHITE);
        circle.setBackground(Color.WHITE);
        pen.setBackground(Color.WHITE);
        fill.setVisible(true);
        fillLBL.setVisible(true);
    }

    private void getLineTool() {
        mode = LINE;
        line.setBackground(Color.LIGHT_GRAY);
        rectangle.setBackground(Color.WHITE);
        circle.setBackground(Color.WHITE);
        pen.setBackground(Color.WHITE);
        fill.setVisible(false);
        fillLBL.setVisible(false);
    }

    private void getPenTool() {
        mode = PEN;
        pen.setBackground(Color.LIGHT_GRAY);
        line.setBackground(Color.WHITE);
        circle.setBackground(Color.WHITE);
        rectangle.setBackground(Color.WHITE);
        fill.setVisible(false);
        fillLBL.setVisible(false);
    }


    /**
     * Simply call this function so that the inspector can view the filename, width and height.
     * @param name Name of the file you've just opened
     * @param columns Width of the Column count of the image. (You can pass colCount from the SLICCodec class)
     * @param rows Height of the Row count of the image. (You can pass rowCount from the SLICCodec class)
     * @since 1.0
     */
    public void setInformation(String name, int columns, int rows) {
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
        saveAsSLC.setEnabled(false);
        saveAsBMP.setEnabled(false);
        open.setEnabled(false);
    }

    /**
     * Simply call this function to avoid malfunctioning by not allowing user to open save unsupported file.
     * @since 1.0
     */
    public void error() {
        saveAsSLC.setEnabled(false);
        saveAsBMP.setEnabled(false);
        open.setEnabled(true);
        Main.canvasWindow.setTitle(StringBundle.getInstance().getString("INSPECTOR_ERR"));
    }
}
