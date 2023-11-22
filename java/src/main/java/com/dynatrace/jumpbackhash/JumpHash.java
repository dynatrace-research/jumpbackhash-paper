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

import static java.util.Objects.requireNonNull;

class JumpHash implements BucketMapper {

  private final PseudoRandomGenerator randomGenerator;

  JumpHash(PseudoRandomGenerator pseudoRandomGenerator) {
    this.randomGenerator = requireNonNull(pseudoRandomGenerator);
  }

  // based on Google's Guava implementation
  // see
  // https://github.com/google/guava/blob/0a17f4a429323589396c38d8ce75ca058faa6c64/guava/src/com/google/common/hash/Hashing.java#L559
  @Override
  public int getBucket(long k, int n) {
    randomGenerator.resetWithSeed(k);
    int b = -1;
    int bPrime = 0;
    while (bPrime < n) {
      b = bPrime;
      bPrime = (int) ((b + 1) / randomGenerator.nextDouble());
    }
    return b;
  }
}
