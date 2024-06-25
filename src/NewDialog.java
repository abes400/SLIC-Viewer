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

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class NewDialog extends JDialog {

    public static boolean accepted = false;
    public int width, height;

    private JTextField widthTF, heightTF;

    public NewDialog() {

        setModal(true);
        setSize(240, 170);
        setLayout(null);
        setTitle(StringBundle.getInstance().getString("CREATE_FILE_TITLE"));
        setResizable(false);

        JLabel w = new JLabel(StringBundle.getInstance().getString("CREATE_FILE_W")),
                h = new JLabel(StringBundle.getInstance().getString("CREATE_FILE_H"));
        w.setBounds(50, 10, 150, 50);
        h.setBounds(50, 40, 150, 50);

        widthTF = new JTextField("500");
        heightTF = new JTextField("500");

        widthTF.setBounds(120, 25, 60, 25);
        heightTF.setBounds(120, 55, 60, 25);

        widthTF.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                char kchar = e.getKeyChar();
                // Forcing the text field only to accept numerical input (in other words, integers)
                widthTF.setEditable(kchar >= '0' && kchar <= '9' || kchar == KeyEvent.VK_BACK_SPACE);
            }
        });

        heightTF.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                char kchar = e.getKeyChar();
                // Forcing the text field only to accept numerical input (in other words, integers)
                heightTF.setEditable(kchar >= '0' && kchar <= '9' || kchar == KeyEvent.VK_BACK_SPACE);
            }
        });

        JButton accept = new JButton(StringBundle.getInstance().getString("CREATE_FILE_ACCEPT"));
        JButton cancel = new JButton(StringBundle.getInstance().getString("CREATE_FILE_CANCEL"));

        accept.addActionListener(e -> approve());
        cancel.addActionListener(e -> abort());

        accept.setBounds(120, 90, 80, 25);
        cancel.setBounds(30, 90, 80, 25);

        add(w);
        add(h);
        add(widthTF);
        add(heightTF);
        add(accept);
        add(cancel);

        getRootPane().setDefaultButton(accept);

        WindowActions.centerWindow(this);

    }

    private void approve() {
        setVisible(false);
        width = Integer.valueOf(widthTF.getText());
        height = Integer.valueOf(heightTF.getText());

        if(width < 5) width = 5;
        if(height < 5) height = 5;

        accepted = true;
    }

    private void abort() {
        setVisible(false);
        accepted = false;
    }
}
