import java.util.*;
import java.lang.*;
import java.math.BigInteger;
import java.io.*;

public class RSA
{
    static String MAX = "1000000";
    static String MIN = "100000";
    static int FIRSTPASS = 1000;
    static ArrayList<BigInteger> primeCandidates;
    
    static String MESSAGE = "Hello";
    
    static HashMap<String, String> huffmanValues = new HashMap<String, String>();
    
    public static void initializeEncoding()
    {
        huffmanValues.put("a", "1111");
        huffmanValues.put("b", "101000");
        huffmanValues.put("c", "01010");
        huffmanValues.put("d", "11011");
        huffmanValues.put("e", "100");
        huffmanValues.put("f", "01011");
        huffmanValues.put("g", "00001");
        huffmanValues.put("h", "0100");
        huffmanValues.put("i", "0111");
        huffmanValues.put("j", "1101000110");
        huffmanValues.put("k", "11010000");
        huffmanValues.put("l", "10101");
        huffmanValues.put("m", "00011");
        huffmanValues.put("n", "1100");
        huffmanValues.put("o", "1110");
        huffmanValues.put("p", "00000");
        huffmanValues.put("q", "1101000101");
        huffmanValues.put("r", "1011");
        huffmanValues.put("s", "0110");
        huffmanValues.put("t", "001");
        huffmanValues.put("u", "00010");
        huffmanValues.put("v", "1101001");
        huffmanValues.put("w", "101001");
        huffmanValues.put("x", "1101000111");
        huffmanValues.put("y", "110101");
        huffmanValues.put("z", "1101000100");
    }
    
    private static BigInteger bigSqrt(BigInteger num)
    {
        BigInteger a = BigInteger.ONE;
        BigInteger b = new BigInteger(num.shiftRight(5).add(new BigInteger("8")).toString());
        while(b.compareTo(a) >= 0) 
        {
            BigInteger mid = new BigInteger(a.add(b).shiftRight(1).toString());
            if(mid.multiply(mid).compareTo(num) > 0) b = mid.subtract(BigInteger.ONE);
            else a = mid.add(BigInteger.ONE);
        }
        return a.subtract(BigInteger.ONE);
    }
    
    public static BigInteger encode(BigInteger message, BigInteger publicKeyExponent, BigInteger base)
    {
        return fastPower(message, publicKeyExponent).remainder(base);
    }
    
    public static BitString huffmanEncode(String message)
    {
        message = message.toLowerCase();
        BitString output;
        
        String current = String.valueOf(message.charAt(0));
        output = new BitString(huffmanValues.get(current));
        
        for (int i = 1; i < message.length(); i++)
        {
            current = String.valueOf(message.charAt(i));
            output = output.concatenate(new BitString(huffmanValues.get(current)));
        }
        return output;
    }
    
    static Hash hash = new Hash();

    public static ArrayList<BigInteger> getPrimeCandidates()
    {
        return getPrimeCandidates(MIN, MAX);
    }
    
    public static ArrayList<BigInteger> getPrimeCandidates(String lb, String ub)
    {
        ArrayList<BigInteger> primeCandidates = new ArrayList();
        Random rn = new Random();
        BigInteger lowBound = new BigInteger(lb);//10000;//rn.nextInt(MAX[0] + MIN[0] + 1) + MIN[0];
        BigInteger upBound = new BigInteger(ub);//100000;//rn.nextInt(MAX[1] + MIN[1] + 1) + MIN[1];
        int firstpass = FIRSTPASS;
        
        HashMap<BigInteger, Boolean> validate = new HashMap<BigInteger, Boolean>();
        System.out.println("( " + lowBound + ", " + upBound + " )");
        int minUnchecked = 1;
        
        BigInteger counter = new BigInteger("2");
        while (counter.compareTo(bigSqrt(upBound)) < 0) 
        {
            if (validate.get(counter) == null)
            {
                BigInteger subcounter = lowBound.divide(counter);
                while (subcounter.compareTo(upBound.divide(counter)) < 0)
                {
                    validate.put(counter.multiply(subcounter), true); 
                    counter = counter.add(BigInteger.ONE);
                }
                
            }
            counter = counter.add(BigInteger.ONE);
        }
        counter = lowBound;
        while (upBound.compareTo(counter) >= 0)
        {
            if (validate.get(counter) == null)
            {
                primeCandidates.add(counter);
            }
            counter = counter.add(BigInteger.ONE);
        }
        return primeCandidates;
    }
    
    
    
    public static BigInteger getOnePrime()
    {
        ArrayList<BigInteger> candidates;
        if (primeCandidates == null)
        {
            candidates = getPrimeCandidates();
        }
        else
        {
            candidates = primeCandidates;
        }
        return getOnePrime(candidates);
    }
    
