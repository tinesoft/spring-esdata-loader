package com.github.spring.esdata.loader.junit4;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Custom {@link TestRule} that easily exposes {@link JunitJupiterExtensionLikeTestRule#before}, {@link JunitJupiterExtensionLikeTestRule#beforeAll}, {@link JunitJupiterExtensionLikeTestRule#after},
 * {@link JunitJupiterExtensionLikeTestRule#afterAll} callbacks, just like Junit Jupiter API does.
 *
 * @author tinesoft
 *
 */
public interface JunitJupiterExtensionLikeTestRule extends TestRule {
	@Override
	default Statement apply(final Statement base, final Description description) {
		if (description.isTest()) {
			return new Statement() {
				@Override
				public void evaluate() throws Throwable {
					JunitJupiterExtensionLikeTestRule.this.before(base, description);
					try {
						base.evaluate();
						JunitJupiterExtensionLikeTestRule.this.verify(base, description);
					} finally {
						JunitJupiterExtensionLikeTestRule.this.after(base, description);
					}
				}
			};
		}
		if (description.isSuite()) {
			return new Statement() {

				@Override
				public void evaluate() throws Throwable {
					JunitJupiterExtensionLikeTestRule.this.beforeAll(base, description);
					try {
						base.evaluate();
						JunitJupiterExtensionLikeTestRule.this.verifyAll(base, description);
					} finally {
						JunitJupiterExtensionLikeTestRule.this.afterAll(base, description);
					}
				}
			};
		}
		return base;
	}

	/**
	 * Callback that is called <i>after</i> a test is executed.
	 *
	 * @param base        The {@link Statement} to be modified
	 * @param description A {@link Description} of the test implemented in {@code base}
	 * @throws Exception if an error occurres
	 */
	default void before(final Statement base, final Description description) throws Exception {
		// let the implementer decide whether this method is useful to implement
	}

	/**
	 * Callback that is called <i>after</i> a test is executed.
	 *
	 * @param base        The {@link Statement} to be modified
	 * @param description A {@link Description} of the test implemented in {@code base}
	 * @throws Exception if an error occurres
	 */
	default void after(final Statement base, final Description description) throws Exception{
		// let the implementer decide whether this method is useful to implement
	}

	/**
	 * Callback that is called <i>only if</i> a test passes.
	 *
	 * @param base        The {@link Statement} to be modified
	 * @param description A {@link Description} of the test implemented in {@code base}
	 * @throws Exception if an error occurres
	 */
	default void verify(final Statement base, final Description description) throws Exception{
		// let the implementer decide whether this method is useful to implement
	}

	/**
	 * Callback that is called <i>before</i> all tests are executed.
	 *
	 * @param base        The {@link Statement} to be modified
	 * @param description A {@link Description} of the test implemented in {@code base}
	 * @throws Exception  if an error occurres
	 */
	default void beforeAll(final Statement base, final Description description) throws Exception {
		this.before(base, description);
	}

	/**
	 * Callback that is called <i>after</i> all tests are executed.
	 *
	 * @param base        The {@link Statement} to be modified
	 * @param description A {@link Description} of the test implemented in {@code base}
	 * @throws Exception if an error occurres
	 */
	default void afterAll(final Statement base, final Description description) throws Exception {
		this.after(base, description);
	}


	/**
	 * Callback that is called <i>only if</i> all tests pass.
	 *
	 * @param base The {@link Statement} to be modified
	 * @param description A {@link Description} of the test implemented in {@code base}
	 * @throws Exception  if an error occurres
	 */
	default void verifyAll(final Statement base, final Description description) throws Exception {
		this.verify(base, description);
	}
}