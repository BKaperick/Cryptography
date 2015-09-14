import java.util.*;
import java.lang.*;
import java.io.*;

public class Hash
{
    public static int SHA1(int message)
    {
        System.out.println("SHA-1 on message " + message);
        
        //Pre-work determines how many 512-bit chunks need to be alotted
        int blocks = (int)Math.ceil(Math.ceil((int)(Math.log(message) / Math.log(2))) / 512);   //Number of 512-bit blocks necessary
        
        System.out.println("Utilizes " + blocks + " blocks");
        
        //Initializes input string as binary bit-set
        BitString binaryRep = decimalToBinary(message, 512*blocks);
        
        System.out.println("Initial Binary representation " + binaryRep);
        
        int origLength = binaryRep.length();
        BitString finalConcat = decimalToBinary(origLength);
        for (int i = 0; i < finalConcat.size(); i++)
        {
            binaryRep.set(447 + i, finalConcat.get(i));
        }        
        
        System.out.println("The final padded binary representation becomes " + binaryRep);
        
        //Splits the binary message into sets of 512 blocks
        ArrayList<BitString> messageBlocks = new ArrayList();
        for (int i = 0; i < blocks; i++)
        {
            messageBlocks.add(binaryRep.subString(i*512, (i+1)*512));
        }
        
        BitString H0 = new BitString("0110 0111 0100 0101 0010 0011 0000 0001");//67452301
        BitString H1 = new BitString("1110 1111 1100 1101 1010 1011 1000 1001");//EFCDAB89
        BitString H2 = new BitString("1001 1000 1011 1010 1101 1100 1111 1110");//98BADCFE
        BitString H3 = new BitString("0001 0000 0011 0010 0101 0100 0111 0110");//10325476
        BitString H4 = new BitString("1100 0011 1101 0010 1110 0001 1111 0000");//C3D2E1F0
        for (int i = 1; i <= blocks; i++)
        {
            ArrayList<BitString> words = blocksToWords(messageBlocks);
            for (int param = 16; param < 80; param++)
            {
                words.set(param, words.get(param - 3).xorP(words.get(param - 8)).xorP(words.get(param - 14)).xorP(words.get(param - 16)).rotateLeft(1));
            }
            BitString tempA = (BitString)H0.clone();
            BitString tempB = (BitString)H1.clone();
            BitString tempC = (BitString)H2.clone();
            BitString tempD = (BitString)H3.clone();
            BitString tempE = (BitString)H4.clone();
            
            for (int param = 0; param < 80; param++)
            {
                BitString tempString = tempA.rotateLeft(5);
                compressWords(param, tempB, tempC, tempD).add(tempE).add(words.get(param)).add(SHA1wordConstants(param));
                tempE = (BitString)tempD.clone();
                tempD = (BitString)tempC.clone();
                tempC = tempB.rotateLeft(30);
                tempB = (BitString)tempA.clone();
                tempA = (BitString)tempString.clone();
            }
            
            H0 = H0.add(tempA);
            H1 = H1.add(tempB);
            H2 = H2.add(tempC);
            H3 = H3.add(tempD);
            H4 = H4.add(tempE);
        }
        
        return H0.concatenate(H1).concatenate(H2).concatenate(H3).concatenate(H2).concatenate(H1).toInt();
    }
    
    /**
     * Takes a list of 521 bit blocks, and returns a list of 8-bit words.
     */
    public static ArrayList<BitString> blocksToWords(ArrayList<BitString> blocksArray)
    {
        ArrayList<BitString> output = new ArrayList();
        for (BitString bs : blocksArray)
        {
            for (int i = 0; i < 16; i++)
            {
                output.add(bs.subString(i*32, (i+1)*32));
            }
        }
        System.out.println("We have a total of " + output.size() + " words");
        return output;
    }
    
    public static BitString SHA1wordConstants(int param)
    {
        if (param <= 19)
        {
            return new BitString("0101 1010 1000 0010 0111 1001 1001 1001");
        }
        if (param <= 39)
        {
            return new BitString("0110 1110 1101 1001 1110 1011 1010 0001");
        }
        if (param <= 59)
        {
            return new BitString("1000 1111 0001 1011 1011 1100 1101 1100");
        }
        if (param <= 79)
        {
            return new BitString("1100 1010 0110 0010 1100 0001 1101 0110");
        }
        else
        {
            return null;
        }
    }
    
    public static BitString compressWords(int param, BitString word1, BitString word2, BitString word3)
    {
        if (param <= 19)
        {
            return (word1.andP(word2)).orP(word1.notP().andP(word3));
        }
        if (param <= 39 || param >= 60)
        {
            return word1.xorP(word2).xorP(word3);
        }
        else
        {
            return (word1.andP(word2)).orP( word1.andP(word2) ).orP( word2.andP(word3) );
        }
    }
    
    
    
    public static BitString decimalToBinary(int num)
    {
        if (num == 0)
        {
            return new BitString();
        }
        int length = 1 + (int)(Math.log(num) / Math.log(2));
        return decimalToBinary(num, length);
    }
    
    /*
     * Converts decimal value into l-bit BitSet (padded with 0's)
     */    
    public static BitString decimalToBinary(int num, int l)
    {
        if (num == 0)
        {
            return new BitString();
        }
        
        int length = 1+(int)(Math.log(num) / Math.log(2));
        System.out.println("\t" + num + " requires a bitstring of length " + length);
        BitString output = new BitString(l);
        for (int i = 0; i < l; i++)
        {
            System.out.println("\t\tAfter " + i + " iterations, " + output);
            output.set(i, num % 2 == 1);
            num = (int) (num / 2);
        }
        return output;
    }
    
    public static void main(String[] args)
    {
        int message = 16;
        int encryptedMessage = SHA1(message);
        System.out.println(encryptedMessage);
    }
}