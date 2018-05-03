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

package com.google.zxing.datamatrix.decoder;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author bbrown@google.com (Brian Brown)
 */
public final class DecodedBitStreamParserTestCase extends Assert {

  @Test
  public void testAsciiStandardDecode() throws Exception {
    // ASCII characters 0-127 are encoded as the value + 1
    byte[] bytes = {(byte) ('a' + 1), (byte) ('b' + 1), (byte) ('c' + 1),
                    (byte) ('A' + 1), (byte) ('B' + 1), (byte) ('C' + 1)};
    String decodedString = DecodedBitStreamParser.decode(bytes).getText();
    assertEquals("abcABC", decodedString);
  }

  @Test
  public void testAsciiDoubleDigitDecode() throws Exception{
    // ASCII double digit (00 - 99) Numeric Value + 130
    byte[] bytes = {(byte)       130 , (byte) ( 1 + 130),
                    (byte) (98 + 130), (byte) (99 + 130)};
    String decodedString = DecodedBitStreamParser.decode(bytes).getText();
    assertEquals("00019899", decodedString);
  }

  @Test
  public void testTextDecode() throws Exception {
    // Text encoding (each 3 values are in 2 bytes)
    // a b c = 14 15 16
    // 16 bit = (1600 * 14) + (40 * 15) + 16 + 1 = 23017
    // 1st codeword = 23017 div 256 = 89
    // 2nd codeword = 23017 mod 256 = 233

    // ° = 1 30 4
    // 16 bit = (1600 * 1) + (40 * 30) + 4 + 1 = 2805
    // 1st codeword = 2805 div 256 = 10
    // 2nd codeword = 2805 mod 256 = 245
    byte[] bytes = {(byte) 239 ,
                    (byte) (89),  (byte) (233),
                    (byte) (10),  (byte) (245)
                    };
    String decodedString = DecodedBitStreamParser.decode(bytes).getText();
    assertEquals("abc°", decodedString);
  }

  @Test
  public void testTextAndAsciiStandardDecode() throws Exception {
    // Text encoding and then immediately goes back to ASCII standard encoding
    byte[] bytes = {(byte) 239 ,      (byte) (254),
                    (byte) ('a' + 1), (byte) ('b' + 1), (byte) ('c' + 1)};
    String decodedString = DecodedBitStreamParser.decode(bytes).getText();
    assertEquals("abc", decodedString);
  }



  // TODO(bbrown): Add test cases for each encoding type
  // TODO(bbrown): Add test cases for switching encoding types
}
