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

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.IntStream;

public class RandomValueConsumptionSimulation {

  public static int[] getNumberOfBucketValues(int min, int max, double decrementFactor) {
    List<Integer> distinctCounts = new ArrayList<>();
    for (int c = max; c >= min; c = Math.min(c - 1, (int) (c * decrementFactor))) {
      distinctCounts.add(c);
    }
    Collections.reverse(distinctCounts);
    return distinctCounts.stream().mapToInt(Integer::valueOf).toArray();
  }

  private static final class CountingPseudoRandomGenerator implements PseudoRandomGenerator {

    private final PseudoRandomGenerator pseudoRandomGenerator;
    private long count = 0;

    private CountingPseudoRandomGenerator(PseudoRandomGenerator pseudoRandomGenerator) {
      this.pseudoRandomGenerator = pseudoRandomGenerator;
    }

    public long getCount() {
      return count;
    }

    @Override
    public long nextLong() {
      count += 1;
      return pseudoRandomGenerator.nextLong();
    }

    @Override
    public void resetWithSeed(long seed) {
      count = 0;
      pseudoRandomGenerator.resetWithSeed(seed);
    }
  }

  private static final class Config {

    private final String label;

    private final Function<PseudoRandomGenerator, BucketMapper> bucketMapperSupplier;

    public Config(
        String label, Function<PseudoRandomGenerator, BucketMapper> bucketMapperSupplier) {
      this.label = label;
      this.bucketMapperSupplier = bucketMapperSupplier;
    }

    String getLabel() {
      return label;
    }
  }

  public static void main(String[] args) {

    SplittableRandom random = new SplittableRandom(0xa6b5ec7a57a6d38eL);

    int[] numBucketValues = getNumberOfBucketValues(1, 1_000_000, 0.999);
    long[] seedValues = random.longs(numBucketValues.length).toArray();

    List<Config> configs =
        List.of(
            new Config("JumpHash", JumpHash::new),
            new Config("JumpBackHash", JumpBackHash::new),
            new Config("JumpBackHash32", JumpBackHash32::new));

    double[][] mean = new double[configs.size()][];
    double[][] variance = new double[configs.size()][];
    for (int i = 0; i < configs.size(); ++i) {
      mean[i] = new double[numBucketValues.length];
      variance[i] = new double[numBucketValues.length];
    }

    final int numIterations = 10_000_000;

    try {
      ForkJoinPool forkJoinPool = new ForkJoinPool();
      forkJoinPool
          .submit(
              () ->
                  IntStream.range(0, numBucketValues.length)
                      .parallel()
                      .forEach(
                          i -> {
                            int numBuckets = numBucketValues[i];
                            SplittableRandom rng = new SplittableRandom(seedValues[i]);

                            List<CountingPseudoRandomGenerator> countingPseudoRandomGenerators =
                                configs.stream()
                                    .map(x -> new CountingPseudoRandomGenerator(new SplitMix64V1()))
                                    .toList();
                            List<BucketMapper> bucketMapper = new ArrayList<>(configs.size());

                            for (int j = 0; j < configs.size(); ++j) {
                              bucketMapper.add(
                                  configs
                                      .get(j)
                                      .bucketMapperSupplier
                                      .apply(countingPseudoRandomGenerators.get(j)));
                            }

                            long[] count = new long[configs.size()];
                            long[] countSquared = new long[configs.size()];

                            for (int k = 0; k < numIterations; ++k) {
                              long key = rng.nextLong();
                              for (int j = 0; j < configs.size(); ++j) {
                                bucketMapper.get(j).getBucket(key, numBuckets);
                                long c = countingPseudoRandomGenerators.get(j).getCount();
                                count[j] += c;
                                countSquared[j] += c * c;
                              }
                            }

                            for (int j = 0; j < configs.size(); ++j) {
                              double m = count[j] / (double) numIterations;
                              mean[j][i] = m;
                              variance[j][i] = countSquared[j] / (double) numIterations - m * m;
                            }
                          }))
          .get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }

    try (FileWriter o =
        new FileWriter(
            "../results/random_value_consumption_simulation.csv", StandardCharsets.UTF_8)) {

      o.write("number of iterations = " + numIterations + '\n');
      o.write("number of buckets");
      for (int j = 0; j < configs.size(); ++j) {
        o.write("; mean" + configs.get(j).getLabel());
        o.write("; variance" + configs.get(j).getLabel());
      }

      o.write('\n');
      for (int i = 0; i < numBucketValues.length; ++i) {
        o.write(Integer.toString(numBucketValues[i]));
        for (int j = 0; j < configs.size(); ++j) {
          o.write("; " + mean[j][i]);
          o.write("; " + variance[j][i]);
        }
        o.write('\n');
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
