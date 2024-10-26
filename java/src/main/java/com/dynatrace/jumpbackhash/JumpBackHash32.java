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
import static java.lang.Integer.numberOfLeadingZeros;
import static java.util.Objects.requireNonNull;

public class JumpBackHash32 implements BucketMapper {

  private final PseudoRandomGenerator randomGenerator;

  public JumpBackHash32(PseudoRandomGenerator pseudoRandomGenerator) {
    this.randomGenerator = requireNonNull(pseudoRandomGenerator);
  }

  @Override
  public int getBucket(long k, int n) {
    if (n <= 1) return 0;
    randomGenerator.resetWithSeed(k);
    long v = (randomGenerator.nextInt() & 0xFFFFFFFFL) | ((long) randomGenerator.nextInt() << 32);
    int u = (int) (v ^ (v >>> 32)) & (~0 >>> numberOfLeadingZeros(n - 1));
    while (u != 0) {
      int q = 1 << ~numberOfLeadingZeros(u); // q = 2^m
      int b = q + ((int) (v >>> (bitCount(u) << 5)) & (q - 1));
      while (true) {
        if (b < n) return b;
        int w = randomGenerator.nextInt();
        b = w & ((q << 1) - 1);
        if (b < q) break;
      }
      u ^= q;
    }
    return 0;
  }
}
