package com.github.spring.esdata.loader.junit4;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Custom {@link TestRule} that easily exposes {@link JunitJupiterExtensionLikeTestRule#before}, {@link JunitJupiterExtensionLikeTestRule#beforeAll}, {@link JunitJupiterExtensionLikeTestRule#after},
 * {@link JunitJupiterExtensionLikeTestRule#afterAll} callbacks, just like Junit Jupiter's Extensions.
 *
 * @author tinesoft
 * @see {@linkplain https://stackoverflow.com/a/48759584/445311} <a href="https://stackoverflow.com/a/48759584/445311"></a>
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

	default void before(final Statement base, final Description description) throws Exception {
		// let the implementer decide whether this method is useful to implement
	}

	default void after(final Statement base, final Description description) {
		// let the implementer decide whether this method is useful to implement
	}

	/**
	 * Only runs for Tests that pass
	 */
	default void verify(final Statement base, final Description description) {
		// let the implementer decide whether this method is useful to implement
	}

	default void beforeAll(final Statement base, final Description description) throws Exception {
		this.before(base, description);
	}

	default void afterAll(final Statement base, final Description description) {
		this.after(base, description);
	}

	/**
	 * Only runs for Suites that pass
	 */
	default void verifyAll(final Statement base, final Description description) {
		this.verify(base, description);
	}
}