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

    public final static JScrollPane scrollPane = new JScrollPane();
    private final static int screenColCount = Toolkit.getDefaultToolkit().getScreenSize().width,
                             screenRowCount = Toolkit.getDefaultToolkit().getScreenSize().height;
    public static int tempWidth, tempHeight;

    public static Graphics2D g;

    /**
     * Creates a new CanvasWindow window.
     * @since 1.0
     */
    public CanvasWindow() {
        // Initiating Canvas Window Base
        setMinimumSize(new Dimension(300, 300));
        setSize(600, 500);
        setTitle(StringBundle.getInstance().getString("CNV_INFO"));
        setLayout(new BorderLayout());

        // Adding the scroll pane for scrollability
        scrollPane.setBorder(createEmptyBorder());
        imageLabel.setBounds(0, 0, 800, 800);
        scrollPane.getViewport().add(imageLabel);

        add(scrollPane, BorderLayout.CENTER);

        WindowActions.centerWindow(this);
        setVisible(true);

        g = (Graphics2D) getGraphics();
    }

    /**
     * Simply call this function and any Buffered Image shows up on the Canvas Window.
     * @param image The BufferedImage object you want the Canvas Window to view.
     * @since 1.0
     */
    public void showImage(BufferedImage image) {
        // Removing and re-adding are crucial so that user won't
        // have to resize the window for image to show up.

        for(int i = 0; i < SLICCodec.colCount; i++){
            image.setRGB(i, SLICCodec.rowCount, Color.lightGray.getRGB());
            image.setRGB(i, SLICCodec.rowCount + 1, Color.lightGray.getRGB());
        }
        for(int i = 0; i < SLICCodec.rowCount; i++){
            image.setRGB(SLICCodec.colCount, i, Color.lightGray.getRGB());
            image.setRGB(SLICCodec.colCount + 1, i, Color.lightGray.getRGB());
        }

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
            setTitle(FileOperations.filename + " " + SLICCodec.colCount + "x" + SLICCodec.rowCount + " ( px.)");

    }

    void paintComponent(int x1, int y1, int x2, int y2){
        super.paintComponents(g);

        tempWidth = Math.abs(x2-x1);
        tempHeight = Math.abs(y2-y1);

        switch(Inspector.mode){
            case Inspector.LINE:
                g.drawLine(x1, y1, x2, y2);
                break;
            case Inspector.RECTANGLE:
                g.drawRect(x2 < x1 ? x2 : x1, y2 < y1 ? y2 : y1, tempWidth, tempHeight);
                break;
            case Inspector.CIRCLE:
                g.drawOval(x2 < x1 ? x2 : x1, y2 < y1 ? y2 : y1, tempWidth, tempHeight);

        }


    }
}
