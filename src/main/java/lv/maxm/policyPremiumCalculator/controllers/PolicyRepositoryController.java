/**
 * 2020-05-20
 */
package lv.maxm.policyPremiumCalculator.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lv.maxm.policyPremiumCalculator.entities.Policy;
import lv.maxm.policyPremiumCalculator.services.PolicyRepositoryService;

/**
 * Controller for managing policies in private repository
 * 
 * @author MaxM
 *
 */
@RestController
@RequestMapping("/api/v1/policies")
@Api(tags = "Private Policy Repository")
public class PolicyRepositoryController {
	
	@Autowired
	private PolicyRepositoryService policyRepositoryService;
	
	@ApiOperation(value = "Create a new policy", notes = "Create a new policy in the private repository")
	@PostMapping
	public ResponseEntity<Policy> createPolicy(
			@RequestBody @Valid Policy policy,
			@RequestHeader("X-User-Id") @ApiParam(value = "User ID for private access", required = true) String userId) {
		
		try {
			Policy createdPolicy = policyRepositoryService.createPolicy(policy, userId);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}
	
	@ApiOperation(value = "Get a policy by number", notes = "Retrieve a policy by its number from the private repository")
	@GetMapping("/{policyNumber}")
	public ResponseEntity<Policy> getPolicy(
			@PathVariable @ApiParam(value = "Policy number", required = true) String policyNumber,
			@RequestHeader("X-User-Id") @ApiParam(value = "User ID for private access", required = true) String userId) {
		
		Optional<Policy> policy = policyRepositoryService.getPolicy(policyNumber, userId);
		return policy.map(p -> ResponseEntity.ok(p))
					 .orElse(ResponseEntity.notFound().build());
	}
	
	@ApiOperation(value = "Get all policies", notes = "Retrieve all policies accessible to the user")
	@GetMapping
	public ResponseEntity<List<Policy>> getAllPolicies(
			@RequestHeader("X-User-Id") @ApiParam(value = "User ID for private access", required = true) String userId) {
		
		List<Policy> policies = policyRepositoryService.getAllPolicies(userId);
		return ResponseEntity.ok(policies);
	}
	
	@ApiOperation(value = "Update a policy", notes = "Update an existing policy in the private repository")
	@PutMapping("/{policyNumber}")
	public ResponseEntity<Policy> updatePolicy(
			@PathVariable @ApiParam(value = "Policy number", required = true) String policyNumber,
			@RequestBody @Valid Policy policy,
			@RequestHeader("X-User-Id") @ApiParam(value = "User ID for private access", required = true) String userId) {
		
		// Ensure policy number matches the path parameter
		if (!policyNumber.equals(policy.getNumber())) {
			return ResponseEntity.badRequest().build();
		}
		
		try {
			Policy updatedPolicy = policyRepositoryService.updatePolicy(policy, userId);
			return ResponseEntity.ok(updatedPolicy);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@ApiOperation(value = "Delete a policy", notes = "Delete a policy from the private repository")
	@DeleteMapping("/{policyNumber}")
	public ResponseEntity<Void> deletePolicy(
			@PathVariable @ApiParam(value = "Policy number", required = true) String policyNumber,
			@RequestHeader("X-User-Id") @ApiParam(value = "User ID for private access", required = true) String userId) {
		
		boolean deleted = policyRepositoryService.deletePolicy(policyNumber, userId);
		return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}
	
	@ApiOperation(value = "Check if policy exists", notes = "Check if a policy exists and is accessible to the user")
	@GetMapping("/{policyNumber}/exists")
	public ResponseEntity<Boolean> policyExists(
			@PathVariable @ApiParam(value = "Policy number", required = true) String policyNumber,
			@RequestHeader("X-User-Id") @ApiParam(value = "User ID for private access", required = true) String userId) {
		
		boolean exists = policyRepositoryService.policyExists(policyNumber, userId);
		return ResponseEntity.ok(exists);
	}
}