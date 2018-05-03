/*
 * Copyright 2008 ZXing authors
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

package com.google.zxing.qrcode.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.common.BitSourceBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests {@link DecodedBitStreamParser}.
 *
 * @author Sean Owen
 */
public final class DecodedBitStreamParserTestCase extends Assert {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Test
  public void testSimpleByteMode() throws Exception {
    BitSourceBuilder builder = new BitSourceBuilder();
    builder.write(0x04, 4); // Byte mode
    builder.write(0x03, 8); // 3 bytes
    builder.write(0xF1, 8);
    builder.write(0xF2, 8);
    builder.write(0xF3, 8);
    String result = DecodedBitStreamParser.decode(builder.toByteArray(),
        Version.getVersionForNumber(1), null, null).getText();
    assertEquals("\u00f1\u00f2\u00f3", result);
  }

  @Test
  public void testSimpleSJIS() throws Exception {
    BitSourceBuilder builder = new BitSourceBuilder();
    builder.write(0x04, 4); // Byte mode
    builder.write(0x04, 8); // 4 bytes
    builder.write(0xA1, 8);
    builder.write(0xA2, 8);
    builder.write(0xA3, 8);
    builder.write(0xD0, 8);
    String result = DecodedBitStreamParser.decode(builder.toByteArray(),
        Version.getVersionForNumber(1), null, null).getText();
    assertEquals("\uff61\uff62\uff63\uff90", result);
  }

  @Test
  public void testECI() throws Exception {
    BitSourceBuilder builder = new BitSourceBuilder();
    builder.write(0x07, 4); // ECI mode
    builder.write(0x02, 8); // ECI 2 = CP437 encoding
    builder.write(0x04, 4); // Byte mode
    builder.write(0x03, 8); // 3 bytes
    builder.write(0xA1, 8);
    builder.write(0xA2, 8);
    builder.write(0xA3, 8);
    String result = DecodedBitStreamParser.decode(builder.toByteArray(),
        Version.getVersionForNumber(1), null, null).getText();
    assertEquals("\u00ed\u00f3\u00fa", result);
  }

  @Test
  public void testHanzi() throws Exception {
    BitSourceBuilder builder = new BitSourceBuilder();
    builder.write(0x0D, 4); // Hanzi mode
    builder.write(0x01, 4); // Subset 1 = GB2312 encoding
    builder.write(0x01, 8); // 1 characters
    builder.write(0x03C1, 13);
    String result = DecodedBitStreamParser.decode(builder.toByteArray(),
        Version.getVersionForNumber(1), null, null).getText();
    assertEquals("\u963f", result);
  }

  @Test
  public void testNumericWithPairDigitsAtTheEnd() throws Exception {
    BitSourceBuilder builder = new BitSourceBuilder();
    builder.write(0x01, 4);   // Numeric mode
    builder.write(0x08, 10);  // 8 digits will be read
    builder.write(0x7B, 10);  // 123 in hex
    builder.write(0x1C8, 10); // 456 in hex
    builder.write(0x4E, 7);   // 78 in hex
    String result = DecodedBitStreamParser.decode(builder.toByteArray(),
      Version.getVersionForNumber(1), null, null).getText();
    assertEquals("12345678", result);
  }

  @Test
  public void testNumericWithSingleDigitAtTheEnd() throws Exception {
    BitSourceBuilder builder = new BitSourceBuilder();
    builder.write(0x01, 4);  // Numeric mode
    builder.write(0x04, 10); // 4 digits will be read
    builder.write(0x7B, 10); // 123 in hex
    builder.write(0x04, 4);  // 4 in hex
    String result = DecodedBitStreamParser.decode(builder.toByteArray(),
      Version.getVersionForNumber(1), null, null).getText();
    assertEquals("1234", result);
  }

  @Test
  public void testAlphanumeric() throws Exception {
    BitSourceBuilder builder = new BitSourceBuilder();
    builder.write(0x002, 4);  // Alphanumeric mode
    builder.write(0x005, 9);  // 5 digits will be read
    builder.write(0x1C3, 11); // A1 in hex
    builder.write(0x1F1, 11); // B2 in hex
    builder.write(0x00C, 6);  // C in hex
    String result = DecodedBitStreamParser.decode(builder.toByteArray(),
      Version.getVersionForNumber(1), null, null).getText();
    assertEquals("A1B2C", result);
  }

  @Test
  public void testAlphanumericWithFNC1() throws Exception {
    BitSourceBuilder builder = new BitSourceBuilder();
    builder.write(0x005, 4);  // FNC1 first position mode
    builder.write(0x002, 4);  // Alphanumeric mode
    builder.write(0x006, 9);  // 6 digits
    builder.write(0x6B8, 11); // %A in hex
    builder.write(0x6D4, 11); // %% in hex
    builder.write(0x215, 11); // B% in hex
    String result = DecodedBitStreamParser.decode(builder.toByteArray(),
      Version.getVersionForNumber(1), null, null).getText();

    // The alphanumeric string is %A%%B%
    // Because of FNC1 should be decoded as [0x1d]A%B[0x1d]
    assertEquals("\u001dA%B\u001d", result);
  }

  @Test
  public void testInvalidAlphanumeric() throws Exception {
    BitSourceBuilder builder = new BitSourceBuilder();
    builder.write(0x002, 4); // Alphanumeric mode
    builder.write(0x004, 9); // 4 digits expected
    builder.write(0x6B8, 5); // but only two (%A) were given

    exception.expect(FormatException.class);
    DecodedBitStreamParser.decode(builder.toByteArray(),
      Version.getVersionForNumber(1), null, null).getText();
  }

  @Test
  public void testInvalidSingleAlphanumeric() throws Exception {
    BitSourceBuilder builder = new BitSourceBuilder();
    builder.write(0x02, 4); // Alphanumeric mode
    builder.write(0x01, 9); // 1 digit is expected
    builder.write(0x01, 3); // but it has less bits than it should

    exception.expect(FormatException.class);
    DecodedBitStreamParser.decode(builder.toByteArray(),
      Version.getVersionForNumber(1), null, null).getText();
  }

  // TODO definitely need more tests here

}
