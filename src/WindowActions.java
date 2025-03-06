// Written by İ.K. Bilir (Abes400)

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

/**
 * @author İ.K. Bilir (Abes400)
 * @since 1.0
 */

public class WindowActions {
    /**
     This function will put ANY WINDOW INSTANCE, which is passed, to the center of the screen.
     @param candidateWindow the window instance you would like to center
     */
    public static void centerWindow(Window candidateWindow){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension winSize = candidateWindow.getSize();
        Point center = new Point((screenSize.width - winSize.width) / 2, (screenSize.height - winSize.height) / 2);
        candidateWindow.setLocation(center);
    }

}
