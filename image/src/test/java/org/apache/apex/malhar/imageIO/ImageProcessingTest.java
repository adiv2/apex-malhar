/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.apex.malhar.imageIO;
/*
 * Tests for basic image processing operators.
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;


public class ImageProcessingTest extends ImageResizeOperator
{
  private Data data = new Data();
  File imageFile;
  private static final Logger LOG = LoggerFactory.getLogger(ImageProcessingTest.class);

  @Before
  public void setup()
  {
    String filePath;
    String[] compatibleFileTypes = {"jpg", "png", "jpeg", "fits", "gif", "tif"};
    BufferedImage bufferedImage = null;
    imageFile = new File("src/test/resources/TestImages/TestImage.jpg");
    filePath = imageFile.getAbsolutePath();
    try {
      bufferedImageType = ImageIO.read(imageFile).getType();
      bufferedImage = ImageIO.read(imageFile);
    } catch (IOException e) {
      LOG.info("Error is " + e.getMessage());
    }
    for (int i = 0; i < compatibleFileTypes.length; i++) {
      if (filePath.contains(compatibleFileTypes[i])) {
        data.imageType = compatibleFileTypes[i];
        if (data.imageType.equalsIgnoreCase("jpeg")) {
          data.imageType = "jpg";
        }
      }
    }
    data.fileName = imageFile.getName();
    data.bytesImage = bufferedImageToByteArray(bufferedImage, data.imageType);
  }

  @Test
  public void resizeTest()
  {
    ImageResizeOperator imageResizer = new ImageResizeOperator();
    imageResizer.scale = 0.5;
    BufferedImage original = byteArrayToBufferedImage(data.bytesImage);
    imageResizer.resize(data);
    BufferedImage result = byteArrayToBufferedImage(data.bytesImage);
    Assert.assertEquals((original.getWidth() * imageResizer.scale), (double)result.getWidth(), 0.0);
  }

  @Test
  public void compressTest()
  {
    File compressedFile = new File("src/test/resources/TestImages/CompressedTestImage.jpg");
    ImageCompressionOperator compress = new ImageCompressionOperator();
    compress.compressionRatio = 0.9f;
    compress.compress(data);
    try {
      FileUtils.writeByteArrayToFile(compressedFile, data.bytesImage);
    } catch (IOException e) {
      LOG.debug(e.getMessage());
    }
    Assert.assertEquals(true, (imageFile.length() > compressedFile.length()));
    Assert.assertEquals(true, (compress.compressionRatio < 1f));

  }

  @Test
  public void fileFormatConversionTest()
  {
    ImageFormatConversionOperator fileFormatConverter = new ImageFormatConversionOperator();
    fileFormatConverter.toFileType = "png";
    fileFormatConverter.converter(data);
    Assert.assertEquals(true, (data.imageType.equalsIgnoreCase(fileFormatConverter.toFileType)));

  }
}
