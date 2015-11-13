/*
 * #%L
 * Simmetrics Core
 * %%
 * Copyright (C) 2014 - 2015 Simmetrics Authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.simmetrics;

import static org.junit.Assert.fail;
import static org.simmetrics.tokenizers.Tokenizers.whitespace;

import java.util.List;

import org.junit.Test;
import org.simmetrics.tokenizers.Tokenizer;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

@SuppressWarnings("javadoc")
public abstract class MultisetMetricTest extends MetricTest<Multiset<String>> {

	protected static final class T {
		protected final Multiset<String> a;
		protected final Multiset<String> b;
		protected final float similarity;

		public T(float similarity, Multiset<String> a, Multiset<String> b) {
			this.a = a;
			this.b = b;
			this.similarity = similarity;
		}

		public T(float similarity, String a, String b) {
			this(whitespace(), similarity, a, b);
		}

		public T(Tokenizer t, float similarity, String a, String b) {
			this(similarity, t.tokenizeToMultiset(a), t.tokenizeToMultiset(b));
		}

		public T(float similarity, List<String> a, List<String> b) {
			this(similarity, HashMultiset.create(a), HashMultiset.create(b));
		}
	}

	private static void testContainsListWithNullVsListWithouthNullTest(
			T[] listTests) {
		for (T t : listTests) {
			if (t.a.contains(null) ^ t.b.contains(null)) {
				return;
			}
		}

		fail("tests did not contain list with null vs list without null test");
	}

	private static void testIllegalArgumentException(Metric<Multiset<String>> metric,
			Multiset<String> a, Multiset<String> b, Multiset<String> nullList) {
		try {
			metric.compare(nullList, b);
			fail("Metric should have thrown a illegal argument exception for the first argument");
		} catch (IllegalArgumentException ignored) {
			// Ignored
		}

		try {
			metric.compare(a, nullList);
			fail("Metric should have thrown a illegal argument exception for the second argument");
		} catch (IllegalArgumentException ignored) {
			// Ignored
		}

		try {
			metric.compare(nullList, nullList);
			fail("Metric should have thrown a illegal argument exception for either argument");
		} catch (IllegalArgumentException ignored) {
			// Ignored
		}
	}

	private static MetricTest.T<Multiset<String>>[] transformTest(T... tests) {
		@SuppressWarnings("unchecked")
		MetricTest.T<Multiset<String>>[] transformed = new MetricTest.T[tests.length];
		for (int i = 0; i < tests.length; i++) {
			T t = tests[i];
			transformed[i] = new MetricTest.T<>(t.similarity,t.a, t.b);
		}
		return transformed;
	}

	private T[] listTests;

	@Override
	protected final Multiset<String> getEmpty() {
		return HashMultiset.create();
	}

	protected abstract T[] getListTests();

	public Multiset<String> getNullList() {
		HashMultiset<String> create = HashMultiset.create();
		create.add(null);
		return create;
	}

	@Override
	protected final MetricTest.T<Multiset<String>>[] getTests() {
		listTests = getListTests();
		return transformTest(listTests);
	}

	@Test
	public void nullValues() {
		if (supportsNullValues()) {
			testContainsListWithNullVsListWithouthNullTest(listTests);
		} else {
			for (T t : listTests) {
				testIllegalArgumentException(metric, t.a, t.b, getNullList());
			}
		}
	}

	protected boolean supportsNullValues() {
		return true;
	}

}