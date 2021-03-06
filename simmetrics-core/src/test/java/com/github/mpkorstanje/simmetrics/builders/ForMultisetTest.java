/*-
 * #%L
 * Simmetrics Core
 * %%
 * Copyright (C) 2014 - 2021 Simmetrics Authors
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
package com.github.mpkorstanje.simmetrics.builders;

import com.github.mpkorstanje.simmetrics.Metric;
import com.github.mpkorstanje.simmetrics.StringMetricTest;
import com.github.mpkorstanje.simmetrics.builders.StringMetrics.ForMultiset;
import com.github.mpkorstanje.simmetrics.metrics.Identity;
import com.github.mpkorstanje.simmetrics.tokenizers.Tokenizer;
import com.github.mpkorstanje.simmetrics.tokenizers.Tokenizers;
import com.google.common.collect.Multiset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class ForMultisetTest extends StringMetricTest {

	private final Tokenizer tokenizer = Tokenizers.whitespace();
	private final Metric<Multiset<String>> metric = new Identity<>();

	@Override
	protected ForMultiset getMetric() {
		return new ForMultiset(metric, tokenizer);
	}

	@Override
	protected T[] getTests() {
		return new T[] { new T(0.0f, "To repeat repeat is to repeat", ""),
				new T(0.0f, "To repeat repeat is to repeat", "To repeat is to repeat"),
				new T(1.0f, "To repeat repeat is to repeat", "To  repeat  repeat  is  to  repeat") };
	}

	@Override
	protected boolean satisfiesCoincidence() {
		return false;
	}
	
	@Override
	protected boolean toStringIncludesSimpleClassName() {
		return false;
	}
	
	@Test
	void shouldReturnTokenizer() {
		assertSame(tokenizer, getMetric().getTokenizer());
	}

	@Test
	void shouldReturnMetric() {
		assertSame(metric, getMetric().getMetric());
	}
}
