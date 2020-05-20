/**
 * 2020-05-19
 */
package lv.maxm.policyPremiumCalculator.entities;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author MaxM
 *
 */
public class PolicyObject {

	@NotBlank
	private String name;
	
	@Valid
	private List<PolicySubObject> subObjects;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PolicySubObject> getSubObjects() {
		if (subObjects == null) {
			subObjects = new ArrayList<>();
		}
		return subObjects;
	}

	public void setSubObjects(List<PolicySubObject> subObjects) {
		this.subObjects = subObjects;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PolicyObject [name=");
		builder.append(name);
		builder.append(", subObjects=");
		builder.append(subObjects);
		builder.append("]");
		return builder.toString();
	}

}
