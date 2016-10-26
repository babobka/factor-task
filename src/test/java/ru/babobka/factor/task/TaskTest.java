package ru.babobka.factor.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import ru.babobka.nodeserials.NodeRequest;

import ru.babobka.subtask.model.SubTask;

public class TaskTest {

	private final SubTask TASK = new EllipticCurveFactorTask();

	private NodeRequest primeNumberRequest;

	private final BigInteger littleNumber = BigInteger.probablePrime(8, new Random())
			.multiply(BigInteger.probablePrime(8, new Random()));

	private final BigInteger mediumNumber = BigInteger.probablePrime(16, new Random())
			.multiply(BigInteger.probablePrime(16, new Random()));

	private final BigInteger bigNumber = BigInteger.probablePrime(32, new Random())
			.multiply(BigInteger.probablePrime(32, new Random()));

	private final BigInteger veryBigNumber = BigInteger.probablePrime(48, new Random())
			.multiply(BigInteger.probablePrime(48, new Random()));

	private final BigInteger extraBigNumber = BigInteger.probablePrime(52, new Random())
			.multiply(BigInteger.probablePrime(52, new Random()));

	private NodeRequest littleNumberRequestRequest;

	private NodeRequest mediumNumberRequest;

	private NodeRequest bigNumberRequest;

	private NodeRequest veryBigNumberRequest;

	private NodeRequest extraBigNumberRequest;

	@Before
	public void init() {
		Map<String, Serializable> additionMap = new HashMap<>();
		additionMap.put("number", BigInteger.valueOf(104729));
		primeNumberRequest = new NodeRequest(1, 1, "ellipticFactor", additionMap, false, false);

		additionMap = new HashMap<>();
		additionMap.put("number", littleNumber);
		littleNumberRequestRequest = new NodeRequest(1, 1, "ellipticFactor", additionMap, false, false);

		additionMap = new HashMap<>();
		additionMap.put("number", mediumNumber);
		mediumNumberRequest = new NodeRequest(1, 1, "ellipticFactor", additionMap, false, false);

		additionMap = new HashMap<>();
		additionMap.put("number", bigNumber);
		bigNumberRequest = new NodeRequest(1, 1, "ellipticFactor", additionMap, false, false);

		additionMap = new HashMap<>();
		additionMap.put("number", veryBigNumber);
		veryBigNumberRequest = new NodeRequest(1, 1, "ellipticFactor", additionMap, false, false);

		additionMap = new HashMap<>();
		additionMap.put("number", extraBigNumber);
		extraBigNumberRequest = new NodeRequest(1, 1, "ellipticFactor", additionMap, false, false);
	}

	@Test
	public void testValidation() {
		assertFalse(TASK.validateRequest(primeNumberRequest).isValid());
	}

	@Test
	public void testLittleNumber() {
		System.out.println("Try to factor " + littleNumber);
		BigInteger factor = (BigInteger) TASK.execute(littleNumberRequestRequest).getResultMap().get("factor");
		assertEquals(littleNumber.mod(factor), BigInteger.ZERO);
	}

	@Test
	public void testMediumNumber() {
		System.out.println("Try to factor " + mediumNumber);
		BigInteger factor = (BigInteger) TASK.execute(mediumNumberRequest).getResultMap().get("factor");
		assertEquals(mediumNumber.mod(factor), BigInteger.ZERO);
	}

	@Test
	public void testBigNumber() {
		System.out.println("Try to factor " + bigNumber);
		BigInteger factor = (BigInteger) TASK.execute(bigNumberRequest).getResultMap().get("factor");
		assertEquals(bigNumber.mod(factor), BigInteger.ZERO);
	}

	@Test
	public void testVeryBigNumber() {
		System.out.println("Try to factor " + veryBigNumber);
		BigInteger factor = (BigInteger) TASK.execute(veryBigNumberRequest).getResultMap().get("factor");
		assertEquals(veryBigNumber.mod(factor), BigInteger.ZERO);
	}

	@Test
	public void testExtraBigNumber() {
		System.out.println("Try to factor " + extraBigNumber);
		BigInteger factor = (BigInteger) TASK.execute(extraBigNumberRequest).getResultMap().get("factor");
		assertEquals(extraBigNumber.mod(factor), BigInteger.ZERO);
	}
}