    public static BigInteger getOnePrime(ArrayList<BigInteger> candidates)
    {
        Random rn = new Random();
        
        int prime = 0;
        BigInteger root;
        BigInteger num;
        BigInteger half;
        int prod;
        boolean isComposite;
        
        int findNothing = candidates.size();
        
        while (findNothing > 0)
        {
            isComposite = false;
            num = candidates.get(rn.nextInt(candidates.size()));
            root = bigSqrt(num);
            half = num.divide(new BigInteger("2"));
            
            BigInteger counter = BigInteger.valueOf(FIRSTPASS);
            while (counter.compareTo(root) <= 0 ) //i starts at the first unknown factor up to sqrt of num
            {  
                if (isComposite)
                {
                    break;
                }
                BigInteger subcounter = root.add(root.subtract(counter));
                while (subcounter.compareTo(half) <= 0)//j starts at the minimum value multiplied by i to give num, up to num/2
                {
                    if (counter.multiply(subcounter).compareTo(num) >= 0)         //this implies num is composite
                    {
                        isComposite = true;
                        break;
                    }
                    subcounter = subcounter.add(BigInteger.ONE);
                }
                counter = counter.add(BigInteger.ONE);
            }
            if (!isComposite)
            {
                return num;
            }
            findNothing--;
        }
        return null;
    }
    
    public static BigInteger publicKeyExponent(BigInteger phi)
    {
        return getCoprimeLT(phi);
    }
    
    public static BigInteger randomBigInteger(BigInteger upperBound)
    {
        Random rn = new Random();
        BigInteger candidate;
        do 
        {
            candidate = new BigInteger(upperBound.bitLength(), rn);
        } while (upperBound.compareTo(candidate) >= 0);
        return upperBound;
    }
    
    public static boolean Coprime(BigInteger a, BigInteger b)
    {
        if (a.compareTo(b) < 0)
        {
            BigInteger temp = a;
            a = b;
            b = temp;
        }
        
        int steps = 0;
        BigInteger r = BigInteger.ONE;
        BigInteger q;
        while (r.equals(BigInteger.ZERO))
        {
            r = a.remainder(b);
            q = (a.divide(b));
            a = b;
            b = r;
            if (r.equals(BigInteger.ONE))
            {
                return false;
            }
        }
        return true;
    }
    
    public static BigInteger getCoprimeLT(BigInteger maxBound)
    {
        BigInteger candidate = randomBigInteger(maxBound);
        
        while (! Coprime(candidate, maxBound))
        {
            candidate = randomBigInteger(maxBound);
        }
        return candidate;
    }
    
    public static BigInteger privateKeyExponent(BigInteger phi, BigInteger e)
    {
        return productCongruentMod1(phi, e);
    }
    
    public static BigInteger privateKey(BigInteger base, BigInteger exponent, BigInteger messagePrime)
    {
        return fastPower(messagePrime, exponent).remainder(base);
    }
    
    /*
     * Method returns k such that k*num % base = 1
     * it is guaranteed to find at least one k, given for loop bound,
     * but may be more secure to search for higher values of k than those here.
     */
    public static BigInteger productCongruentMod1(BigInteger num, BigInteger base)
    {
        System.out.println("num: " + num.toString() + " base: " + base.toString());
        ArrayList<BigInteger> validOutput = new ArrayList();
        BigInteger counter = BigInteger.ONE;
        while (counter.compareTo(num.add(BigInteger.ONE).divide(base)) <= 0)
        {
            if ((counter.multiply(base).add(BigInteger.ONE)).remainder(num).equals(BigInteger.ZERO))
            {
                validOutput.add(counter);
            }
            counter = counter.add(BigInteger.ONE);
            System.out.println(num.add(BigInteger.ONE).divide(base) + " " + validOutput.toString());
        }
        Random rn = new Random();
        return validOutput.get(rn.nextInt(validOutput.size()));
    }
    
    public static BigInteger publicKey(BigInteger base, BigInteger exponent, BigInteger message)
    {
        return fastPower(message, exponent).remainder(base);
    }    
    
    /*
     * Implementation of exponentation by squaring recursive algorithm
     */
    public static BigInteger fastPower(BigInteger x, BigInteger n)
    {
        if (n.equals(BigInteger.ONE))
        {
            return x;
        }
        if (n.remainder(BigInteger.valueOf(2)).equals(BigInteger.ZERO))
        {
            return fastPower(x.multiply(x), n.divide(BigInteger.valueOf(2)));
        }
        else //(n % 2 == 1)
        {
            return x.multiply(fastPower(x.multiply(x), (n.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2))));
        }
    }
    
    public static int padMessage(int message)
    {
        return Hash.hash(message);
    }
    
    public static int unpadMessage(int paddedMessage)
    {
        return 0;
    }
    
    
    
    public static void main(String[] args)
    {
        Random rn = new Random();
        initializeEncoding();
        
        BitString message = huffmanEncode(MESSAGE);
        //BigInteger hashedMessage = Hash.hash(message);
        
        BigInteger prime1 = getOnePrime();
        BigInteger prime2 = getOnePrime();
        System.out.println(randomBigInteger(BigInteger.valueOf(10000)));
        
        BigInteger n = prime1.multiply(prime2);
        BigInteger phi = prime1.subtract(BigInteger.ONE).multiply(prime2.subtract(BigInteger.ONE)); //totient is multiplicative, and totient of a prime p is p-1
        
        BigInteger e = publicKeyExponent(phi);           //e is arbitrary 1<e<phi(n)
        BigInteger d = privateKeyExponent(phi, e);
        
        BigInteger encryptedMessage = encode(message.toBigInt(), e, n);
        
        System.out.println(message + " : " + message + " : " + encryptedMessage);
        
    }
}