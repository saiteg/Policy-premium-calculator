/**
 * 2020-05-20
 */
package lv.maxm.policyPremiumCalculator.controllers;

import java.math.BigDecimal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lv.maxm.policyPremiumCalculator.entities.Policy;
import lv.maxm.policyPremiumCalculator.services.PremiumCalculatorService;

/**
 * @author MaxM
 *
 */
@RestController
@RequestMapping("/api/v1")
@Api(tags = "Premium Calculator")
public class PremiumCalculatorController {
	
	@Autowired
	PremiumCalculatorService premiumCalculatorService;

	@ApiOperation(value = "Calculate premium", notes = "Compose policy object and send it to get results")
	@PostMapping("calculate")
	public BigDecimal calculate(@RequestBody @Valid Policy policy) {
		BigDecimal sum = premiumCalculatorService.calculate(policy);
		return sum;
	}
	
}
