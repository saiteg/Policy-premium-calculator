/**
 * 2020-05-19
 */
package lv.maxm.policyPremiumCalculator.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lv.maxm.policyPremiumCalculator.entities.Policy;
import lv.maxm.policyPremiumCalculator.entities.PolicySubObject;
import lv.maxm.policyPremiumCalculator.entities.PolicySubObject.Type;

/**
 * @author MaxM
 *
 */
@Service
@Validated
public class PremiumCalculatorService {

	public BigDecimal calculate(@Valid Policy policy) {
		BigDecimal total = BigDecimal.ZERO;
		Map<Type, BigDecimal> collect = policy.getObjects()
				.stream()
				.flatMap(p -> p.getSubObjects().stream())
				.collect(Collectors.groupingBy(PolicySubObject::getType, Collectors.reducing(BigDecimal.ZERO,
						PolicySubObject::getAmount,
                        BigDecimal::add)));
		total = collect.entrySet().stream().map(x -> x.getKey().calculate(x.getValue())).reduce(total, BigDecimal::add);
		total = total.setScale(2, RoundingMode.HALF_UP);
		return total;
	}
	
}
