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

import com.dynatrace.hash4j.hashing.Hashing;
import java.util.random.RandomGenerator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import org.hipparchus.distribution.continuous.UniformRealDistribution;
import org.hipparchus.stat.inference.GTest;
import org.hipparchus.stat.inference.KolmogorovSmirnovTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

interface UniformityTest extends BaseTest {

  int UNIFORMITY_TEST_NUM_CYCLES = 1_000_000;
  double UNIFORMITY_TEST_OVERALL_ALPHA = 0.01;
  double UNIFORMITY_TEST_SMALL_TEST_SPECIFIC_ALPHA =
      calculateTestSpecificAlpha(getUniformityTestSmallNumBuckets());
  double UNIFORMITY_TEST_LARGE_TEST_SPECIFIC_ALPHA =
      calculateTestSpecificAlpha(getUniformityTestLargeNumBuckets());

  static IntStream getUniformityTestSmallNumBuckets() {
    int maxNumBuckets = 1000;
    return IntStream.range(1, maxNumBuckets + 1);
  }

  static IntStream getUniformityTestLargeNumBuckets() {
    return IntStream.of(
        Integer.MAX_VALUE,
        Integer.MAX_VALUE - 1,
        0x40000001, // 2^30 + 1
        0x40000000, // 2^30
        0x3FFFFFFF, // 2^30 - 1
        0x30000000, // 3*2^28
        0x20000001, // 2^29 + 1
        0x20000000, // 2^29
        0x1FFFFFFF, // 2^29 - 1
        0x18000000, // 3*2^27
        0x10000001, // 2^28 + 1
        0x10000000, // 2^28
        0x0FFFFFFF); // 2^28 + 1
  }

  private static double calculateTestSpecificAlpha(IntStream numBucketsStream) {
    double alpha = UNIFORMITY_TEST_OVERALL_ALPHA;
    return -Math.expm1(Math.log1p(-alpha) / numBucketsStream.count());
  }

  private static long calculateSeed(int numBuckets, long seed) {
    return Hashing.komihash5_0().hashStream().putLong(seed).putInt(numBuckets).getAsLong();
  }

  @ParameterizedTest
  @MethodSource("getUniformityTestSmallNumBuckets")
  default void testUniformityWithSmallNumBuckets(int numBuckets) {

    RandomGenerator randomGenerator =
        RANDOM_FACTORY.create(calculateSeed(numBuckets, 0xd12e813698df9fd3L));

    long[] counts = new long[numBuckets];
    double[] expected = DoubleStream.generate(() -> 1.).limit(numBuckets).toArray();

    for (int i = 0; i < UNIFORMITY_TEST_NUM_CYCLES; ++i) {
      long hashedKey = randomGenerator.nextLong();
      int bucketIdx = mapKeyToBucketIndex(hashedKey, numBuckets);
      counts[bucketIdx] += 1;
    }

    if (numBuckets >= 2) {
      double pValue = new GTest().gTest(expected, counts);
      assertThat(pValue).isGreaterThan(UNIFORMITY_TEST_SMALL_TEST_SPECIFIC_ALPHA);
    }
  }

  @ParameterizedTest
  @MethodSource("getUniformityTestLargeNumBuckets")
  default void testUniformityWithLargeNumBuckets(int numBuckets) {

    RandomGenerator randomGenerator =
        RANDOM_FACTORY.create(calculateSeed(numBuckets, 0xd456be3a53643f5eL));

    double[] bucketIndices = new double[UNIFORMITY_TEST_NUM_CYCLES];

    for (int i = 0; i < UNIFORMITY_TEST_NUM_CYCLES; ++i) {
      long hashedKey = randomGenerator.nextLong();
      bucketIndices[i] = mapKeyToBucketIndex(hashedKey, numBuckets);
    }

    double pValue =
        new KolmogorovSmirnovTest()
            .kolmogorovSmirnovTest(new UniformRealDistribution(0., numBuckets), bucketIndices);
    assertThat(pValue).isGreaterThan(UNIFORMITY_TEST_LARGE_TEST_SPECIFIC_ALPHA);
  }
}
