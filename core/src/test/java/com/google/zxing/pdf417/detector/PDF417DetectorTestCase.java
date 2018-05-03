/*
 * Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.pdf417.detector;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.BufferedImageLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;

import com.google.zxing.pdf417.detector.Detector;
import com.google.zxing.pdf417.detector.PDF417DetectorResult;
import com.google.zxing.NotFoundException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PDF417DetectorTestCase extends Assert {
	@Rule
	public ExpectedException exception = ExpectedException.none(); 
	
	private BinaryBitmap bitmap;
	private RGBLuminanceSource source;
	private GlobalHistogramBinarizer binarizer;
	private static final int[] PDF417_TEST = {
		      48, 901, 56, 141, 627, 856, 330, 69, 244, 900, 852, 169, 843, 895, 852, 895, 913, 154, 845, 778, 387, 89, 869,
		      901, 219, 474, 543, 650, 169, 201, 9, 160, 35, 70, 900, 900, 900, 900, 900, 900, 900, 900, 900, 900, 900, 900,
		      900, 900};
	
	@Test
	public void testPath_1() throws NotFoundException {
		source = new RGBLuminanceSource(4, 4, PDF417_TEST);
		binarizer = new GlobalHistogramBinarizer(source);
		bitmap = new BinaryBitmap(binarizer);
		
		PDF417DetectorResult result = Detector.detect(bitmap, null, false);
		assertEquals(result.getPoints().size(), 0);
	}
	
	@Test
	public void testPath_2() throws NotFoundException {
		source = new RGBLuminanceSource(1, 1, new int[] {1});
		binarizer = new GlobalHistogramBinarizer(source);
		bitmap = new BinaryBitmap(binarizer);
			
		exception.expect(NotFoundException.class);
		Detector.detect(bitmap, null, false);
	}
	
	@Test
	public void testPath_3() throws NotFoundException, IOException {
		PDF417DetectorResult res = Detector.detect(loadTestImage("src/test/resources/blackbox/pdf417-4/02-02.png"),
												   null, true);
		assertEquals(res.getPoints().size(), 4);
	}
	
	@Test
	public void testPath_4() throws NotFoundException, IOException {
		PDF417DetectorResult res = Detector.detect(loadTestImage("src/test/resources/blackbox/pdf417-4/02-02.png"),
												   null, false);
		assertEquals(res.getPoints().size(), 1);
	}
	
	@Test
	public void testPath_5() throws NotFoundException, IOException {
		PDF417DetectorResult res = Detector.detect(loadTestImage("src/test/resources/blackbox/pdf417-4/01-01.png"),
												   null, true);
		assertEquals(res.getPoints().size(), 1);
	}
	
	private BinaryBitmap loadTestImage(String imagePath) throws IOException {
		BufferedImage image = ImageIO.read(Paths.get(imagePath).toFile());
		return new BinaryBitmap(new GlobalHistogramBinarizer(new BufferedImageLuminanceSource(image)));
	}
}
