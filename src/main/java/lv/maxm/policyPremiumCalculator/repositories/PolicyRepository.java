/**
 * 2020-05-19
 */
package lv.maxm.policyPremiumCalculator.repositories;

import java.util.List;
import java.util.Optional;

import lv.maxm.policyPremiumCalculator.entities.Policy;

/**
 * Repository interface for Policy management
 * 
 * @author MaxM
 *
 */
public interface PolicyRepository {
	
	/**
	 * Save a policy to the repository
	 * 
	 * @param policy Policy to save
	 * @param userId User ID for private access control
	 * @return Saved policy with assigned ID
	 */
	Policy save(Policy policy, String userId);
	
	/**
	 * Find a policy by its number
	 * 
	 * @param policyNumber Policy number to search for
	 * @param userId User ID for private access control
	 * @return Optional containing the policy if found and accessible to user
	 */
	Optional<Policy> findByNumber(String policyNumber, String userId);
	
	/**
	 * Find all policies accessible to a user
	 * 
	 * @param userId User ID for private access control
	 * @return List of policies accessible to the user
	 */
	List<Policy> findAllByUser(String userId);
	
	/**
	 * Delete a policy by its number
	 * 
	 * @param policyNumber Policy number to delete
	 * @param userId User ID for private access control
	 * @return true if policy was deleted, false if not found or not accessible
	 */
	boolean deleteByNumber(String policyNumber, String userId);
	
	/**
	 * Check if a policy exists and is accessible to user
	 * 
	 * @param policyNumber Policy number to check
	 * @param userId User ID for private access control
	 * @return true if policy exists and is accessible to user
	 */
	boolean existsByNumber(String policyNumber, String userId);
}