import java.util.*;
import java.lang.*;
import java.io.*;

public class RSA
{
    static int MAX = 1000000;
    static int MIN = 100000;
    static int FIRSTPASS = 1000;
    static ArrayList<Integer> primeCandidates;
    
    static int MESSAGE = "Hello"
    
    static Hash hash = new Hash();

    public static ArrayList<Integer> getPrimeCandidates()
    {
        return getPrimeCandidates(MIN, MAX);
    }
    
    public static ArrayList<Integer> getPrimeCandidates(int lb, int ub)
    {
        ArrayList<Integer> primeCandidates = new ArrayList();
        Random rn = new Random();
        int lowBound = lb;//10000;//rn.nextInt(MAX[0] + MIN[0] + 1) + MIN[0];
        int upBound = ub;//100000;//rn.nextInt(MAX[1] + MIN[1] + 1) + MIN[1];
        int firstpass = FIRSTPASS;
        
        HashMap<Integer, Boolean> validate = new HashMap<Integer, Boolean>();
        System.out.println("( " + lowBound + ", " + upBound + " )");
        int minUnchecked = 1;
        
        for (int i = 2; i <= Math.min(firstpass, Math.sqrt(upBound)); i++)
        {
            if (validate.get(i) == null)
            {
                for (int j = Math.floorDiv(lowBound, i); j <= upBound / i; j++)
                {
                    validate.put(i*j, true); 
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
        System.out.println("candidates got");
        return primeCandidates;
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
    
    public static int SHA1()
    {
        return -1;
    }
    
    public static int padMessage(int message)
    {
        return hash.SHA1(message);
    }
    
    public static int unpadMessage(int paddedMessage)
    {
        
    }
    
    public static void main(String[] args)
    {
        Random rn = new Random();
        int message = MESSAGE;
        int hashMessage;
        padMessage()
        
        int prime1 = getOnePrime();
        int prime2 = getOnePrime();
        System.out.println("primes: " + prime1 + ", " + prime2);
        
        int n = prime1*prime2;
        int phi = (prime1 - 1)*(prime2 - 1); //totient is multiplicative, and totient of a prime p is p-1
        
        int e = publicKeyExponent(phi);           //e is arbitrary 1<e<phi(n)
        int d = privateKeyExponent(phi, e);
    }
}