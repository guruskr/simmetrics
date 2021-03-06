/*-
 * #%L
 * Simmetrics - Core
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

import static java.util.Objects.requireNonNull;
import static com.github.mpkorstanje.simmetrics.simplifiers.Simplifiers.chain;

import java.util.List;
import java.util.Set;

import com.github.mpkorstanje.simmetrics.Distance;
import com.github.mpkorstanje.simmetrics.StringDistance;
import com.github.mpkorstanje.simmetrics.simplifiers.Simplifier;
import com.github.mpkorstanje.simmetrics.tokenizers.Tokenizer;

import com.google.common.collect.Multiset;

final class StringDistances {

	public static StringDistance create(Distance<String> distance) {
		if (distance instanceof StringDistance) {
			return (StringDistance) distance;
		}

		return new ForString(distance);
	}

	public static StringDistance create(Distance<String> distance, Simplifier simplifier) {
		if (distance instanceof ForString) {
			ForString forString = (ForString) distance;
			return new ForStringWithSimplifier(forString.getDistance(), simplifier);
		} else if (distance instanceof ForStringWithSimplifier) {
			ForStringWithSimplifier fsws = (ForStringWithSimplifier) distance;
			return new ForStringWithSimplifier(fsws.getDistance(), chain(simplifier, fsws.getSimplifier()));
		} else if (distance instanceof ForList) {
			ForList fl = (ForList) distance;
			return createForListDistance(fl.getDistance(), simplifier, fl.getTokenizer());
		} else if (distance instanceof ForListWithSimplifier) {
			ForListWithSimplifier fl = (ForListWithSimplifier) distance;
			return createForListDistance(fl.getDistance(), chain(simplifier, fl.getSimplifier()), fl.getTokenizer());
		} else if (distance instanceof ForSet) {
			ForSet fl = (ForSet) distance;
			return createForSetDistance(fl.getDistance(), simplifier, fl.getTokenizer());
		} else if (distance instanceof ForSetWithSimplifier) {
			ForSetWithSimplifier fl = (ForSetWithSimplifier) distance;
			return createForSetDistance(fl.getDistance(), chain(simplifier, fl.getSimplifier()), fl.getTokenizer());
		}

		return new ForStringWithSimplifier(distance, simplifier);
	}

	public static StringDistance createForListDistance(Distance<List<String>> distance, Simplifier simplifier,
			Tokenizer tokenizer) {
		return new ForListWithSimplifier(distance, simplifier, tokenizer);
	}

	public static StringDistance createForListDistance(Distance<List<String>> distance, Tokenizer tokenizer) {
		return new ForList(distance, tokenizer);
	}

	public static StringDistance createForSetDistance(Distance<Set<String>> distance, Simplifier simplifier,
			Tokenizer tokenizer) {
		return new ForSetWithSimplifier(distance, simplifier, tokenizer);
	}

	public static StringDistance createForSetDistance(Distance<Set<String>> distance, Tokenizer tokenizer) {
		return new ForSet(distance, tokenizer);
	}

	public static StringDistance createForMultisetDistance(Distance<Multiset<String>> distance, Simplifier simplifier,
			Tokenizer tokenizer) {
		return new ForMultisetWithSimplifier(distance, simplifier, tokenizer);
	}

	public static StringDistance createForMultisetDistance(Distance<Multiset<String>> distance, Tokenizer tokenizer) {
		return new ForMultiset(distance, tokenizer);
	}
	
	static final class ForList implements StringDistance {
		private final Distance<List<String>> distance;
		private final Tokenizer tokenizer;

		ForList(Distance<List<String>> distance, Tokenizer tokenizer) {

			requireNonNull(distance);
			requireNonNull(tokenizer);

			this.distance = distance;
			this.tokenizer = tokenizer;
		}

		@Override
		public float distance(String a, String b) {
			return distance.distance(tokenizer.tokenizeToList(a), tokenizer.tokenizeToList(b));
		}

		Distance<List<String>> getDistance() {
			return distance;
		}

		Tokenizer getTokenizer() {
			return tokenizer;
		}

		@Override
		public String toString() {
			return distance + " [" + tokenizer + "]";
		}
	}

	static final class ForListWithSimplifier implements StringDistance {
		private final Distance<List<String>> distance;
		private final Simplifier simplifier;
		private final Tokenizer tokenizer;

		ForListWithSimplifier(Distance<List<String>> distance, Simplifier simplifier, Tokenizer tokenizer) {

			requireNonNull(distance);
			requireNonNull(simplifier);
			requireNonNull(tokenizer);

			this.distance = distance;
			this.simplifier = simplifier;
			this.tokenizer = tokenizer;
		}

		@Override
		public float distance(String a, String b) {
			return distance.distance(tokenizer.tokenizeToList(simplifier.simplify(a)),
					tokenizer.tokenizeToList(simplifier.simplify(b)));
		}

		Distance<List<String>> getDistance() {
			return distance;
		}

		Simplifier getSimplifier() {
			return simplifier;
		}

		Tokenizer getTokenizer() {
			return tokenizer;
		}

		@Override
		public String toString() {
			return distance + " [" + simplifier + " -> " + tokenizer + "]";
		}
	}

	static final class ForSet implements StringDistance {

		private final Distance<Set<String>> distance;
		private final Tokenizer tokenizer;

		ForSet(Distance<Set<String>> distance, Tokenizer tokenizer) {
			requireNonNull(distance);
			requireNonNull(tokenizer);

			this.distance = distance;
			this.tokenizer = tokenizer;
		}

		@Override
		public float distance(String a, String b) {
			return distance.distance(tokenizer.tokenizeToSet(a), tokenizer.tokenizeToSet(b));
		}

		Distance<Set<String>> getDistance() {
			return distance;
		}

		Tokenizer getTokenizer() {
			return tokenizer;
		}

		@Override
		public String toString() {
			return distance + " [" + tokenizer + "]";
		}

	}

	static final class ForSetWithSimplifier implements StringDistance {

		private final Distance<Set<String>> distance;
		private final Simplifier simplifier;
		private final Tokenizer tokenizer;

		ForSetWithSimplifier(Distance<Set<String>> distance, Simplifier simplifier, Tokenizer tokenizer) {
			requireNonNull(distance);
			requireNonNull(simplifier);
			requireNonNull(tokenizer);

			this.distance = distance;
			this.simplifier = simplifier;
			this.tokenizer = tokenizer;
		}

		@Override
		public float distance(String a, String b) {
			return distance.distance(tokenizer.tokenizeToSet(simplifier.simplify(a)),
					tokenizer.tokenizeToSet(simplifier.simplify(b)));
		}

		Distance<Set<String>> getDistance() {
			return distance;
		}

		Simplifier getSimplifier() {
			return simplifier;
		}

		Tokenizer getTokenizer() {
			return tokenizer;
		}

		@Override
		public String toString() {
			return distance + " [" + simplifier + " -> " + tokenizer + "]";
		}

	}
	static final class ForMultiset implements StringDistance {

		private final Distance<Multiset<String>> distance;
		private final Tokenizer tokenizer;

		ForMultiset(Distance<Multiset<String>> distance, Tokenizer tokenizer) {
			requireNonNull(distance);
			requireNonNull(tokenizer);

			this.distance = distance;
			this.tokenizer = tokenizer;
		}

		@Override
		public float distance(String a, String b) {
			return distance.distance(tokenizer.tokenizeToMultiset(a), tokenizer.tokenizeToMultiset(b));
		}

		Distance<Multiset<String>> getDistance() {
			return distance;
		}

		Tokenizer getTokenizer() {
			return tokenizer;
		}

		@Override
		public String toString() {
			return distance + " [" + tokenizer + "]";
		}

	}

	static final class ForMultisetWithSimplifier implements StringDistance {

		private final Distance<Multiset<String>> distance;
		private final Simplifier simplifier;
		private final Tokenizer tokenizer;

		ForMultisetWithSimplifier(Distance<Multiset<String>> distance, Simplifier simplifier, Tokenizer tokenizer) {
			requireNonNull(distance);
			requireNonNull(simplifier);
			requireNonNull(tokenizer);

			this.distance = distance;
			this.simplifier = simplifier;
			this.tokenizer = tokenizer;
		}

		@Override
		public float distance(String a, String b) {
			return distance.distance(tokenizer.tokenizeToMultiset(simplifier.simplify(a)),
					tokenizer.tokenizeToMultiset(simplifier.simplify(b)));
		}

		Distance<Multiset<String>> getDistance() {
			return distance;
		}

		Simplifier getSimplifier() {
			return simplifier;
		}

		Tokenizer getTokenizer() {
			return tokenizer;
		}

		@Override
		public String toString() {
			return distance + " [" + simplifier + " -> " + tokenizer + "]";
		}

	}
	static final class ForString implements StringDistance {
		private final Distance<String> distance;

		ForString(Distance<String> distance) {
			this.distance = distance;
		}

		@Override
		public float distance(String a, String b) {
			return distance.distance(a, b);
		}

		@Override
		public String toString() {
			return distance.toString();
		}

		Distance<String> getDistance() {
			return distance;
		}

	}

	static final class ForStringWithSimplifier implements StringDistance {

		private final Distance<String> distance;

		private final Simplifier simplifier;

		ForStringWithSimplifier(Distance<String> distance, Simplifier simplifier) {
			requireNonNull(distance);
			requireNonNull(simplifier);

			this.distance = distance;
			this.simplifier = simplifier;
		}

		@Override
		public float distance(String a, String b) {
			return distance.distance(simplifier.simplify(a), simplifier.simplify(b));
		}

		Distance<String> getDistance() {
			return distance;
		}

		Simplifier getSimplifier() {
			return simplifier;
		}

		@Override
		public String toString() {
			return distance + " [" + simplifier + "]";
		}

	}

	private StringDistances() {
		// Utility class.
	}

}
