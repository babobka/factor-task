package ru.babobka.factor.task;

import ru.babobka.factor.model.EllipticCurveProjective;
import ru.babobka.factor.model.EllipticFactorDistributor;
import ru.babobka.factor.model.EllipticFactorReducer;
import ru.babobka.factor.model.FactoringResult;
import ru.babobka.factor.runnable.EllipticCurveProjectiveFactorRunnable;
import ru.babobka.factor.util.MathUtil;
import ru.babobka.factor.util.ThreadUtil;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.subtask.model.ExecutionResult;
import ru.babobka.subtask.model.Reducer;
import ru.babobka.subtask.model.RequestDistributor;
import ru.babobka.subtask.model.SubTask;
import ru.babobka.subtask.model.ValidationResult;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by dolgopolov.a on 08.12.15.
 */
public class EllipticCurveFactorTask implements SubTask {

	private volatile AtomicReferenceArray<Thread> localThreads;

	public static final String NUMBER = "number";

	public static final String FACTOR = "factor";

	private final EllipticFactorDistributor distributor = new EllipticFactorDistributor();

	private final EllipticFactorReducer reducer = new EllipticFactorReducer();

	private static final String X = "x";

	private static final String Y = "y";

	private static final String A = "a";

	private static final String B = "b";

	private volatile boolean stopped;

	@Override
	public ExecutionResult execute(NodeRequest request) {
		if (!stopped) {

			Map<String, Serializable> result = new HashMap<>();
			BigInteger number = (BigInteger) request.getAddition().get(NUMBER);
			FactoringResult factoringResult;
			if (isRequestDataTooSmall(request.getAddition())) {
				factoringResult = new FactoringResult(BigInteger.valueOf(MathUtil.dummyFactor(number.longValue())),
						EllipticCurveProjective.dummyCurve());
			} else {
				factoringResult = ellipticFactorParallel(number, Runtime.getRuntime().availableProcessors());
			}
			if (factoringResult != null) {
				result.put(NUMBER, number);
				result.put(FACTOR, factoringResult.getFactor());
				result.put(X, factoringResult.getEllipticCurveProjective().getX());
				result.put(Y, factoringResult.getEllipticCurveProjective().getY());
				result.put(A, factoringResult.getEllipticCurveProjective().getA());
				result.put(B, factoringResult.getEllipticCurveProjective().getB());
				return new ExecutionResult(stopped, result);
			}

		}
		return new ExecutionResult(stopped, null);
	}

	@Override
	public void stopTask() {
		stopped = true;
		ThreadUtil.interruptBatch(localThreads);
	}

	@Override
	public ValidationResult validateRequest(NodeRequest request) {
		if (request == null) {
			return new ValidationResult("Empty request", false);
		} else {
			try {
				BigInteger n = (BigInteger) request.getAddition().get(NUMBER);
				if (isRequestDataTooSmall(request.getAddition()) && MathUtil.isPrime(n.longValue())) {
					return new ValidationResult("number is not composite", false);
				} else if (n.isProbablePrime(50)) {
					return new ValidationResult("number is not composite", false);
				}

			} catch (Exception e) {
				return new ValidationResult(e, false);
			}
		}
		return new ValidationResult(true);
	}

	public FactoringResult ellipticFactorParallel(BigInteger n, int cores) {

		localThreads = new AtomicReferenceArray<>(cores);
		AtomicReferenceArray<FactoringResult> resultArray = new AtomicReferenceArray<>(cores);
		for (int i = 0; i < localThreads.length(); i++) {
			localThreads.set(i, new Thread(new EllipticCurveProjectiveFactorRunnable(resultArray, localThreads, i, n)));
			localThreads.get(i).start();
		}
		for (int i = 0; i < localThreads.length(); i++) {
			try {
				localThreads.get(i).join();
			} catch (InterruptedException e) {
				localThreads.get(i).interrupt();
				e.printStackTrace();
			}
		}
		for (int i = 0; i < resultArray.length(); i++) {
			if (resultArray.get(i) != null) {
				return resultArray.get(i);
			}
		}
		return null;
	}

	@Override
	public RequestDistributor getDistributor() {
		return distributor;
	}

	@Override
	public Reducer getReducer() {
		return reducer;
	}

	@Override
	public boolean isRequestDataTooSmall(Map<String, Serializable> addition) {
		BigInteger number = (BigInteger) addition.get(NUMBER);
		return number.bitLength() < 50;
	}

	@Override
	public SubTask newInstance() {

		return new EllipticCurveFactorTask();
	}

}