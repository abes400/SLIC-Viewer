// Written by İ.K. Bilir (Abes400)

import java.awt.Color;
import java.awt.image.BufferedImage;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * The Compressor and Decompressor (CODEC) class that is capable of taking an image file in format of <strong>SLC</strong>
 * (the extension is <strong>.slc</strong>), decompress it and write it onto a <strong>BufferedImage</strong> object.
 * It's also capable of taking a <strong>BufferedImage</strong> object, compress it and write it onto a <strong>.slc</strong>
 * file.
 *
 * @author İ.K. Bilir (Abes400)
 * @since 1.0
 */
public class SLICCodec {

    /**
     * <strong>rowCount</strong> stores the <strong>height</strong> of the most recent .slc file decompressed.
     *
     * <p>
     *  <strong>colCount</strong> stores the <strong>width</strong> of the most recent .slc file decompressed.
     * </p>
     * <p> </p>
     * <p>
     *  <strong>NOTICE</strong> Before calling <strong>saveToFile()</strong> method, remember to assign the corresponding
     *  dimensions to these values so that it knows the size of the image to be compressed.
     * </p>
     */
    public static short rowCount, colCount;

    // Holds where the last top left corner of the tile to be created or drawn should be.
    private static short cursorRow, cursorCol;

    // A DP boolean matrix to store which parts of the image are painted.
    // When decompressing, It helps us determine where the next "tile" should go.
    // When compressing, it helps us determine where the next "tile" should be generated from.
    // Working together with cursorRow and cursorCount, it helps us see what parts of the image are
    //      already painted.
    private final static boolean[][] alreadyPainted = new boolean[5000][5000];

    /**
     * Decompresses the SLIC image file in the given path and write it to a BufferedImage
     *
     * <p> </p>
     * <p>
     *  Simply call this method to read an image file in format of <strong>SLIC</strong>
     *  (the extension is <strong>.slc</strong>), decompress it and write it onto a <strong>BufferedImage</strong> object.
     * </p>
     * @param compressedFile The source path to the image file of SLIC format that we want to compress.
     * @param image The target BufferedImage on which we uncompressed image will be painted.
     */
    public static void readFile(Path compressedFile, BufferedImage image) throws IOException {

        byte[] rawBytes = Files.readAllBytes(compressedFile); // Save all the raw bytes in a byte array

        // Need them for our boolean DP matrix, alreadyPainted
        rowCount = (short)(((rawBytes[0] & 0xFF) << 8) | (rawBytes[1] & 0xFF));
        colCount = (short)(((rawBytes[2] & 0xFF) << 8) | (rawBytes[3] & 0xFF));

        // Clearing the alreadyPainted matrix in case of opening another file.
        for(int r = 0 ; r < rowCount; r++)
            for (int c = 0; c < colCount; c++)
                alreadyPainted[r][c] = false;

        // Resetting the position of cursors
        cursorRow = cursorCol = 0;

        // Hold the tile color
        String tileColor;
        short tileMode, tileSize;

        //Thread.sleep(100); //

        /*  FOR EACH TILE ITERATED:
         *
         *  1 - Read the tile information and extract the color, mode and size.
         *  2 - Find the first unpainted spot
         *  3 - Paint the tile with the information gathered from the top left corner as the first unpainted spot
         *  4-  Assign the tile to the AlreadyPainted
         */

        // Iterating through the tiles contained in the file, and paint each tile tothe BufferedImage.
        for(int cursor = 4 ; cursor <= rawBytes.length - 4; cursor += 4){

            // Get the first 3 bytes to store the 24 bit color
            tileColor = String.format("0x%02X%02X%02X", rawBytes[cursor], rawBytes[cursor+1], rawBytes[cursor+2]);
            tileMode = (short)((rawBytes[cursor+3] & 0xFF) >> 6); // Get the first two bits from the 4th byte of the tile
            tileSize = (short) (rawBytes[cursor+3]&0x3F);

            // This is how you set the cursors to the first unpainted spot.
            getFirstUnpaintedSpot();
            paintTileToImage(tileMode, tileSize, tileColor, image);
        }

    }

    /**
     * Compresses the BufferedImage into a SLIC image file.
     * @param image The BufferedImage we want to compress
     * @param compressedFile The target path to which the compressed image is saved
     */

