/*
 * Grakn - A Distributed Semantic Database
 * Copyright (C) 2016-2018 Grakn Labs Limited
 *
 * Grakn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Grakn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Grakn. If not, see <http://www.gnu.org/licenses/gpl.txt>.
 */

package ai.grakn.test.rule;

import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.List;

/**
 * A {@link TestRule} that is composed of other {@link TestRule}s.
 *
 * @author Felix Chapman
 */
public abstract class CompositeTestRule implements TestRule {

    protected abstract List<TestRule> testRules();

    private ExternalResource innerResource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            CompositeTestRule.this.before();
        }

        @Override
        protected void after() {
            CompositeTestRule.this.after();
        }
    };

    /**
     * Takes all the rules in {@link #testRules()} and applies them to this Test Rule.
     * This is essential because the composite rule may depend on these rules being executed.
     */
    @Override
    public final Statement apply(Statement base, Description description) {
        base = innerResource.apply(base, description);

        for (TestRule each : testRules()) {
            base = each.apply(base, description);
        }

        return base;
    }

    /**
     * Execute the given {@link Runnable} within this context. This is useful in cases when you can't use a JUnit
     * {@link org.junit.Rule} or {@link org.junit.ClassRule} annotation.
     */
    public final void execute(Runnable runnable) {
        try {
            apply(new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    runnable.run();
                }
            }, Description.EMPTY).evaluate();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    protected void before() throws Throwable {

    }

    protected void after() {

    }
}
