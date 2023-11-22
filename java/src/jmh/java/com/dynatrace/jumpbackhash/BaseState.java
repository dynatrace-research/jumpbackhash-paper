//
// Copyright (c) 2024 Dynatrace LLC. All rights reserved.
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

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import org.openjdk.jmh.annotations.*;

@State(Scope.Thread)
public abstract class BaseState {

  private static final int NUM_KEYS_EXPONENT = 12;
  private static final int NUM_KEYS = 1 << NUM_KEYS_EXPONENT;
  private static final int NUM_KEYS_MASK = NUM_KEYS - 1;
  private static final RandomGeneratorFactory<?> RANDOM_FACTORY =
      RandomGeneratorFactory.of("L32X64MixRandom");

  @Param({
    "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "12", "14", "16", "17", "20", "24", "28",
    "32", "33", "40", "48", "56", "64", "65", "80", "96", "112", "128", "129", "160", "192", "224",
    "256", "257", "320", "384", "448", "512", "513", "640", "768", "896", "1024", "1025", "1280",
    "1536", "1792", "2048", "2049", "2560", "3072", "3584", "4096", "4097", "5120", "6144", "7168",
    "8192", "8193", "10240", "12288", "14336", "16384", "16385", "20480", "24576", "28672", "32768",
    "32769", "40960", "49152", "57344", "65536", "65537", "81920", "98304", "114688", "131072",
    "131073", "163840", "196608", "229376", "262144", "262145", "327680", "393216", "458752",
    "524288", "524289", "655360", "786432", "917504", "1048576"
  })
  int numBuckets;

  private long[] keys = new long[NUM_KEYS];
  private int keyIndex;

  @Setup(Level.Iteration)
  public void setup() {
    RandomGenerator randomGenerator = RANDOM_FACTORY.create();
    for (int i = 0; i < NUM_KEYS; ++i) {
      keys[i] = randomGenerator.nextLong();
    }
    keyIndex = 0;
    initializeMapper();
  }

  long getKey() {
    return keys[NUM_KEYS_MASK & keyIndex++];
  }

  int hash() {
    return hash(getKey(), numBuckets);
  }

  abstract int hash(long key, int numBuckets);

  abstract void initializeMapper();
}
