package lv.maxm.policyPremiumCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

import lv.maxm.policyPremiumCalculator.entities.Policy;
import lv.maxm.policyPremiumCalculator.entities.PolicyObject;
import lv.maxm.policyPremiumCalculator.entities.PolicySubObject;
import lv.maxm.policyPremiumCalculator.entities.PolicySubObject.Type;

public class Tests {

	public static void main(String[] args) {
		BigDecimal total = BigDecimal.ZERO;
		Policy policy = new Policy();
		PolicyObject policyObject1 = new PolicyObject();
		policyObject1.getSubObjects().add(new PolicySubObject("TV", new BigDecimal("500"), Type.FIRE));
//		policyObject1.getSubObjects().add(new PolicySubObject("PC", new BigDecimal("1.4"), Type.FIRE));
		policyObject1.getSubObjects().add(new PolicySubObject("TV", new BigDecimal("102.51"), Type.THEFT));
//		policyObject1.getSubObjects().add(new PolicySubObject("PC", new BigDecimal("22.03"), Type.THEFT));
		PolicyObject policyObject2 = new PolicyObject();
		policyObject2.getSubObjects().add(new PolicySubObject("TV", new BigDecimal("100"), Type.FIRE));
//		policyObject2.getSubObjects().add(new PolicySubObject("PC", new BigDecimal("10.4"), Type.FIRE));
		policyObject2.getSubObjects().add(new PolicySubObject("TV", new BigDecimal("8"), Type.THEFT));
//		policyObject2.getSubObjects().add(new PolicySubObject("PC", new BigDecimal("4.03"), Type.THEFT));
		policy.getObjects().add(policyObject1);
		policy.getObjects().add(policyObject2);
		
		Map<Type, BigDecimal> collect = policy.getObjects()
				.stream()
				.flatMap(p -> p.getSubObjects().stream())
				.collect(Collectors.groupingBy(PolicySubObject::getType, Collectors.reducing(BigDecimal.ZERO,
						PolicySubObject::getAmount,
                        BigDecimal::add)));
		total = collect.entrySet().stream().map(x -> x.getKey().calculate(x.getValue())).reduce(total, BigDecimal::add);
		total = total.setScale(2, RoundingMode.HALF_UP);
		System.out.println(total);
	}

}
