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

/** A hash function that maps a given hash consistently to a bucket index of given range. */
public interface BucketMapper {

  /**
   * Returns a bucket index in the range {@code [0, numBuckets)} based on the given hash value.
   *
   * <p>The returned bucket index is uniformly distributed. If {@code numBuckets} is changed,
   * remapping to other bucket indices is minimized.
   *
   * <p>For more details see Lamping, John, and Eric Veach. "A fast, minimal memory, consistent hash
   * algorithm." arXiv preprint <a href="https://arxiv.org/abs/1406.2294">arXiv:1406.2294</a>
   * (2014).
   *
   * @param hash a 64-bit hash value
   * @param numBuckets the number of buckets, must be positive
   * @return the bucket index
   */
  int getBucket(long hash, int numBuckets);
}
