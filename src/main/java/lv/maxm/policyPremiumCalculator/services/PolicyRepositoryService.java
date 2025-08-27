/**
 * 2020-05-19
 */
package lv.maxm.policyPremiumCalculator.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lv.maxm.policyPremiumCalculator.entities.Policy;
import lv.maxm.policyPremiumCalculator.repositories.PolicyRepository;

/**
 * Service for managing policies in private repository
 * 
 * @author MaxM
 *
 */
@Service
@Validated
public class PolicyRepositoryService {
	
	@Autowired
	private PolicyRepository policyRepository;
	
	/**
	 * Create a new policy in the private repository
	 * 
	 * @param policy Policy to create
	 * @param userId User ID for private access control
	 * @return Created policy
	 */
	public Policy createPolicy(@Valid Policy policy, String userId) {
		if (policy == null) {
			throw new IllegalArgumentException("Policy cannot be null");
		}
		
		if (userId == null || userId.trim().isEmpty()) {
			throw new IllegalArgumentException("User ID cannot be null or empty");
		}
		
		// Check if policy already exists for this user
		if (policy.getNumber() != null && policyRepository.existsByNumber(policy.getNumber(), userId)) {
			throw new IllegalArgumentException("Policy with number " + policy.getNumber() + " already exists");
		}
		
		return policyRepository.save(policy, userId);
	}
	
	/**
	 * Update an existing policy
	 * 
	 * @param policy Policy to update
	 * @param userId User ID for private access control
	 * @return Updated policy
	 */
	public Policy updatePolicy(@Valid Policy policy, String userId) {
		if (policy == null || policy.getNumber() == null) {
			throw new IllegalArgumentException("Policy and policy number cannot be null");
		}
		
		if (userId == null || userId.trim().isEmpty()) {
			throw new IllegalArgumentException("User ID cannot be null or empty");
		}
		
		// Check if policy exists and is accessible to user
		if (!policyRepository.existsByNumber(policy.getNumber(), userId)) {
			throw new IllegalArgumentException("Policy with number " + policy.getNumber() + " not found or not accessible");
		}
		
		return policyRepository.save(policy, userId);
	}
	
	/**
	 * Get a policy by its number
	 * 
	 * @param policyNumber Policy number
	 * @param userId User ID for private access control
	 * @return Optional containing the policy if found
	 */
	public Optional<Policy> getPolicy(String policyNumber, String userId) {
		if (policyNumber == null || userId == null) {
			return Optional.empty();
		}
		
		return policyRepository.findByNumber(policyNumber, userId);
	}
	
	/**
	 * Get all policies for a user
	 * 
	 * @param userId User ID for private access control
	 * @return List of policies owned by the user
	 */
	public List<Policy> getAllPolicies(String userId) {
		if (userId == null) {
			return List.of();
		}
		
		return policyRepository.findAllByUser(userId);
	}
	
	/**
	 * Delete a policy by its number
	 * 
	 * @param policyNumber Policy number to delete
	 * @param userId User ID for private access control
	 * @return true if policy was deleted
	 */
	public boolean deletePolicy(String policyNumber, String userId) {
		if (policyNumber == null || userId == null) {
			return false;
		}
		
		return policyRepository.deleteByNumber(policyNumber, userId);
	}
	
	/**
	 * Check if a policy exists and is accessible to user
	 * 
	 * @param policyNumber Policy number to check
	 * @param userId User ID for private access control
	 * @return true if policy exists and is accessible
	 */
	public boolean policyExists(String policyNumber, String userId) {
		if (policyNumber == null || userId == null) {
			return false;
		}
		
		return policyRepository.existsByNumber(policyNumber, userId);
	}
}