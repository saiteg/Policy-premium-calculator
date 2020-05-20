/**
 * 2020-05-19
 */
package lv.maxm.policyPremiumCalculator.entities;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author MaxM
 *
 */
public class Policy {

	public enum Status {
		REGISTERED, APPROVED
	}

	@NotBlank
	private String number;
	@NotNull
	private Status status;
	@NotEmpty
	@Valid
	private List<PolicyObject> objects;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<PolicyObject> getObjects() {
		if (objects == null) {
			objects = new ArrayList<>();
		}
		return objects;
	}

	public void setObjects(List<PolicyObject> objects) {
		this.objects = objects;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Policy [number=");
		builder.append(number);
		builder.append(", status=");
		builder.append(status);
		builder.append(", objects=");
		builder.append(objects);
		builder.append("]");
		return builder.toString();
	}

}
