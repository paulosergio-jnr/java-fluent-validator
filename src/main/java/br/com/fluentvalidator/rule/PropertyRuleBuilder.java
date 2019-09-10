package br.com.fluentvalidator.rule;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import br.com.fluentvalidator.Validator;
import br.com.fluentvalidator.builder.Code;
import br.com.fluentvalidator.builder.Critical;
import br.com.fluentvalidator.builder.FieldName;
import br.com.fluentvalidator.builder.Message;
import br.com.fluentvalidator.builder.Must;
import br.com.fluentvalidator.builder.Rule;
import br.com.fluentvalidator.builder.RuleBuilder;
import br.com.fluentvalidator.builder.WhenProperty;
import br.com.fluentvalidator.builder.WithValidator;
import br.com.fluentvalidator.exception.ValidationException;

public class PropertyRuleBuilder<T, P> extends AbstractRuleBuilder<T, P, WhenProperty<T, P>>
		implements RuleBuilder<T, P, WhenProperty<T, P>>, WhenProperty<T, P> {

	private Collection<Rule<P>> rules = new LinkedList<>();
	
	private Validation<P, P> currentValidation;

	public PropertyRuleBuilder(final Function<T, P> function) {
		super(function);
	}

	@Override
	public boolean apply(final T instance) {
		return Objects.nonNull(instance) && ValidationProcessor.process(this.function.apply(instance), this.rules);
	}

	@Override
	public Must<T, P, WhenProperty<T, P>> must(final Predicate<P> predicate) {
		this.currentValidation.must(predicate);
		return this;
	}

	@Override
	public Message<T, P, WhenProperty<T, P>> withMessage(final String message) {
		this.currentValidation.withMessage(message);
		return this;
	}

	@Override
	public Code<T, P, WhenProperty<T, P>> withCode(final String code) {
		this.currentValidation.withCode(code);
		return this;
	}

	@Override
	public FieldName<T, P, WhenProperty<T, P>> withFieldName(final String fieldName) {
		this.currentValidation.withFieldName(fieldName);
		return this;
	}

	@Override
	public Critical<T, P, WhenProperty<T, P>> critical() {
		this.currentValidation.critical();
		return this;
	}

	@Override
	public Critical<T, P, WhenProperty<T, P>> critical(final Class<? extends ValidationException> clazz) {
		this.currentValidation.critical(clazz);
		return this;
	}

	@Override
	public WithValidator<T, P, WhenProperty<T, P>> withValidator(final Validator<P> validator) {
		this.currentValidation.withValidator(validator);
		return this;
	}

	@Override
	public WhenProperty<T, P> when(final Predicate<P> predicate) {
		this.currentValidation = new PropertyValidationRule(predicate);
		this.rules.add(this.currentValidation);
		return this;
	}

	class PropertyValidationRule extends ValidationRule<P, P> {

		protected PropertyValidationRule(final Predicate<P> when) {
			super(when);
		}
		
		@Override
		public boolean apply(P instance) {
			return Boolean.FALSE.equals(super.getWhen().test(instance)) || super.apply(instance);
		}

		@Override
		boolean accept(final P instance) {
			return ValidationProcessor.process(instance, this.getValidator());
		}

	}

}
