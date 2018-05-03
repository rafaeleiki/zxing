package com.google.zxing.qrcode.detector;


import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;

public class AlignmentPatternFinderTestCase extends Assert {
	
	private static BitMatrix matrix; 
	private static ResultPointCallback resultPointCallback;
	private static AlignmentPatternFinder apf; 
	
	@BeforeClass
	public static void before() {
		matrix = new BitMatrix(100);
		matrix.set(95, 95);
		matrix.set(95, 96);
		matrix.set(95, 97);
		matrix.set(95, 98);
		matrix.set(95, 99);
		matrix.set(99, 95);
		matrix.set(99, 96);
		matrix.set(99, 97);
		matrix.set(99, 98);
		matrix.set(96, 95);
		matrix.set(96, 99);
		matrix.set(97, 95);
		matrix.set(97, 97);
		matrix.set(97, 99);
		matrix.set(98, 95);
		matrix.set(98, 99);
	}
	
	@Test
	public void testValid1() throws NotFoundException {
	    apf = new AlignmentPatternFinder(matrix, 0,0, 100, 100, 1, resultPointCallback);
	    testValid(apf);
	    
	}
	
	@Test
	public void testValid2() throws NotFoundException {
	    apf = new AlignmentPatternFinder(matrix, 95,95, 5, 5, 1, resultPointCallback);
	    testValid(apf);
	    
	}

	private static BitMatrix topPattern() {
		BitMatrix m = new BitMatrix(100);
		m.set(0, 0);
		m.set(0, 1);
		m.set(0, 2);
		m.set(0, 3);
		m.set(0, 4);
		m.set(4, 0);
		m.set(4, 1);
		m.set(4, 2);
		m.set(4, 3);
		m.set(4, 4);
		m.set(1, 0);
		m.set(1, 4);
		m.set(2, 0);
		m.set(2, 5);
		m.set(2, 2);
		m.set(3, 0);
		m.set(3, 5);
		return m;
		
	}
	
	@Test(expected = Error.class) 
	public void testInvalidX1(){
	    apf = new AlignmentPatternFinder(topPattern(), -1,0,6, 5, 1, resultPointCallback);
	    AlignmentPattern ap = apf.find();
	    
	}
	
	
	@Test(expected = Error.class) 
	public void testInvalidX2(){
	    apf = new AlignmentPatternFinder(matrix, 96,0,5, 100, 1, resultPointCallback);
	    AlignmentPattern ap = apf.find();
	    
	}
	

	@Test(expected = Error.class) 
	public void testInvalidY1(){
	    apf = new AlignmentPatternFinder(topPattern(), 0,-1,5, 6, 1, resultPointCallback);
	    AlignmentPattern ap = apf.find();
	    
	}
	
	@Test(expected = Error.class) 
	public void testInvalidY2(){
	    apf = new AlignmentPatternFinder(matrix, 0,96,100, 4, 1, resultPointCallback);
	    AlignmentPattern ap = apf.find();
	    
	}
	

	@Test(expected = Error.class) 
	public void testInvalidheight1(){
	    apf = new AlignmentPatternFinder(matrix, 95,95,5, 4, 1, resultPointCallback);
	    AlignmentPattern ap = apf.find();
	    
	}
	
	@Test(expected = Error.class) 
	public void testInvalidheight2(){
	    apf = new AlignmentPatternFinder(matrix, 0,0,100, 4, 1, resultPointCallback);
	    AlignmentPattern ap = apf.find();
	    
	}
	
	@Test(expected = Error.class) 
	public void testInvalidwidth1(){
	    apf = new AlignmentPatternFinder(matrix, 95,95,4, 5, 1, resultPointCallback);
	    AlignmentPattern ap = apf.find();
	    
	}
	
	@Test(expected = Error.class) 
	public void testInvalidwidth2(){
	    apf = new AlignmentPatternFinder(matrix, 0,0,4, 100, 1, resultPointCallback);
	    AlignmentPattern ap = apf.find();
	    
	}
	
	private static void testValid(AlignmentPatternFinder apf) throws NotFoundException {
		  AlignmentPattern ap = new AlignmentPattern(97.5f, 97.5f, 1);
	    assertEquals(ap, apf.find());
	}

}
