import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("WeakerAccess")
public class ImageProcessing {


    /**
     * @param img : image to rotate
     * @param angle : angle of rotation
     * @return an image rotated by angle °
     */
    public static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2.0, (newHeight - h) / 2.0);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);

        return rotated;
    }


    /**
     *
     * @param image The image to be scaled
     * @param imageType Target image type, e.g. TYPE_INT_RGB
     * @param newWidth The required width
     * @param newHeight The required width
     *
     * @return The scaled image
     */
    public static BufferedImage scaleImage(BufferedImage image, int imageType,
                                           int newWidth, int newHeight) {
        // Make sure the aspect ratio is maintained, so the image is not distorted
        double thumbRatio = (double) newWidth / (double) newHeight;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double aspectRatio = (double) imageWidth / (double) imageHeight;

        if (thumbRatio < aspectRatio) {
            newHeight = (int) (newWidth / aspectRatio);
        } else {
            newWidth = (int) (newHeight * aspectRatio);
        }

        // Draw the scaled image
        BufferedImage newImage = new BufferedImage(newWidth, newHeight,
                imageType);
        Graphics2D graphics2D = newImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, newWidth, newHeight, null);

        return newImage;
    }


    /**
     * @param f : photo from which we are gonna read metadata
     * @return the angle of the photo when it was taken with the camera
     * @throws IOException if pb with readMetadata
     * @throws MetadataException if pb with getInt
     */
    protected static int getCameraAngleWithMetadata(File f) throws IOException, MetadataException {
        int angle = 0;
        try {
            //Metadata metadata = ImageMetadataReader.readMetadata(file); if other than JPEG but slower
            Metadata metadata = JpegMetadataReader.readMetadata(f);
            if(Main.PRINT_METADATA) Utils.print(metadata, "Using JpegMetadataReader");
            ExifIFD0Directory exifIFD0 = metadata.getDirectory(ExifIFD0Directory.class);
            int orientation = exifIFD0.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            if(orientation==6) angle = 90;
            else if(orientation==8) angle = 270;

        } catch (JpegProcessingException e) {
            Utils.print(e);
        }
        return angle;
    }
}
