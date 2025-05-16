/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import java.lang.invoke.MethodHandles;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.lang3.StringUtils;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmImageUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void checkIsImage(byte[] data) {
        checkArgument(getContentType(data).matches("image/.*"), "invalid content type");
        try {
            checkNotNull(ImageIO.read(new ByteArrayInputStream(data)));
        } catch (Exception ex) {
            throw new IllegalArgumentException("invalid image data", ex);
        }
    }

    public static byte[] toByteArray(RenderedImage image, String format) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, format, out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static boolean imageEquals(RenderedImage one, RenderedImage two) { //TODO improve this (?)
        if (one == two) {
            return true;
        } else if (one.getWidth() != two.getWidth() || one.getHeight() != two.getHeight()) {
            return false;
        } else {
            BufferedImage image1 = new BufferedImage(one.getWidth(), one.getHeight(), BufferedImage.TYPE_INT_ARGB), image2 = new BufferedImage(one.getWidth(), one.getHeight(), BufferedImage.TYPE_INT_ARGB);
            image1.getGraphics().drawImage((Image) one, 0, 0, null);
            image2.getGraphics().drawImage((Image) two, 0, 0, null);
            DataBuffer data1 = image1.getData().getDataBuffer(), data2 = image2.getData().getDataBuffer();
            checkArgument(data1.getSize() == data2.getSize() && data1.getNumBanks() == 1 && data2.getNumBanks() == 1);
            for (int i = 0; i < data1.getSize(); i++) {
                if (data1.getElem(i) != data2.getElem(i)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean imageSimilar(RenderedImage one, RenderedImage two, double maxDifferencePercentage) {
        BufferedImage image1 = new BufferedImage(one.getWidth(), one.getHeight(), BufferedImage.TYPE_INT_ARGB),
                image2 = new BufferedImage(two.getWidth(), two.getHeight(), BufferedImage.TYPE_INT_ARGB);
        image1.getGraphics().drawImage((Image) one, 0, 0, null);
        image2.getGraphics().drawImage((Image) two, 0, 0, null);
        int differentPixels = 0;
        double mse;
        int[] pixelImg1 = ((DataBufferInt) image1.getData().getDataBuffer()).getData();
        int[] pixelImg2 = ((DataBufferInt) image2.getData().getDataBuffer()).getData();
        int size = one.getWidth() * one.getHeight();
        for (int i = 0; i < size; ++i) {
            if (pixelImg2[i] != pixelImg1[i]) {
                differentPixels++;
            }
        }
        double percentageDifference = 100.0 * differentPixels / size;
        LOGGER.debug("The two images differ for < {} > percentage", percentageDifference);
        return percentageDifference < maxDifferencePercentage;
    }

    @Nullable
    public static String getImageFormat(byte[] imageBytes) {
        try {
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(new ByteArrayInputStream(imageBytes));
            return list(ImageIO.getImageReaders(imageInputStream)).stream().map(rethrowFunction(r -> r.getFormatName())).filter(StringUtils::isNotBlank).findFirst().orElse(null);
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public static InputStream resizeImage(InputStream imageBytes, int maxWidth, int maxHeight) {
        return new ByteArrayInputStream(resizeImage(CmIoUtils.toByteArray(imageBytes), maxWidth, maxHeight));
    }

    public static InputStream resizeImage(InputStream imageBytes, int maxSizeBytes) {
        return new ByteArrayInputStream(resizeImage(CmIoUtils.toByteArray(imageBytes), maxSizeBytes));
    }

    public static byte[] resizeImage(byte[] imageBytes, int maxWidth, int maxHeight) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (image.getWidth() > maxWidth || image.getHeight() > maxHeight) {
                double scalingFactor = min(maxWidth / (double) image.getWidth(), maxHeight / (double) image.getHeight());
                int targetWidth = (int) (image.getWidth() * scalingFactor), targetHeight = (int) (image.getHeight() * scalingFactor);
                String targetFormat = firstNotBlank(getImageFormat(imageBytes), "png");
                LOGGER.debug("resize image target width = {} height = {}", targetWidth, targetHeight);
                imageBytes = doResizeImage(image, targetWidth, targetHeight, targetFormat);
            }
            return imageBytes;
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public static byte[] resizeImage(byte[] imageBytes, int maxSizeBytes) {
        try {
            if (imageBytes.length > maxSizeBytes) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                String targetFormat = firstNotBlank(getImageFormat(imageBytes), "png");
                byte[] resizedBytes = doResizeImage(image, image.getWidth(), image.getHeight(), targetFormat);
                double scalingFactor = sqrt(maxSizeBytes) / sqrt(resizedBytes.length) * 0.95;
                while (resizedBytes.length > maxSizeBytes) {
                    LOGGER.debug("resize image target max size = {} scaling factor = {}", maxSizeBytes, scalingFactor);
                    resizedBytes = doResizeImage(image, (int) (image.getWidth() * scalingFactor), (int) (image.getHeight() * scalingFactor), targetFormat);
                    scalingFactor *= 0.8;
                }
                imageBytes = resizedBytes;
            }
            return imageBytes;
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    private static byte[] doResizeImage(BufferedImage image, int targetWidth, int targetHeight, String targetFormat) throws Exception {
        LOGGER.trace("do resize image target width = {} height = {}", targetWidth, targetHeight);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType());

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setComposite(AlphaComposite.Src);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(image, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, targetFormat, out);
        byte[] data = out.toByteArray();

        LOGGER.debug("resized image width = {} height = {} size = {}", targetWidth, targetHeight, data.length);

        return data;
    }

}
