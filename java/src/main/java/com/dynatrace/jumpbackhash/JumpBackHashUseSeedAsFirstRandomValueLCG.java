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

/**
 * This is an implementation of JumpBackHash that uses the 64-bit hash key directly as random value.
 * Further random bits are generated using a 64-bit linear congruential generator (LCG). As it is
 * known that the lower bits have poor statistical quality, only the upper 32 bits are used. The
 * outer loop of JumpBackHash has been unrolled, as it is known that there are at most 2 iterations.
 */
public class JumpBackHashUseSeedAsFirstRandomValueLCG implements BucketMapper {

  // constant for LCG taken from
  // L’Ecuyer, Pierre. "Tables of linear congruential generators of different sizes and good lattice
  // structure." Mathematics of Computation 68.225 (1999): 249-260.
  private static final long MULTIPLIER = 3935559000370003845L;
  private static final long INCREMENT = 1L;

  @Override
  public int getBucket(long k, int n) {
    if (n <= 1) return 0;
    long v = k; // use key as 64-bit random value
    int u = (int) (k ^ (k >>> 32)) & (~0 >>> numberOfLeadingZeros(n - 1));
    if (u == 0) return 0;
    int q = 1 << ~numberOfLeadingZeros(u); // q = 2^m
    int b = q + ((int) (v >>> (bitCount(u) << 5)) & (q - 1));
    if (b < n) return b;
    while (true) {
      k = k * MULTIPLIER + INCREMENT; // LCG
      b = (int) (k >>> 32) & ((q << 1) - 1); // use only upper 32-bits of LCG
      if (b < q) break;
      if (b < n) return b;
    }
    u ^= q;
    if (u == 0) return 0;
    q = 1 << ~numberOfLeadingZeros(u); // q = 2^m
    b = q + ((int) (v >>> (bitCount(u) << 5)) & (q - 1));
    return b;
  }
}