    public static void saveToFile(BufferedImage image, Path compressedFile) throws IOException{
        cursorRow = cursorCol = 0;

        // Initiate the information needed for the Tile information to generate.
        short length = 0; short mode;
        int color = 0;

        // Initiating the METADATA of the file, which stores the dimensions of the image
        byte[] sizes = new byte[4], colorsAndMetadata = new byte[4];
        sizes[0] = (byte)((rowCount >> 8) & 0xFF);
        sizes[1] = (byte)(rowCount & 0XFF);
        sizes[2] = (byte)((colCount>>8) & 0xFF);
        sizes[3] = (byte)(colCount & 0xFF);

        // Writing the METADATA
        Files.write(compressedFile, sizes, StandardOpenOption.CREATE);


        // Clearing the alreadyPainted matrix in case of opening another file.
        for(int r = 0 ; r < rowCount; r++)
            for (int c = 0; c < colCount; c++)
                alreadyPainted[r][c] = false;


        // The iteration loop for extracting the tiles out of the BufferedImage
        /* AT EACH ITERATION:
         * 1 - Go to the first unpainted spot
         * 2 - Try to create a homogeneous square as big as possible (mode 3) until there is an odd pixel
         * 3 - If length is more than 1, jump to step 9
         * 4 - Try to create a homogeneous horizontal line as long as possible (mode 1) until there is an odd pixel
         * 5 - If length is more than 1, jump to step 10
         * 6 - Try to create a homogeneous vertical line as long as possible (mode 2) until there is an odd pixel
         * 7 - If length is more than 1, jump to step 11
         * 8 - Assign the mode as dot (mode 0) and jump to step 12
         * 9 - Assign the mode as square (mode 3) and jump to step 12
         * 10- Assign the mode as horizontal line (mode 1) and jump to step 12
         * 11- Assign the mode as vertical line (mode 2)
         * 12- Assign the color and length
         * 13- Assign the tile to the AlreadyPainted
         * 14- Write the collected tile information bytes to the file
         */
        do{
            // step 1
            getFirstUnpaintedSpot();

            // step 2 to 11
            for(mode = (short)3; mode >= 0; mode--){
                for(length = 1; length < 63; length++){

                    color = image.getRGB(cursorCol, cursorRow);

                    if(containsAnOdd(color, mode, length, image)) {
                        length--;
                        break;
                    }

                }

                if(length > 1) break;
            }

            // step 12
            length = mode == 0 ? 0 : length;
            mode = mode == 2 ? 1 : mode==1 ? 2 : mode;

            colorsAndMetadata[0] = (byte)((color >> 16) & 0xFF);
            colorsAndMetadata[1] = (byte)((color >> 8) & 0xFF);
            colorsAndMetadata[2] = (byte)(color & 0xFF);
            colorsAndMetadata[3] = (byte)(((mode << 6) & 0xFF) | length & 0xFF);

            // step 13
            paintTileToImage(mode, length, "",image);

            // step 14
            Files.write(compressedFile, colorsAndMetadata, StandardOpenOption.APPEND);

        } while (!allAreasChecked());


        System.out.println("Compression successful.");
    }



    // Assign the tile to the AlreadyPainted, leave tileColor as empty string for only assigning
    private static void paintTileToImage(short tileMode, short tileSize, String tileColor, BufferedImage image){
        short width = 0, height = 0;

        // Width and height information is decided by the tileMode
        switch (tileMode){
            case 0: width = 1;
                height = 1;
                break;
            case 1: width = tileSize;
                height = 1;
                break;
            case 2: width = 1;
                height = tileSize;
                break;
            case 3: width = height = tileSize;
                break;
        }

        // Actually painting the tile
        for(short r = cursorRow; r < cursorRow + height; r++)
            for(short c = cursorCol; c < cursorCol + width; c++){
                alreadyPainted[r][c] = true;
                if(!tileColor.isEmpty()) // Painting the tile
                    image.setRGB(c, r, Color.decode(tileColor).getRGB());
            }
    }


    // Assigns the coordinates of the first spot on the AlreadyPainted dp matrix.
    private static void getFirstUnpaintedSpot(){
        for(short r = cursorRow; r < rowCount; r++)
            for(short c = 0; c < colCount; c++)
                if (!alreadyPainted[r][c]){
                    cursorRow = r;
                    cursorCol = c;

                    System.out.println(r*colCount+c + " of " + rowCount*colCount);
                    return;
                }
    }

