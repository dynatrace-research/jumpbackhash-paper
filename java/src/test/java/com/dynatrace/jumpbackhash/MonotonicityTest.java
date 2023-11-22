//
// Copyright (c) 2023-2024 Dynatrace LLC. All rights reserved.
//
// This software and associated documentation files (the "Software")
// are being made available by Dynatrace LLC for the sole purpose of
// illustrating the implementation of certain algorithms which have
// been published by Dynatrace LLC. Permission is hereby granted,
// free of charge, to any person obtaining a copy of the Software,
// to view and use the Software for internal, non-production,
// non-commercial purposes only – the Software may not be used to
// process live data or distributed, sublicensed, modified and/or
// sold either alone or as part of or in combination with any other
// software.
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
// OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
// HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// DEALINGS IN THE SOFTWARE.
//
package com.dynatrace.jumpbackhash;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.random.RandomGenerator;
import org.junit.jupiter.api.Test;

interface MonotonicityTest extends BaseTest {

  int MONOTONICITY_TEST_NUM_CYCLES = 10_000;
  int MONOTONICITY_TEST_MAX_NUM_BUCKETS = 10_000;

  @Test
  default void testMonotonicity() {

    RandomGenerator randomGenerator = RANDOM_FACTORY.create(0x3df6dcebff42e20dL);

    for (int i = 0; i < MONOTONICITY_TEST_NUM_CYCLES; ++i) {
      long hashedKey = randomGenerator.nextLong();
      assertThat(mapKeyToBucketIndex(hashedKey, 1)).isZero();
      int oldBucketIdx = 0;
      for (int numBuckets = 1; numBuckets < MONOTONICITY_TEST_MAX_NUM_BUCKETS; ++numBuckets) {
        int newBucketIdx = mapKeyToBucketIndex(hashedKey, numBuckets);
        if (oldBucketIdx != newBucketIdx) {
          assertThat(newBucketIdx).isEqualTo(numBuckets - 1);
          oldBucketIdx = newBucketIdx;
        }
      }
    }
  }
}
