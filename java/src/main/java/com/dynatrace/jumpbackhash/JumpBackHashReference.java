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

import static java.lang.Integer.bitCount;
import static java.util.Objects.requireNonNull;

/** This is a non-optimized reference implementation of JumpBackHash as described in the paper. */
public class JumpBackHashReference implements BucketMapper {

  private final PseudoRandomGenerator randomGenerator;

  JumpBackHashReference(PseudoRandomGenerator pseudoRandomGenerator) {
    this.randomGenerator = requireNonNull(pseudoRandomGenerator);
  }

  private static int floorLog2(int x) {
    return (int) (Math.log(x) / Math.log(2.));
    // same as return 31 - Integer.numberOfLeadingZeros(x);
  }

  private static int pow2(int exponent) {
    return 1 << exponent;
  }

  private static int modPow2(int x, int exponent) {
    return x & (pow2(exponent) - 1);
  }

  @Override
  public int getBucket(long k, int n) {
    if (n <= 1) return 0;
    randomGenerator.resetWithSeed(k);
    int V0 = randomGenerator.nextInt();
    int V1 = randomGenerator.nextInt();
    int U = V0 ^ V1;
    int m = floorLog2(n - 1) + 1;
    int u = modPow2(U, m);
    while (u != 0) {
      m = floorLog2(u);
      int s = bitCount(u) % 2;
      int b = pow2(m) + modPow2((s == 0) ? V0 : V1, m);
      while (true) {
        if (b < n) return b;
        int wv = randomGenerator.nextInt();
        b = modPow2(wv, m + 1);
        if (b < pow2(m)) break;
      }
      u = u ^ pow2(m);
    }
    return 0;
  }
}
