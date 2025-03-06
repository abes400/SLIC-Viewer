// Written by İ.K. Bilir (Abes400)

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;


import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The singleton class that contains a dialog with the information about the SLC Viewer application itself.
 * It contains the application version, author and the license.
 *
 * @author İ. K. Bilir (Abes400)
 * @since 1.0
 */
public class About extends JDialog{
    private static About aboutInstance = null;
    private ResourceBundle resourceBundle = StringBundle.getInstance();


    /**
     * Creates a new SLIC Viewer dialog that contains the information about the SLIC Viewer application.
     * @since 1.0
     */
    private About() {

        // Initiating a SLIC Viewer dialog object with the title "SLIC Viewer"
        setTitle(resourceBundle.getString("ABOUT_TITLE"));
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
        JLabel text = new JLabel(resourceBundle.getString("ABOUT_VER"));

        // Setting the coordinates and size of each index
        text.setBounds(410, 5, 500, 60);
        add(text);
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
