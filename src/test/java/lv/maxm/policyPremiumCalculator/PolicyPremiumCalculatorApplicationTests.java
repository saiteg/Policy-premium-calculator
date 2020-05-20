package lv.maxm.policyPremiumCalculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lv.maxm.policyPremiumCalculator.entities.Policy;
import lv.maxm.policyPremiumCalculator.entities.PolicyObject;
import lv.maxm.policyPremiumCalculator.entities.PolicySubObject;
import lv.maxm.policyPremiumCalculator.entities.PolicySubObject.Type;
import lv.maxm.policyPremiumCalculator.services.PremiumCalculatorService;

@SpringBootTest
class PolicyPremiumCalculatorApplicationTests {
	
	@Autowired
	PremiumCalculatorService premiumCalculatorService;
	
	@Test
	void checkCalculation() {
		Policy policy = new Policy();
		policy.setNumber("4234234");
		policy.setStatus(Policy.Status.REGISTERED);
		PolicyObject policyObject = new PolicyObject();
		policyObject.setName("House");
		policyObject.getSubObjects().add(new PolicySubObject("TV", new BigDecimal("500"), Type.FIRE));
		policyObject.getSubObjects().add(new PolicySubObject("TV", new BigDecimal("102.51"), Type.THEFT));
		policy.getObjects().add(policyObject);
		BigDecimal sum = premiumCalculatorService.calculate(policy);
		assertEquals(new BigDecimal("17.13"), sum);
	}
	
	@Test
	void whenInputIsInvalid_thenThrowsException() {
		Policy policy = new Policy();
		assertThrows(ConstraintViolationException.class, () -> {
			premiumCalculatorService.calculate(policy);
	    });
		policy.setNumber("4234234");
		assertThrows(ConstraintViolationException.class, () -> {
			premiumCalculatorService.calculate(policy);
		});
		policy.setStatus(Policy.Status.REGISTERED);
		assertThrows(ConstraintViolationException.class, () -> {
			premiumCalculatorService.calculate(policy);
		});
		PolicyObject policyObject = new PolicyObject();
		policy.getObjects().add(policyObject);
		assertThrows(ConstraintViolationException.class, () -> {
			premiumCalculatorService.calculate(policy);
		});
		policyObject.setName("House");
		PolicySubObject policySubObject = new PolicySubObject();
		policyObject.getSubObjects().add(policySubObject);
		assertThrows(ConstraintViolationException.class, () -> {
			premiumCalculatorService.calculate(policy);
		});
		policySubObject.setName("TV");
		assertThrows(ConstraintViolationException.class, () -> {
			premiumCalculatorService.calculate(policy);
		});
		policySubObject.setAmount(new BigDecimal("5"));
		assertThrows(ConstraintViolationException.class, () -> {
			premiumCalculatorService.calculate(policy);
		});
		policySubObject.setType(Type.FIRE);
		
		BigDecimal sum = premiumCalculatorService.calculate(policy);
		assertEquals(new BigDecimal("0.07"), sum);
	}

}
