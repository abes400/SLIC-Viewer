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

import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.extras.FlatDesktop;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class Main {

    // image : The object that is sent to the canvasWindow,
    //          it ALWAYS stores the final output image from bmp or slc files.
    // tempImage : When a bmp is open, it's written in here, then every single pixel is assigned to "image"
    //              That's how I managed to got it to work so don't question.
    static BufferedImage image = new BufferedImage(5000, 5000, BufferedImage.TYPE_INT_ARGB),
                         tempImage;
    static CanvasWindow canvasWindow;
    static final int SLC_IMAGE = 0, BITMAP = 1;
    static Inspector inspector;
    static JMenuItem saveBMPFileItem, saveSLCFileItem;

    // The entrance function.
    public static void main(String[] args) throws Exception {

        // Using Mac's own toolbar instead of in-window toolbar.
        if(System.getProperty("os.name").toLowerCase().contains("mac"))
            System.setProperty("apple.laf.useScreenMenuBar", "true");

        // Making the app look sleek
        UIManager.setLookAndFeel("com.formdev.flatlaf.themes.FlatMacLightLaf");

        // Changing the default java "about" dialog with the one we created.
        FlatDesktop.setAboutHandler( () -> About.getInstance().setVisible(true));

        // Initiating the menu bar
        JMenuBar applicationMenuBar = new JMenuBar();

        // Initiating the menu items
        JMenu fileMenu = new JMenu("File"),
              windowMenu = new JMenu("Window");
        applicationMenuBar.add(fileMenu); applicationMenuBar.add(windowMenu);

        // Initiating the menu commands
        JMenuItem openFileItem = new JMenuItem("Open..."),
                showInspector = new JMenuItem("Show Inspector"),
                centerCanvas = new JMenuItem("Center Viewer");
        saveBMPFileItem = new JMenuItem("Save as BITMAP");
        saveSLCFileItem = new JMenuItem("Save as SLIC");
        fileMenu.add(openFileItem);
        fileMenu.add(saveSLCFileItem); fileMenu.add(saveBMPFileItem);

        windowMenu.add(showInspector); windowMenu.add(centerCanvas);

        // Assigning the methods for the menu commands
        openFileItem.addActionListener(e -> openFile());
        saveBMPFileItem.addActionListener(e -> saveFile(BITMAP));
        saveSLCFileItem.addActionListener(e -> saveFile(SLC_IMAGE));
        saveBMPFileItem.setEnabled(false);
        saveSLCFileItem.setEnabled(false);

        // If NOT running on a mac, add About command to the Window section
        if(!System.getProperty("os.name").toLowerCase().contains("mac")) {
            JMenuItem aboutMenuItem = new JMenuItem("About SLIC Viewer");
            windowMenu.add(aboutMenuItem);
            aboutMenuItem.addActionListener(e -> About.getInstance().setVisible(true));
        }

        showInspector.addActionListener(e -> inspector.setVisible(true));
        centerCanvas.addActionListener(e -> WindowActions.centerWindow(canvasWindow));

        // Initiating the Inspector dialog
        inspector = new Inspector();
        Inspector.open.addActionListener(e -> openFile());
        Inspector.saveAsBMP.addActionListener(e -> saveFile(BITMAP));
        Inspector.saveAsSLC.addActionListener(e -> saveFile(SLC_IMAGE));

        // Initiating the Canvas window
        canvasWindow = new CanvasWindow();
        canvasWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        canvasWindow.setJMenuBar(applicationMenuBar);


        // When the window state changes this is how the inspector window is manipulated.
        canvasWindow.addWindowListener(new WindowAdapter() {

            @Override
            public void windowIconified(WindowEvent e) {
                inspector.setVisible(false);
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                inspector.setVisible(true);
            }
            @Override
            public void windowActivated(WindowEvent e) {
                if (System.getProperty("os.name").toLowerCase().contains("mac"))
                    inspector.toFront();
                    canvasWindow.requestFocus();
            }});


        // Using transparent title bar, so it looks integrated with the content. It looks cool.
        if(SystemInfo.isMacFullWindowContentSupported &&
           System.getProperty("os.name").toLowerCase().contains("mac")) {
            inspector.getRootPane().putClientProperty("apple.awt.transparentTitleBar", "true");
            canvasWindow.getRootPane().putClientProperty("apple.awt.transparentTitleBar", "true");
            About.getInstance().getRootPane().putClientProperty("apple.awt.transparentTitleBar", "true");
        }



        // Relocating the Inspector
        inspector.setLocation(50, canvasWindow.getY());
        canvasWindow.setVisible(true);
        inspector.setVisible(true);
    }

    /*
    *       Now here comes the main functionalities of the program. Here you can see how the program uses the
    *       abstraction provided by SLICCodec, and sends the extracted info into the canvasWindow.
    *
    *       Without the abstraction of the SLICCodec, it would be pain in the neck to implement the compression
    *       and decompression algorithms between all these messy code.
    *
    *       This is why it was a credible option to abstract the (de)compression process away by implementing them
    *       on SLICCodec class.
    * */

    // Opening the .bmp and .slc file into image objects and send them to canvas window. As a result the opened
    // image will show up on the canvas window.
    public static void openFile() {
        try {

            FileOperations.loadFileTo(JFileChooser.FILES_ONLY);

            // If user clicks cancel, the function will not try to open a null file.
            if(FileOperations.path != null) {

                inspector.waitOpening(FileOperations.filename); // foolproofing
                canvasWindow.setVisible(false);

                // Clearing the canvas
                image.getGraphics().fillRect(0, 0, 5000, 5000);

                if (FileOperations.filename.endsWith("slc")) { // The opened file is of SLIC format.

                    // Decompressing the .slc file and assign the output into the image object.
                    SLICCodec.readFile(FileOperations.path, image);

                    // Sending the output image to canvas window
                    canvasWindow.showImage(image);

                } else if (FileOperations.filename.endsWith("bmp")) { // The opened file is of BITMAP format.

                    // Reading the file into tempImage, because I didn't want to re-instantiate the image object
                    // over and over again since it's 5000x5000! But temp image is of exact size of the bmp.
                    tempImage = ImageIO.read(new File(FileOperations.path.toString()));

                    // We still need the width and height information for the app to function properly.
                    // Luckily the rowcount and colcount are mutable. But it is unsafe to use it in normal circumstances.
                    // But since I'm the one who wrote it, I know when to assign and when not to reassign the values.
                    SLICCodec.colCount = (short) tempImage.getWidth();
                    SLICCodec.rowCount = (short) tempImage.getHeight();

                    for (int r = 0; r < SLICCodec.rowCount; r++)
                        for (int c = 0; c < SLICCodec.colCount; c++)
                            image.setRGB(c, r, tempImage.getRGB(c, r));

                    // System.out.print(SLICCodec.colCount + " " + SLICCodec.rowCount);
                    canvasWindow.showImage(image);
                }
                // Assigning the width and height.
                inspector.setInformation(FileOperations.filename, SLICCodec.colCount, SLICCodec.rowCount);

                if(FileOperations.filename.endsWith("bmp") || FileOperations.filename.endsWith("slc"))
                    { saveBMPFileItem.setEnabled(true); saveSLCFileItem.setEnabled(true); }
                else {
                    inspector.error();
                    saveBMPFileItem.setEnabled(false); saveSLCFileItem.setEnabled(false);
                }

                canvasWindow.setVisible(true);
            }
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public static void saveFile(int mode) {
        try {
            FileOperations.loadFileTo(JFileChooser.DIRECTORIES_ONLY);

            //System.out.println(FileOperations.path);
            //System.out.println("Path: " + FileOperations.path +"\nName: " + FileOperations.filename);

            // Deciding on the extension
            String ext = mode == BITMAP ? ".bmp" : mode == SLC_IMAGE ? ".slc" : "";

            // If user clicks cancel, the function will not try to save to a null file.
            if(FileOperations.filename != null && FileOperations.path != null) {

                inspector.waitCompressing(); // foolproofing

                canvasWindow.setVisible(false);

                String destination = FileOperations.path + ext; // Target path

                if(Files.exists(Paths.get(destination)))
                    JOptionPane.showMessageDialog(null,
                                         "This file already exists, find something else bi zahmet");

                else if(mode == BITMAP) { // Saving as a bitmap image

                    // Reading the file into tempImage, because I didn't want to re-instantiate the image object
                    // over and over again since it's 5000x5000! But temp image is of exact size of the bmp.
                    tempImage = new BufferedImage(SLICCodec.colCount, SLICCodec.rowCount, BufferedImage.TYPE_INT_RGB);

                    for (int r = 0; r < SLICCodec.rowCount; r++)
                        for (int c = 0; c < SLICCodec.colCount; c++)
                            tempImage.setRGB(c, r, image.getRGB(c, r));

                    // Writing to file
                    ImageIO.write(tempImage, "bmp", new File(destination));

                } else if(mode == SLC_IMAGE) SLICCodec.saveToFile(image, Paths.get(destination));
                                            // Compressing the image .slc and writing to file.

                inspector.setInformation(FileOperations.filename, SLICCodec.colCount, SLICCodec.rowCount);
                canvasWindow.setTitle(FileOperations.filename);

                canvasWindow.setVisible(true);
            }
        } catch (IOException e) { throw new RuntimeException(e); }
    }
}