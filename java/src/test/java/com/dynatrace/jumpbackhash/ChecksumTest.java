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

import com.dynatrace.hash4j.hashing.HashStream64;
import com.dynatrace.hash4j.hashing.Hashing;
import java.util.SplittableRandom;
import org.junit.jupiter.api.Test;

interface ChecksumTest extends BaseTest {

  long getCheckSum();

  @Test
  default void testCheckSum() {
    int numIterations = 1_000_000;
    SplittableRandom random = new SplittableRandom(0x0a55871a9d9103b7L);
    HashStream64 checkSumHashStream = Hashing.komihash5_0().hashStream();
    for (int i = 0; i < numIterations; ++i) {
      int numBuckets = Math.max(1, random.nextInt() >>> 1 >>> random.nextInt());
      long hash = random.nextLong();
      int bucketIdx = mapKeyToBucketIndex(hash, numBuckets);
      checkSumHashStream.putInt(bucketIdx);
    }
    assertThat(checkSumHashStream.getAsLong()).isEqualTo(getCheckSum());
  }
}
