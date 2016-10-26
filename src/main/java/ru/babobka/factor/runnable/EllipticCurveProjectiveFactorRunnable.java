package ru.babobka.factor.runnable;

import ru.babobka.factor.exception.InfinityPointException;
import ru.babobka.factor.model.EllipticCurveProjective;
import ru.babobka.factor.model.FactoringResult;
import ru.babobka.factor.util.MathUtil;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by dolgopolov.a on 24.11.15.
 */
public class EllipticCurveProjectiveFactorRunnable implements Runnable {
	private final BigInteger n;

	private final AtomicReferenceArray<FactoringResult> resultArray;

	private final AtomicReferenceArray<Thread> threads;

	private static final int MIN_B = 10000;

	private final int id;

	private long B;

	public EllipticCurveProjectiveFactorRunnable(AtomicReferenceArray<FactoringResult> resultArray,
			AtomicReferenceArray<Thread> threads, int id, BigInteger n) {
		this.resultArray = resultArray;
		this.id = id;
		this.n = n;
		this.threads = threads;

		this.B = MathUtil.sqrtBig(MathUtil.sqrtBig(MathUtil.sqrtBig(n))).longValue();
		if (B < MIN_B) {
			B = MIN_B;
		}
	}

	@Override
	public void run() {

		factorRecursive(0, n.bitLength() * 2);

	}

	private void factorRecursive(int tests, int max) {

		EllipticCurveProjective P = EllipticCurveProjective.generateRandomCurve(n);
		BigInteger g = P.getN().gcd(P.getX());
		if (g.compareTo(BigInteger.ONE) >= 1 && g.compareTo(P.getN()) < 0) {
			resultArray.set(id, new FactoringResult(g, P));
			return;
		}

		EllipticCurveProjective beginCurve = P.copy();
		for (long i = 3; i < B; i += 2) {
			if (MathUtil.isPrime(i)) {
				long r = MathUtil.log(i, B);
				for (long j = 0; j < r; j++) {
					try {
						if (Thread.interrupted()) {
							resultArray.set(id, null);
							return;
						}
						P = P.multiply(P, i);
						g = P.getN().gcd(P.getX());
						if (g.compareTo(BigInteger.ONE) >= 1 && g.compareTo(P.getN()) < 0) {
							resultArray.set(id, new FactoringResult(g, beginCurve));
							for (int k = 0; k < threads.length(); k++) {
								if (k != id) {
									Thread thread;
									while ((thread = threads.get(k)) == null) {
										Thread.yield();
									}
									thread.interrupt();
								}
							}
							return;
						}
					} catch (InfinityPointException e) {
						P = EllipticCurveProjective.generateRandomCurve(n);
						beginCurve = P.copy();
						break;
					}
				}
			}
		}

		if (tests < max) {
			factorRecursive(tests + 1, max);
		} else {
			resultArray.set(id, null);
			return;
		}
	}
}
