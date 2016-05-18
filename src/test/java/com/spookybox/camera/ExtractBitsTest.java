package com.spookybox.camera;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.BitSet;

import static com.spookybox.graphics.ByteBufferToImage.leftShiftBit;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExtractBitsTest {
    private byte[] mTestArray;
    private ByteBuffer mBuffer;

    @Before
    public void setUp() {
        mTestArray = new byte[4];
        mTestArray[0] = (byte) 0x0F;
        mTestArray[1] = (byte) 0xF0;
        mBuffer = ByteBuffer.wrap(mTestArray);
    }

    @Test
    public void testReadFirstByte(){
        BitSet testSubject = BitSet.valueOf(mBuffer);
        for(int index = 0; index < 4; index++){
            assertTrue(testSubject.get(index));
        }
        for(int index = 4; index < 16; index++){
            assertFalse(testSubject.get(index));
        }
    }
    
    @Test
    public void testAssignShort(){
        BitSet testSubject = BitSet.valueOf(mBuffer);

        boolean bit1 = testSubject.get(0);
        boolean bit2 = testSubject.get(1);
        boolean bit3 = testSubject.get(2);
        boolean bit4 = testSubject.get(3);
        boolean bit5 = testSubject.get(4);
        boolean bit6 = testSubject.get(5);
        boolean bit7 = testSubject.get(6);
        boolean bit8 = testSubject.get(7);
        boolean bit9 = testSubject.get(8);
        boolean bit10 = testSubject.get(9);
        boolean bit11= testSubject.get(10);
        boolean bit12= testSubject.get(11);
        boolean bit13= testSubject.get(12);
        boolean bit14= testSubject.get(13);
        boolean bit15= testSubject.get(14);
        boolean bit16= testSubject.get(15);

        short value = leftShiftBit(bit1, 15);
        value |= leftShiftBit(bit2, 14);
        value |= leftShiftBit(bit3, 13);
        value |= leftShiftBit(bit4, 12);
        value |= leftShiftBit(bit5, 11);
        value |= leftShiftBit(bit6, 10);
        value |= leftShiftBit(bit7, 9);
        value |= leftShiftBit(bit8, 8);
        value |= leftShiftBit(bit9, 7);
        value |= leftShiftBit(bit10, 6);
        value |= leftShiftBit(bit11, 5);
        value |= leftShiftBit(bit12, 4);
        value |= leftShiftBit(bit13, 3);
        value |= leftShiftBit(bit14, 2);
        value |= leftShiftBit(bit15, 1);
        value |= leftShiftBit(bit16, 0);
        assertTrue(value == 0xF0);

    }
}
