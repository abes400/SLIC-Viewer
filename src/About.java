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

import java.awt.Font;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.util.Objects;

/**
 * The singleton class that contains a dialog with the information about the SLC Viewer application itself.
 * It contains the application version, author and the license.
 *
 * @author İ. K. Bilir (Abes400)
 * @since 1.0
 */
public class About extends JDialog{
    private static About aboutInstance = null;


    /**
     * Creates a new SLIC Viewer dialog that contains the information about the SLIC Viewer application.
     * @since 1.0
     */
    private About() {

        // Initiating a SLIC Viewer dialog object with the title "SLIC Viewer"
        setTitle("About SLIC Viewer");
        setLayout(null);
        setSize(510, 320);
        setResizable(false);

        WindowActions.centerWindow(this);

        try{

            // Initiating a new Label with the image file containing the application's logo.
            JLabel logo = new JLabel( new ImageIcon(
                    ImageIO.read(Objects.requireNonNull(
                            getClass().getResource("/Artworks/logo.png") ))));

            // Setting the position and dimension of the logo label.
            logo.setBounds(10, 0,270, 70);
            add(logo);

        } catch (Exception e) { e.printStackTrace(); }


        // Initiating the JLabel containing the information about the application.
        JLabel text = new JLabel("Version 1.0");

        // Setting the coordinates and size of each index
        text.setBounds(410, 5, 500, 60);
        add(text);


        StringBuilder GPL = new StringBuilder(); //the string to contain the GPL licence text.


        // Reading the contents of the GPL_Notice.txt and appending it to the GPL.
        try{
            // Creating an InputStream instance "inputStream" for streaming out of "GPL_Notice_and_Credits.txt"
            // then creating an InputStreamReader named "isr" with the "inputStream"
            // then creating a BufferedReader "br" with the "isr"
            InputStream inputStream = getClass().getResourceAsStream("GPL_Notice_and_Credits.txt");
            assert inputStream != null;
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String nextLine;
            // Appending the next line read by "br" to the GPL string
            while((nextLine = br.readLine()) != null) GPL.append(nextLine).append('\n');

        } catch(Exception e) { e.printStackTrace(); }


        // Initiating the text area that contains the GPL string.
        JTextArea copyText = new JTextArea(GPL.toString());

        copyText.setEditable(false);
        copyText.setLineWrap(true); //Wraps the text to our box
        copyText.setWrapStyleWord(true); // To ensure that words are not split
        copyText.setFocusable(false);

        copyText.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));

        JScrollPane scrollPane = new JScrollPane(copyText);
        scrollPane.setFocusable(false);
        scrollPane.setBounds(10, 70, 480, 200);
        add(scrollPane);
    }

    /**
     * Get the instance of this singleton. Only one instance exists.
     * @return Returns the instance of this singleton. Only one instance exists.
     */
    public static synchronized About getInstance() {
        if(aboutInstance == null) aboutInstance = new About();

        return aboutInstance;
    }
}
