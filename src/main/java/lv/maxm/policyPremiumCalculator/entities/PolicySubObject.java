/**
 * 2020-05-19
 */
package lv.maxm.policyPremiumCalculator.entities;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author MaxM
 *
 */
public class PolicySubObject {

	public enum Type {
		FIRE("0.014", ">", "100", "0.024"), 
		THEFT("0.11", ">=", "15", "0.05");

		private String defaultCoef;
		private String operator;
		private String limit;
		private String extendedCoef;

		Type(String defaultCoef, String operator, String limit, String extendedCoef) {
			this.defaultCoef = defaultCoef;
			this.operator = operator;
			this.limit = limit;
			this.extendedCoef = extendedCoef;
		}

		public BigDecimal calculate(BigDecimal amount) {
			if (amount == null) {
				return BigDecimal.ZERO;
			}
			EvaluationContext context = new StandardEvaluationContext();
			context.setVariable( "amount", amount );
			context.setVariable( "defaultCoef", defaultCoef );
			context.setVariable( "limit", new BigDecimal(limit) );
			context.setVariable( "extendedCoef", extendedCoef );
			ExpressionParser expressionParser = new SpelExpressionParser();
			Expression expression = expressionParser.parseExpression( "#amount " + operator + " #limit? #extendedCoef: #defaultCoef" );
			String coefString = (String) expression.getValue(context);
			BigDecimal sum = amount.multiply(new BigDecimal(coefString));
			return sum;
		}
	}

	@NotBlank
	private String name;
	@NotNull
	private BigDecimal amount;
	@NotNull
	private Type type;

	public PolicySubObject() {
	}

	public PolicySubObject(String name, BigDecimal amount, Type type) {
		this.name = name;
		this.amount = amount;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PolicySubObject [name=");
		builder.append(name);
		builder.append(", amount=");
		builder.append(amount);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}

}
