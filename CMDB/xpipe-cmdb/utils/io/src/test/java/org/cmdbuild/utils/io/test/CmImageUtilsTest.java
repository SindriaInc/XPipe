/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.Base64;
import static org.cmdbuild.utils.io.CmImageUtils.imageEquals;
import static org.cmdbuild.utils.io.CmImageUtils.imageSimilar;
import static org.cmdbuild.utils.io.CmImageUtils.resizeImage;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmImageUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testImageEquals() throws IOException {
        assertTrue(imageEquals(ImageIO.read(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/image1.jpg")), ImageIO.read(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/image1.jpg"))));
        assertTrue(imageEquals(ImageIO.read(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/image1.png")), ImageIO.read(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/image1.png"))));
        assertTrue(imageEquals(ImageIO.read(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/image1.png")), ImageIO.read(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/image2.png"))));
    }

    @Test
    public void testImageSimilar() throws IOException {
        assertTrue(imageSimilar(ImageIO.read(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/image_result.png")), ImageIO.read(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/image_result_bold.png")), 1));
        assertFalse(imageSimilar(ImageIO.read(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/image_result.png")), ImageIO.read(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/image_result_bold.png")), 0));
        assertTrue(imageSimilar(ImageIO.read(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/image_result.png")), ImageIO.read(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/image_result_bold.png")), 0.15));
    }

    @Test
    public void testImageResize1() throws IOException {
        byte[] imageBytes = toByteArray(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/image1.jpg"));

        assertArrayEquals(imageBytes, resizeImage(imageBytes, 300, 300));

        byte[] resizedImage = resizeImage(imageBytes, 100, 100);

        assertTrue(imageSimilar(ImageIO.read(new ByteArrayInputStream(resizedImage)), ImageIO.read(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/image1_small.jpg")), 0.1));
    }

    @Test
    public void testImageResize2() throws IOException {
        byte[] imageBytes = toByteArray(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/image1.jpg"));

        assertArrayEquals(imageBytes, resizeImage(imageBytes, 30000));

        byte[] resizedImage = resizeImage(imageBytes, 10000);

        assertTrue(resizedImage.length < 10000);
    }
}
