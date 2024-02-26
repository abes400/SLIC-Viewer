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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;

/**
 * The class that reduces complex code snippets for selecting a file or directory using JFileChooser to a few
 * lines of code. It is created in order to reduce the complexity that may be caused by the long
 * and repeating code snippet that could be used for selecting a file or a directory using JFileChooser.
 * Simply call <pre>{@code loadFileTo(String targetDir, int mode)}</pre> and the selected file or directory is
 * copied to the desired path. <pre></pre>
 * In order to make sure that the program doesn't try to process a null file or directory
 * as the user clicks Cancel, don't forget to check if the attribute <strong>filename</strong> is null.
 * Use the copied file or directory in your code only if <strong>filename is not null</strong>.
 * <pre>
 *     <strong>Ex:</strong>
 *     {@code if(FileOperations.filename != null){
 *         //Process the file here.
 *     }}
 * </pre>
 * @author İ. K. Bilir (Abes400)
 * @since 1.0
 */
public class FileOperations {
    public static String filename; // Holds the filename selected by user
    static JFileChooser fc = new JFileChooser();
    static Path path;

    /**
     * Initiates a JFileChooser object window, copies the selected file or directory to the path passed as parameter and
     * stores its name in the attribute <strong>String filename</strong>
     * <pre></pre>
     * In order to make sure that the program doesn't try to process a null file or directory after the user clicks
     * Cancel, don't forget to check if the attribute <strong>filename</strong> is null. Use the copied file or directory
     * in your code only if <strong>filename is not null</strong>.
     * <pre>
     *     <strong>Ex:</strong>
     *     {@code if(FileOperations.filename != null){
     *         //Process the file here.
     *     }}
     * </pre>
     * @param mode Decides whether only directories or only files can be selected.
     * @throws java.io.IOException -
     * @since 1.0
     */
    public static void loadFileTo(int mode) throws java.io.IOException {
        filename = null; // first set filename to null. Since this attribute is static, the value will not be deleted
        // after the method returns. This can cause some issues since if this method is called again
        // and the cancel button is clicked.

        fc.setDialogTitle(mode == JFileChooser.FILES_ONLY ? "Choose a file" : "Where to save ");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY); // Only files are allowed since the mode is FILES_ONLY

        if(mode == JFileChooser.DIRECTORIES_ONLY) { fc.setSelectedFile(new File("Untitled")); }

        // Restricting selected files to .waw
        int result = mode == JFileChooser.FILES_ONLY ? fc.showOpenDialog(null) :
                mode ==  JFileChooser.DIRECTORIES_ONLY ? fc.showSaveDialog(null) : 0;

        if (result == JFileChooser.APPROVE_OPTION) {

            filename = fc.getSelectedFile().getName(); // Now filename is set to the name of the selected item
            String temp = fc.getSelectedFile().getPath();
            path = Paths.get(temp);

        } else if (result == JFileChooser.CANCEL_OPTION) path = null;
    }
}
