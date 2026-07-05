package lv.maxm.policyPremiumCalculator.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lv.maxm.policyPremiumCalculator.entities.Policy;
import lv.maxm.policyPremiumCalculator.entities.PolicyObject;
import lv.maxm.policyPremiumCalculator.entities.PolicySubObject;
import lv.maxm.policyPremiumCalculator.entities.PolicySubObject.Type;
import lv.maxm.policyPremiumCalculator.repositories.PrivatePolicyRepository;

@SpringBootTest
class PolicyRepositoryServiceTest {
	
	@Autowired
	private PolicyRepositoryService policyRepositoryService;
	
	@Autowired
	private PrivatePolicyRepository repository;
	
	private final String USER_ID_1 = "testuser1";
	private final String USER_ID_2 = "testuser2";
	
	private Policy createTestPolicy(String policyNumber) {
		Policy policy = new Policy();
		policy.setNumber(policyNumber);
		policy.setStatus(Policy.Status.REGISTERED);
		
		PolicyObject policyObject = new PolicyObject();
		policyObject.setName("House");
		
		PolicySubObject subObject = new PolicySubObject();
		subObject.setName("TV");
		subObject.setAmount(new BigDecimal("100.00"));
		subObject.setType(Type.FIRE);
		
		policyObject.getSubObjects().add(subObject);
		policy.getObjects().add(policyObject);
		
		return policy;
	}
	
	@Test
	void testCreatePolicy_Success() {
		repository.clearAll(); // Clean state for test
		
		Policy policy = createTestPolicy("TEST-CREATE-001");
		Policy created = policyRepositoryService.createPolicy(policy, USER_ID_1);
		
		assertEquals("TEST-CREATE-001", created.getNumber());
		assertTrue(policyRepositoryService.policyExists("TEST-CREATE-001", USER_ID_1));
	}
	
	@Test
	void testCreatePolicy_DuplicateNumber_ThrowsException() {
		repository.clearAll(); // Clean state for test
		
		Policy policy1 = createTestPolicy("TEST-DUPLICATE-001");
		policyRepositoryService.createPolicy(policy1, USER_ID_1);
		
		Policy policy2 = createTestPolicy("TEST-DUPLICATE-001");
		assertThrows(IllegalArgumentException.class, () -> {
			policyRepositoryService.createPolicy(policy2, USER_ID_1);
		});
	}
	
	@Test
	void testCreatePolicy_NullPolicy_ThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			policyRepositoryService.createPolicy(null, USER_ID_1);
		});
	}
	
	@Test
	void testCreatePolicy_NullUserId_ThrowsException() {
		Policy policy = createTestPolicy("TEST-NULL-USER-001");
		assertThrows(IllegalArgumentException.class, () -> {
			policyRepositoryService.createPolicy(policy, null);
		});
	}
	
	@Test
	void testUpdatePolicy_Success() {
		repository.clearAll(); // Clean state for test
		
		Policy policy = createTestPolicy("TEST-UPDATE-001");
		policyRepositoryService.createPolicy(policy, USER_ID_1);
		
		policy.setStatus(Policy.Status.APPROVED);
		Policy updated = policyRepositoryService.updatePolicy(policy, USER_ID_1);
		
		assertEquals(Policy.Status.APPROVED, updated.getStatus());
	}
	
	@Test
	void testUpdatePolicy_NonExistent_ThrowsException() {
		repository.clearAll(); // Clean state for test
		
		Policy policy = createTestPolicy("TEST-NONEXISTENT-001");
		assertThrows(IllegalArgumentException.class, () -> {
			policyRepositoryService.updatePolicy(policy, USER_ID_1);
		});
	}
	
	@Test
	void testGetPolicy_Success() {
		repository.clearAll(); // Clean state for test
		
		Policy policy = createTestPolicy("TEST-GET-001");
		policyRepositoryService.createPolicy(policy, USER_ID_1);
		
		Optional<Policy> retrieved = policyRepositoryService.getPolicy("TEST-GET-001", USER_ID_1);
		
		assertTrue(retrieved.isPresent());
		assertEquals("TEST-GET-001", retrieved.get().getNumber());
	}
	
	@Test
	void testGetPolicy_DifferentUser_NotFound() {
		repository.clearAll(); // Clean state for test
		
		Policy policy = createTestPolicy("TEST-GET-PRIVATE-001");
		policyRepositoryService.createPolicy(policy, USER_ID_1);
		
		Optional<Policy> retrieved = policyRepositoryService.getPolicy("TEST-GET-PRIVATE-001", USER_ID_2);
		
		assertFalse(retrieved.isPresent());
	}
	
	@Test
	void testGetAllPolicies_Success() {
		repository.clearAll(); // Clean state for test
		
		Policy policy1 = createTestPolicy("TEST-ALL-001");
		Policy policy2 = createTestPolicy("TEST-ALL-002");
		
		policyRepositoryService.createPolicy(policy1, USER_ID_1);
		policyRepositoryService.createPolicy(policy2, USER_ID_1);
		
		// Create policy for different user
		Policy policy3 = createTestPolicy("TEST-ALL-003");
		policyRepositoryService.createPolicy(policy3, USER_ID_2);
		
		List<Policy> user1Policies = policyRepositoryService.getAllPolicies(USER_ID_1);
		List<Policy> user2Policies = policyRepositoryService.getAllPolicies(USER_ID_2);
		
		assertEquals(2, user1Policies.size());
		assertEquals(1, user2Policies.size());
	}
	
	@Test
	void testDeletePolicy_Success() {
		repository.clearAll(); // Clean state for test
		
		Policy policy = createTestPolicy("TEST-DELETE-001");
		policyRepositoryService.createPolicy(policy, USER_ID_1);
		
		boolean deleted = policyRepositoryService.deletePolicy("TEST-DELETE-001", USER_ID_1);
		
		assertTrue(deleted);
		assertFalse(policyRepositoryService.policyExists("TEST-DELETE-001", USER_ID_1));
	}
	
	@Test
	void testDeletePolicy_DifferentUser_NotDeleted() {
		repository.clearAll(); // Clean state for test
		
		Policy policy = createTestPolicy("TEST-DELETE-PRIVATE-001");
		policyRepositoryService.createPolicy(policy, USER_ID_1);
		
		boolean deleted = policyRepositoryService.deletePolicy("TEST-DELETE-PRIVATE-001", USER_ID_2);
		
		assertFalse(deleted);
		assertTrue(policyRepositoryService.policyExists("TEST-DELETE-PRIVATE-001", USER_ID_1));
	}
}