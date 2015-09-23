import java.util.*;
import java.math.BigInteger;

public class BitString extends java.util.BitSet
{
    public BitString()
    {
        super();
    }

    /*
     * Handles initialization of a string of 1's and 0's, 
     * (e.g. "0101 1010 1000 0010 0111 1001 1001 1001")
     */
    public BitString(String bits)
    {
        super(bits.replaceAll("\\s", "").length());
        
        bits = bits.replaceAll("\\s",  "");
        int bit;
        for (int i = 0; i < bits.length(); i++)
        {
            bit = Character.getNumericValue(bits.charAt(i));
            this.set(i, bit == 1);
        }
        
    }
    
    public BitString(int length)
    {
        super(length);
    }
    
    public BigInteger toBigInt()
    {
        BigInteger output = BigInteger.ZERO;
        int power = 0;
        int base;
        for (int i = 0; i < this.size(); i++)
        {
            boolean b = this.get(i);
            
            output.add(BigInteger.valueOf((b? 1: 0)*Math.pow(2, power))) ;
            power ++;
        }
    }
    
    public BitString andP(BitString other)
    {
        BitString copy = (BitString)this.clone();
        copy.and(other);
        return copy;
    }
    
    public BitString notP()
    {
        BitString copy = (BitString)this.clone();
        copy.flip(0, copy.size() - 1);
        return copy;
    }
    
    public BitString orP(BitString other)
    {
        BitString copy = (BitString)this.clone();
        copy.or(other);
        return copy;
    }
    
    public BitString xorP(BitString other)
    {
        BitString copy = (BitString)this.clone();
        copy.xor(other);
        return copy;
    }
    
    public BitString subString(int index1, int index2)
    {
        BitString string = new BitString(index2 - index1);
        for (int i = index1; i < index2; i++)
        {
            string.set(i, this.get(i));
        }
        return string;
    }

    public BitString rotateLeft(int margin)
    {
        BitString temp = (BitString)this.clone();
        for (int i = 0; i < margin; i++)
        {
            temp.set(i, this.get(this.size() - margin + i));
        }
        for (int i = 0; i < this.size() - margin; i++)
        {
            temp.set(i + margin, this.get(i));
        }
        return temp;
    }
    
    public BitString add(BitString other)
    {
        BitString copy;
        if (this.length() >= other.length())
        {
            copy = (BitString)this.clone();
        }
        else
        {
            copy = (BitString)other.clone();
        }
        
        int carry = 0;
        int bitResult;
        
        for (int i = 0; i < Math.min(this.length(), other.length()); i++)
        {
            bitResult = carry + (this.get(i)? 1: 0) + (other.get(i)? 1:0);
            carry = 0;
            copy.set(i, bitResult % 2 == 1);
            
            if (bitResult >= 2)
            {
                carry = 1;
            }
        }
        
        // Called in the case that neither number had padding zeros, and they add
        // to a number needing an extra bit (e.g. 15 + 15)
        if (carry == 1)
        {
            BitString extraBit = new BitString(copy.size() + 1);
            for (int i = 0; i < copy.size(); i++)
            {
                extraBit.set(i, copy.get(i));
            }
            return extraBit;
        }
        return copy;
    }
    
    public BitString concatenate(BitString other)
    {
        BitString output = new BitString(this.size() + other.size());
        for (int i = 0; i < output.size(); i++)
        {
            if (i < other.size())
            {
                output.set(i, other.get(i));
            }
            else
            {
                output.set(i, this.get(i + other.size()));
            }
        }
        return output;
    }
    
    public int toInt()
    {
        int output = 0;
        for (int i = 0; i < this.length(); i++)
        {
            output += ((this.get(i)) ? 1:0) * (2^i);
        }
        
        return output;
    }
        
}