    // Checks whether the tested tile is homogeneous or not.
    private static boolean containsAnOdd (int color, short tileMode, short tileSize, BufferedImage image) {

        // Width and height information is decided by the tileMode
        int width = 0, height = 0;
        switch (tileMode){
            case 0: width = 1;
                height = 1;
                break;
            case 2: width = tileSize;
                height = 1;
                break;
            case 1: width = 1;
                height = tileSize;
                break;
            case 3: width = height = tileSize;
                break;
        }

        // Checks for different color
        for(short r = cursorRow; r < cursorRow + height && r <= rowCount; r++)
            for(short c = cursorCol; c < cursorCol+ width && c <= colCount; c++){

                if(r == rowCount || c == colCount ){ return true; }
                if (image.getRGB(c, r) != color){ return true; }

            }

        return false;
    }

    // Ensures if all areas are checked.
    private static boolean allAreasChecked () {
        for(int r = cursorRow; r < rowCount; r++) { // IF THE ALGORITHM MALFUNCTIONS CHANGE THE CURSORROW TO 0

            for(int c = 0; c < colCount; c++){ if(!alreadyPainted[r][c]) return false; }

        }
        return true;
    }
}

       /*
            Java interprets bytes with 2's complement notation.
            So whenever the MSB is 1, it will be treated as a NEGATIVE NUMBER.

            10110100 => -76

            We don't want this, we want to get the 180 value.

            Even if we upcast the value into an int, it will extend the MSB to 1 to
            keep the NEGATIVE value.

            10110100 => 11111111 10110100 => still -76

            To get the unsigned value, we can bit-wisely AND the value by 0xFF which is 11111111,
            and assign it to an int.

            In bitwise operations, somehow byte literals are not extended by their MSB unless you upcast them into (byte).

            mybyte & 0xFF
               ^       ^
            10110100 11111111

            (11111111 11111111 11111111) 10110100 <- bytes are upcasted by EXTENDING MSB
            (00000000 00000000 00000000) 11111111 <- byte literals are upcasted WITHOUT EXTENDING MSB

            As a result : 00000000 00000000 00000000 10110100 which is 180


            mybyte & (byte)0xFF
            10110100 11111111

            10110100
            11111111

            10110100 <- result (didn't work)
         */

        /*
            BRIEF DESCRIPTION:

            TILE: Abstraction for adjacent pixel groups containing the same color.

                  In SLIC format, the image to compressed is broken into tiles.

                  Each tile comes in one of four modes that determines their shape:
                    0 - Dot -> just one pixel
                    1 - Horizontal Line
                    2 - Vertical Line
                    3 - Square -> You can store one length for squares.

                   From the possible shapes of the tiles, one can imply that each tile is either square
                   (pixel is also a square) or a line. In fact, this is where the name of the format
                   comes from: SQUARES AND LINES IMAGE COMPRESSION, or SLIC for short.

                   Now, each tile is stored in four bytes (32 bits) in files.

                   Here is one tile is stored: (R for RED, G for GREEN, B for BLUE, M for MODE, L for LENGTH)
                   RRRRRRRR GGGGGGGG BBBBBBBB MMLLLLLL

                   Modes: 00 -> 0 as pixel, 01 -> 1 as Horiz. Line, 10 -> 2 as Vert. line, 11 -> 3 as Square.

                   With this storage model, you can store up to 63 pixels long lines or 63x63 pixel large tiles.

                   Imagine a 63x63 square pixel group in yellow color. In BMP, each pixel is stored in 24 bits.
                   63x63x24 = takes up 95256 bits of space.

                   But in our Format, it is stored as
                   11111111 11111111 00000000 11-111111 -> Yellow tile as a square with side of length 63.

                   In our format, it's stored in 32 bits, this is how the compression occurs.

                   
              FILE STRUCTURE: The letters show what that bit is used for.

                W : Image Width     H : Image Height
                R : Red             G : Green           B : Blue
                M : Mode            S : Size            0 : Unused bit

                0WWWWWWW WWWWWWWW 0HHHHHHH HHHHHHHH     -> Metadata (Width, Height)
                RRRRRRRR GGGGGGGG BBBBBBBB MMSSSSSS     -> Tile information
                RRRRRRRR GGGGGGGG BBBBBBBB MMSSSSSS     -> Tile information
                RRRRRRRR GGGGGGGG BBBBBBBB MMSSSSSS     -> Tile information
                                ...
                RRRRRRRR GGGGGGGG BBBBBBBB MMSSSSSS     -> Tile information
                RRRRRRRR GGGGGGGG BBBBBBBB MMSSSSSS     -> Tile information
         */

