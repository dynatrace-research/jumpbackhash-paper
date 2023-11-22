//
// Copyright (c) 2022-2024 Dynatrace LLC. All rights reserved.
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

import static java.lang.Math.log1p;

/** A pseudo-random generator. */
public interface PseudoRandomGenerator {

  /**
   * Returns a random uniformly distributed 64-bit {@code long} value.
   *
   * @return a random value
   */
  long nextLong();

  /**
   * Returns a random uniformly distributed 32-bit {@code int} value.
   *
   * @return a random value
   */
  default int nextInt() {
    return (int) nextLong();
  }

  /**
   * Returns a random {@code double} value that is uniformly distributed over [0,1).
   *
   * @return a random value
   */
  default double nextDouble() {
    return (nextLong() >>> 11) * 0x1.0p-53;
  }

  /**
   * Returns a random {@code double} value that is exponentially distributed with rate parameter 1.
   *
   * @return a random value
   */
  default double nextExp() {
    return -log1p(-nextDouble());
  }

  // see algorithm 5 with L=32 in Lemire, Daniel. "Fast random integer generation in an interval."
  // ACM Transactions on Modeling and Computer Simulation (TOMACS) 29.1 (2019): 1-12.
  default int uniformInt(int exclusiveUpperBound) {
    long s = exclusiveUpperBound;
    long r = nextLong();
    long x = r & 0xFFFFFFFFL;
    long m = x * s; // is always positive as 0 <= s < 2^31 and 0 <= x < 2^32 => 0 <= m < 2^63
    long l = m & 0xFFFFFFFFL;
    if (l < s) {
      long t = 0x100000000L % s;
      while (l < t) {
        x = (r >>> 32) & 0xFFFFFFFFL;
        m = x * s; // is always positive as 0 <= s < 2^31 and 0 <= x < 2^32 => 0 <= m < 2^63
        l = m & 0xFFFFFFFFL;
        if (l >= t) break;
        r = nextLong();
        x = (r >>> 32) & 0xFFFFFFFFL;
        m = x * s; // is always positive as 0 <= s < 2^31 and 0 <= x < 2^32 => 0 <= m < 2^63
        l = m & 0xFFFFFFFFL;
      }
    }
    return (int) (m >>> 32);
  }

  /**
   * Resets the pseudo-random generator using the given 64-bit seed value.
   *
   * @param seed the seed value
   */
  void resetWithSeed(long seed);
}
