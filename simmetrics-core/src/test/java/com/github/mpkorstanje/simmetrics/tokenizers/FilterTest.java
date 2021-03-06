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
package com.github.mpkorstanje.simmetrics.tokenizers;

import com.github.mpkorstanje.simmetrics.tokenizers.Tokenizers.Filter;
import com.github.mpkorstanje.simmetrics.tokenizers.Tokenizers.Filter.TransformFilter;
import com.github.mpkorstanje.simmetrics.tokenizers.Tokenizers.Transform;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.hamcrest.MatcherAssert.assertThat;
import static com.github.mpkorstanje.simmetrics.PredicateMatcher.accepts;
import static com.github.mpkorstanje.simmetrics.PredicateMatcher.rejects;

class FilterTest extends TokenizerTest {

	private final Tokenizer whitespace = Tokenizers.whitespace();
	private final Function<String, String> identity = Function.identity();
	private final java.util.function.Predicate<String> sometimesPassing = s -> asList("sometimes", "passing").contains(s);
	private final java.util.function.Predicate<String> occasionallyPassing = s -> asList("occasionally", "passing").contains(s);

	static Predicate<String> theAndOr() {
		return s -> !asList("the", "and", "or").contains(s);
	}

	@Override
	protected T[] getTests() {
		return new T[]{
				new T("the mouse and cat or dog", "mouse", "cat", "dog"),
				new T("")
		};
	}

	@Override
	protected Tokenizer getTokenizer() {
		return new Filter(whitespace, theAndOr());
	}

	@Test
	void shouldCreateCombinedForFilter() {
		Filter f = new Filter(whitespace, sometimesPassing);
		Tokenizer tokenizer = Filter.createCombined(f, occasionallyPassing);

		assertEquals(Filter.class, tokenizer.getClass());

		Filter filter = (Filter) tokenizer;

		assertSame(whitespace, filter.getTokenizer());
		assertThat(filter.getPredicate(), accepts("passing"));
		assertThat(filter.getPredicate(), rejects("sometimes", "occasionally"));
	}

	@Test
	void shouldCreateCombinedForTransformFilter() {
		Transform transform = new Transform(whitespace, identity);
		TransformFilter t = new TransformFilter(transform, sometimesPassing);
		Tokenizer tokenizer = Filter.createCombined(t, occasionallyPassing);

		assertEquals(TransformFilter.class, tokenizer.getClass());

		TransformFilter transformFilter = (TransformFilter) tokenizer;

		assertSame(transform, transformFilter.getTokenizer());
		assertThat(transformFilter.getPredicate(), accepts("passing"));
		assertThat(transformFilter.getPredicate(), rejects("sometimes", "occasionally"));
	}

	@Test
	void shouldCreateCombinedForTransform() {
		Transform transform = new Transform(whitespace, identity);
		Tokenizer tokenizer = Filter.createCombined(transform, sometimesPassing);

		assertEquals(TransformFilter.class, tokenizer.getClass());

		TransformFilter transformFilter = (TransformFilter) tokenizer;

		assertSame(transform, transformFilter.getTokenizer());
		assertSame(transformFilter.getPredicate(), sometimesPassing);
	}

}
