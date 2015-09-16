import java.util.*;
import java.lang.*;
import java.math.BigInteger;
import java.io.*;

public class RSA
{
    static String MAX = "1000000";
    static String MIN = "100000";
    static int FIRSTPASS = 1000;
    static ArrayList<Integer> primeCandidates;
    
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

    public static ArrayList<Integer> getPrimeCandidates()
    {
        return getPrimeCandidates(MIN, MAX);
    }
    
    public static ArrayList<Integer> getPrimeCandidates(String lb, String ub)
    {
        ArrayList<Integer> primeCandidates = new ArrayList();
        Random rn = new Random();
        BigInteger lowBound = new BigInteger(lb);//10000;//rn.nextInt(MAX[0] + MIN[0] + 1) + MIN[0];
        BigInteger upBound = new BigInteger(ub);//100000;//rn.nextInt(MAX[1] + MIN[1] + 1) + MIN[1];
        int firstpass = FIRSTPASS;
        
        HashMap<Integer, Boolean> validate = new HashMap<Integer, Boolean>();
        System.out.println("( " + lowBound + ", " + upBound + " )");
        int minUnchecked = 1;
        
        BigInteger counter = new BigInteger("2");
        for (while counter < (int)bigSqrt(upBound))
        {
            if (validate.get(counter) == null)
            {
                for (int j = Math.floorDiv(lowBound, counter); j <= upBound / counter; j++)
                {
                    validate.put(counter*j, true); 
                }
                
            }
        }
        for (int i = lowBound; i <= upBound; i++)
        {
            if (validate.get(i) == null)
            {
                primeCandidates.add(i);
            }
        }
        return primeCandidates;
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
    }
    
    public static int getOnePrime()
    {
        ArrayList<Integer> candidates;
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
    
    public static int getOnePrime(ArrayList<Integer> candidates)
    {
        Random rn = new Random();
        
        int prime = 0;
        int factors;
        int root;
        int num;
        int half;
        int prod;
        boolean isComposite;
        
        while (prime == 0)
        {
            isComposite = false;
            num = candidates.get(rn.nextInt(candidates.size()));
            factors = 0;
            root = (int)(Math.sqrt(num));
            half = (int)(num / 2);
            
            for (int i = FIRSTPASS + 1; i <= root; i++) //i starts at the first unknown factor up to sqrt of num
            {  
                if (isComposite)
                {
                    break;
                }
                for (int j = root + (root - i); j <= half; j++)//j starts at the minimum value multiplied by i to give num, up to num/2
                {
                    if (i*j >= num)         //this implies num is composite
                    {
                        isComposite = true;
                        break;
                    }
                    
                }
            }
            if (!isComposite)
            {
                return num;
            }
        }
        return -1;
    }
    
    public static int publicKeyExponent(int phi)
    {
        return getCoprimeLT(phi);
    }
    
    public static int getCoprimeLT(int maxBound)
    {
        Random rn = new Random();
        int candidate;
        System.out.println("max: " + maxBound);
        candidate = rn.nextInt(maxBound - 1) + 1;
        
        while (! Coprime(candidate, maxBound))
        {
            candidate = rn.nextInt(maxBound - 1) + 1;
        }
        return candidate;
    }
    
    public static boolean Coprime(int a, int b)
    {
        if (a < b)
        {
            int temp = a;
            a = b;
            b = temp;
        }
        
        int steps = 0;
        int r = -1;
        int q;
        while (r != 0)
        {
            r = a % b;
            q = (int) (a / b);
            a = b;
            b = r;
            if (r == 1)
            {
                return false;
            }
        }
        return true;
    }
    
    public static int privateKeyExponent(int phi, int e)
    {
        return productCongruentMod1(phi, e);
    }
    
    /*
     * Method returns k such that k*num % base = 1
     * it is guaranteed to find at least one k, given for loop bound,
     * but may be more secure to search for higher values of k than those here.
     */
    public static int productCongruentMod1(int num, int base)
    {
        ArrayList<Integer> validOutput = new ArrayList();
        for (int k = 1; k <= ((num + 1) / base); k++)
        {
            if ((k*base + 1) % num == 0)
            {
                validOutput.add(k);
            }
        }
        Random rn = new Random();
        return validOutput.get(rn.nextInt(validOutput.size()));
    }
    
    public static int publicKey(int base, int exponent, int message)
    {
        return fastPower(message, exponent) % base;
    }
    
    public static int privateKey(int base, int exponent, int messagePrime)
    {
        return fastPower(messagePrime, exponent) % base;
    }
    
    /*
     * Implementation of exponentation by squaring recursive algorithm
     */
    public static int fastPower(int x, int n)
    {
        if (n == 1)
        {
            return x;
        }
        if (n % 2 == 0)
        {
            return fastPower(x*x, n / 2);
        }
        else //(n % 2 == 1)
        {
            return x*fastPower(x*x, (n-1) / 2);
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
    
    public static int encode(int message, int publicKeyExponent, int base)
    {
        return fastPower(message, publicKeyExponent) % base;
    }
    
    public static void main(String[] args)
    {
        Random rn = new Random();
        initializeEncoding();
        
        BitString message = huffmanEncode(MESSAGE);
        int hashedMessage = Hash.hash(message);
        
        int prime1 = getOnePrime();
        int prime2 = getOnePrime();
        
        int n = prime1*prime2;
        int phi = (prime1 - 1)*(prime2 - 1); //totient is multiplicative, and totient of a prime p is p-1
        
        int e = publicKeyExponent(phi);           //e is arbitrary 1<e<phi(n)
        int d = privateKeyExponent(phi, e);
        
        int encryptedMessage = encode(hashedMessage, e, n);
        
        System.out.println(message + " : " + hashedMessage + " : " + encryptedMessage);
        
    }
}