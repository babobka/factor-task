package ru.babobka.factor.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by dolgopolov.a on 23.11.15.
 */
public class MathUtil {

	private static final BigDecimal THREE_D = BigDecimal.valueOf(3);

	private static final int UP = BigDecimal.ROUND_HALF_UP;

	private MathUtil() {
	}

	// other statements

	public static BigInteger cbrt(BigInteger n) {
		BigDecimal m = new BigDecimal(n); // BigDecimal copy
		BigInteger r = BigInteger.ZERO.setBit(n.bitLength() / 3); // initial
																	// estimate
		for (BigDecimal s = BigDecimal.ZERO; // different from r
				!r.equals(s.toBigInteger()); // loop test: does r=s?
				s = new BigDecimal(r), r = new BigDecimal(r.shiftLeft(1)) // Convert
																			// to
																			// BigDecimal,
						.add(m.divide(s.multiply(s), UP)) // do the tricky
															// division,
						.divide(THREE_D, UP).toBigInteger())
			; // and convert back.
		return r; // return the value
	}

	public static boolean isPowerOfTwo(int x) {
		return (x & (x - 1)) == 0;
	}

	public static long log(long stage, long number) {
		return (long) (Math.log(number) / Math.log(stage));
	}

	public static boolean isPrime(BigInteger a) {
		if (a.bitLength() < 50) {
			return isPrime(a.longValue());
		} else {
			return a.isProbablePrime(50);
		}

	}

	public static boolean isPrime(long a) {

		if (a < 2 || (a != 2 && a % 2 == 0)) {
			return false;
		} else {
			long sqr = (long) (Math.sqrt(a) + 1);
			for (int i = 3; i < sqr; i += 2) {
				if (a % i == 0) {
					return false;
				}
			}
			return true;
		}
	}

	public static BigInteger sqrtBig(BigInteger x) {
		BigInteger div = BigInteger.ZERO.setBit(x.bitLength() / 2);
		BigInteger div2 = div;
		// Loop until we hit the same value twice in a row, or wind
		// up alternating.
		for (;;) {
			BigInteger y = div.add(x.divide(div)).shiftRight(1);
			if (y.equals(div) || y.equals(div2))
				return y;
			div2 = div;
			div = y;
		}
	}

	public static long gcd(long a, long b) {
		if (b == 0) {
			return a;
		} else {
			return gcd(b, a % b);
		}
	}

	public static boolean[] toBinary(long a) {
		long localA = a;
		byte l = (byte) (Math.log(localA) / Math.log(2) + 1);
		boolean[] booleans = new boolean[l];
		for (int i = 0; i < l; i++) {
			booleans[l - i - 1] = localA % 2 != 0;
			localA /= 2;
		}
		return booleans;
	}

	public static long dummyFactor(long n) {
		if (n < 0) {
			throw new IllegalArgumentException();
		}

		if (n != 2 && n % 2 == 0) {
			return 2;
		}
		long sqrt = (long) Math.sqrt(n);
		for (long i = 3; i < sqrt; i += 2) {
			if (n % i == 0) {
				return i;
			}
		}

		return 1;
	}

}
