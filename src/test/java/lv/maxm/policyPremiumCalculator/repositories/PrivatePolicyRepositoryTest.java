package lv.maxm.policyPremiumCalculator.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lv.maxm.policyPremiumCalculator.entities.Policy;
import lv.maxm.policyPremiumCalculator.entities.PolicyObject;
import lv.maxm.policyPremiumCalculator.entities.PolicySubObject;
import lv.maxm.policyPremiumCalculator.entities.PolicySubObject.Type;

class PrivatePolicyRepositoryTest {
	
	private PrivatePolicyRepository repository;
	private Policy testPolicy;
	private final String USER_ID_1 = "user1";
	private final String USER_ID_2 = "user2";
	
	@BeforeEach
	void setUp() {
		repository = new PrivatePolicyRepository();
		
		// Create test policy
		testPolicy = new Policy();
		testPolicy.setNumber("TEST-001");
		testPolicy.setStatus(Policy.Status.REGISTERED);
		
		PolicyObject policyObject = new PolicyObject();
		policyObject.setName("House");
		
		PolicySubObject subObject = new PolicySubObject();
		subObject.setName("TV");
		subObject.setAmount(new BigDecimal("100.00"));
		subObject.setType(Type.FIRE);
		
		policyObject.getSubObjects().add(subObject);
		testPolicy.getObjects().add(policyObject);
	}
	
	@Test
	void testSavePolicy_Success() {
		Policy saved = repository.save(testPolicy, USER_ID_1);
		
		assertEquals(testPolicy.getNumber(), saved.getNumber());
		assertEquals(1, repository.getTotalPolicyCount());
	}
	
	@Test
	void testSavePolicy_NullPolicy_ThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			repository.save(null, USER_ID_1);
		});
	}
	
	@Test
	void testSavePolicy_NullUserId_ThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			repository.save(testPolicy, null);
		});
	}
	
	@Test
	void testSavePolicy_EmptyUserId_ThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			repository.save(testPolicy, "   ");
		});
	}
	
	@Test
	void testSavePolicy_NullPolicyNumber_ThrowsException() {
		testPolicy.setNumber(null);
		assertThrows(IllegalArgumentException.class, () -> {
			repository.save(testPolicy, USER_ID_1);
		});
	}
	
	@Test
	void testSavePolicy_DifferentUsers_SamePolicyNumber_ThrowsException() {
		// Save policy for user1
		repository.save(testPolicy, USER_ID_1);
		
		// Try to save same policy number for user2
		Policy policy2 = new Policy();
		policy2.setNumber("TEST-001");
		policy2.setStatus(Policy.Status.APPROVED);
		
		assertThrows(IllegalArgumentException.class, () -> {
			repository.save(policy2, USER_ID_2);
		});
	}
	
	@Test
	void testSavePolicy_SameUser_UpdatesPolicy() {
		// Save initial policy
		repository.save(testPolicy, USER_ID_1);
		
		// Update policy for same user
		testPolicy.setStatus(Policy.Status.APPROVED);
		Policy updated = repository.save(testPolicy, USER_ID_1);
		
		assertEquals(Policy.Status.APPROVED, updated.getStatus());
		assertEquals(1, repository.getTotalPolicyCount());
	}
	
	@Test
	void testFindByNumber_Success() {
		repository.save(testPolicy, USER_ID_1);
		
		Optional<Policy> found = repository.findByNumber("TEST-001", USER_ID_1);
		
		assertTrue(found.isPresent());
		assertEquals("TEST-001", found.get().getNumber());
	}
	
	@Test
	void testFindByNumber_DifferentUser_NotFound() {
		repository.save(testPolicy, USER_ID_1);
		
		Optional<Policy> found = repository.findByNumber("TEST-001", USER_ID_2);
		
		assertFalse(found.isPresent());
	}
	
	@Test
	void testFindByNumber_NonExistent_NotFound() {
		Optional<Policy> found = repository.findByNumber("NON-EXISTENT", USER_ID_1);
		
		assertFalse(found.isPresent());
	}
	
	@Test
	void testFindAllByUser_Success() {
		// Save policies for user1
		repository.save(testPolicy, USER_ID_1);
		
		Policy policy2 = new Policy();
		policy2.setNumber("TEST-002");
		policy2.setStatus(Policy.Status.APPROVED);
		repository.save(policy2, USER_ID_1);
		
		// Save policy for user2
		Policy policy3 = new Policy();
		policy3.setNumber("TEST-003");
		policy3.setStatus(Policy.Status.REGISTERED);
		repository.save(policy3, USER_ID_2);
		
		List<Policy> user1Policies = repository.findAllByUser(USER_ID_1);
		List<Policy> user2Policies = repository.findAllByUser(USER_ID_2);
		
		assertEquals(2, user1Policies.size());
		assertEquals(1, user2Policies.size());
	}
	
	@Test
	void testFindAllByUser_NoPlicies_EmptyList() {
		List<Policy> policies = repository.findAllByUser(USER_ID_1);
		
		assertTrue(policies.isEmpty());
	}
	
	@Test
	void testDeleteByNumber_Success() {
		repository.save(testPolicy, USER_ID_1);
		
		boolean deleted = repository.deleteByNumber("TEST-001", USER_ID_1);
		
		assertTrue(deleted);
		assertEquals(0, repository.getTotalPolicyCount());
	}
	
	@Test
	void testDeleteByNumber_DifferentUser_NotDeleted() {
		repository.save(testPolicy, USER_ID_1);
		
		boolean deleted = repository.deleteByNumber("TEST-001", USER_ID_2);
		
		assertFalse(deleted);
		assertEquals(1, repository.getTotalPolicyCount());
	}
	
	@Test
	void testDeleteByNumber_NonExistent_NotDeleted() {
		boolean deleted = repository.deleteByNumber("NON-EXISTENT", USER_ID_1);
		
		assertFalse(deleted);
	}
	
	@Test
	void testExistsByNumber_Success() {
		repository.save(testPolicy, USER_ID_1);
		
		assertTrue(repository.existsByNumber("TEST-001", USER_ID_1));
		assertFalse(repository.existsByNumber("TEST-001", USER_ID_2));
		assertFalse(repository.existsByNumber("NON-EXISTENT", USER_ID_1));
	}
	
	@Test
	void testClearAll() {
		repository.save(testPolicy, USER_ID_1);
		assertEquals(1, repository.getTotalPolicyCount());
		
		repository.clearAll();
		assertEquals(0, repository.getTotalPolicyCount());
	}
}