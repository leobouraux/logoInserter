import com.drew.metadata.MetadataException;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

@SuppressWarnings("WeakerAccess")
public class Main {
    public static final boolean PRINT_METADATA = false;
    private static final String folderIn =  "/Users/leobouraux/Downloads/ABCStaff/";           //todo
    private static final String logoPath =  "/Users/leobouraux/Desktop/ESN/_esn_epfl_white.png"; //todo

    private static final String folderOut = folderIn + "DONE/";

    public static void main(String[] args) throws MetadataException {
        try {
            File f = new File(folderIn);
            ArrayList<String> file_names = listFilenamesToPreprocess(f);
            logoImageInserter(file_names);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param folder : folder in which we have pictures
     * @return list of names of all the pictures contained in the folder
     * @throws NullPointerException if no files in Folder ?
     */
    private static ArrayList<String> listFilenamesToPreprocess(final File folder) throws NullPointerException{
        ArrayList<String> files_names = new ArrayList<>();

        File[] files = folder.listFiles();
        if(files != null) {
            for (final File fileEntry : files) {
                if (fileEntry.isDirectory()) {
                    listFilenamesToPreprocess(fileEntry);
                } else {
                    files_names.add(fileEntry.getName());
                }
            }
        }
        return files_names;
    }


    /**
     * @param file_names : list of filenames
     * @throws IOException when pb with -getCameraAngleWithMetadata
     *                                  -read
     *                                  -insertLogo
     *                                  -writePictureInMemory
     * @throws MetadataException when pb with getCameraAngleWithMetadata
     */
    private static void logoImageInserter(ArrayList<String> file_names) throws IOException, MetadataException {
        boolean isDONEfoldercreated = new File(folderOut).mkdirs();
        if(!isDONEfoldercreated) System.out.println("DONE folder not created\n-----------------------\n");

        int counter = 0;

        for (String photoName : file_names) {
            String imagePathFrom = folderIn + photoName;
            String imagePathTo = folderOut + photoName;
            File f = new File(imagePathFrom);

            int angle = ImageProcessing.getCameraAngleWithMetadata(f);

            if (f.canRead()) {
                BufferedImage originalImage = ImageIO.read(f);

                if (originalImage != null) {
                    BufferedImage rotatedImage = ImageProcessing.rotateImageByDegrees(originalImage, angle);
                    if (rotatedImage != null) {
                        insertLogo(rotatedImage);
                        writePictureInMemory(imagePathTo, rotatedImage);
                    } else System.out.println("Problem with the rotation of image " + photoName);
                } else System.out.println("Problem with the original image " + photoName);
            }
            counter++;
            System.out.print("\r" +  (int)(100*counter/(double)file_names.size())+"% done");
        }
    }

    private static void writePictureInMemory(String imagePathTo, BufferedImage originalImage) throws IOException {
        File f_target = new File(imagePathTo);
        ImageIO.write(originalImage,
                "jpg",
                f_target);
    }

    private static void insertLogo(BufferedImage originalImage) throws IOException {
        BufferedImage logo = ImageIO.read(new File(logoPath));

        BufferedImage logo_resized = ImageProcessing.scaleImage(logo,
                BufferedImage.TYPE_INT_ARGB,
                originalImage.getWidth() / 10,
                originalImage.getHeight() / 5);

        Graphics g = originalImage.getGraphics();
        g.drawImage(logo_resized,
                (int) (originalImage.getWidth() - logo_resized.getWidth() * 1.1),
                (int) (originalImage.getHeight() - logo_resized.getHeight() * 1.1),
                null);
    }
}


