/**
 * 2020-05-19
 */
package lv.maxm.policyPremiumCalculator.repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import lv.maxm.policyPremiumCalculator.entities.Policy;

/**
 * Private in-memory implementation of PolicyRepository
 * Provides private access control based on user ID
 * 
 * @author MaxM
 *
 */
@Repository
public class PrivatePolicyRepository implements PolicyRepository {
	
	// Map structure: policyNumber -> PolicyEntry
	private final Map<String, PolicyEntry> policies = new ConcurrentHashMap<>();
	
	/**
	 * Internal class to store policy with owner information
	 */
	private static class PolicyEntry {
		private final Policy policy;
		private final String ownerId;
		
		public PolicyEntry(Policy policy, String ownerId) {
			this.policy = policy;
			this.ownerId = ownerId;
		}
		
		public Policy getPolicy() {
			return policy;
		}
		
		public String getOwnerId() {
			return ownerId;
		}
		
		public boolean isAccessibleBy(String userId) {
			return ownerId.equals(userId);
		}
	}
	
	@Override
	public Policy save(Policy policy, String userId) {
		if (policy == null || userId == null || userId.trim().isEmpty()) {
			throw new IllegalArgumentException("Policy and userId cannot be null or empty");
		}
		
		String policyNumber = policy.getNumber();
		if (policyNumber == null || policyNumber.trim().isEmpty()) {
			throw new IllegalArgumentException("Policy number cannot be null or empty");
		}
		
		// Check if policy exists and is owned by different user
		PolicyEntry existingEntry = policies.get(policyNumber);
		if (existingEntry != null && !existingEntry.isAccessibleBy(userId)) {
			throw new IllegalArgumentException("Policy number " + policyNumber + " already exists and is owned by another user");
		}
		
		PolicyEntry entry = new PolicyEntry(policy, userId);
		policies.put(policyNumber, entry);
		return policy;
	}
	
	@Override
	public Optional<Policy> findByNumber(String policyNumber, String userId) {
		if (policyNumber == null || userId == null) {
			return Optional.empty();
		}
		
		PolicyEntry entry = policies.get(policyNumber);
		if (entry != null && entry.isAccessibleBy(userId)) {
			return Optional.of(entry.getPolicy());
		}
		
		return Optional.empty();
	}
	
	@Override
	public List<Policy> findAllByUser(String userId) {
		if (userId == null) {
			return List.of();
		}
		
		return policies.values().stream()
				.filter(entry -> entry.isAccessibleBy(userId))
				.map(PolicyEntry::getPolicy)
				.collect(Collectors.toList());
	}
	
	@Override
	public boolean deleteByNumber(String policyNumber, String userId) {
		if (policyNumber == null || userId == null) {
			return false;
		}
		
		PolicyEntry entry = policies.get(policyNumber);
		if (entry != null && entry.isAccessibleBy(userId)) {
			policies.remove(policyNumber);
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean existsByNumber(String policyNumber, String userId) {
		if (policyNumber == null || userId == null) {
			return false;
		}
		
		PolicyEntry entry = policies.get(policyNumber);
		return entry != null && entry.isAccessibleBy(userId);
	}
	
	/**
	 * Get total number of policies in the repository (for testing/monitoring)
	 * 
	 * @return Total number of policies
	 */
	public int getTotalPolicyCount() {
		return policies.size();
	}
	
	/**
	 * Clear all policies from the repository (for testing)
	 */
	public void clearAll() {
		policies.clear();
	}
}