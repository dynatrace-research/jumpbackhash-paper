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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class BenchmarkNumBucketsValuesTest {

  @Test
  void testBenchmarkNumBucketsValues() {
    Set<Integer> set = new HashSet<>();

    for (int n = 0; n <= 20; ++n) {
      set.add((int) (Math.pow(2., n)));
      set.add((int) (Math.pow(2., n) + 1));
      set.add((int) (Math.pow(2., n) * (1 + 1 / 4.)));
      set.add((int) (Math.pow(2., n) * (1 + 2 / 4.)));
      set.add((int) (Math.pow(2., n) * (1 + 3 / 4.)));
    }

    List<String> numBucketsValues =
        set.stream().sorted().filter(i -> i <= 1 << 20).map(i -> Integer.toString(i)).toList();

    assertThat(numBucketsValues)
        .isEqualTo(
            Arrays.asList(
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "12", "14", "16", "17", "20",
                "24", "28", "32", "33", "40", "48", "56", "64", "65", "80", "96", "112", "128",
                "129", "160", "192", "224", "256", "257", "320", "384", "448", "512", "513", "640",
                "768", "896", "1024", "1025", "1280", "1536", "1792", "2048", "2049", "2560",
                "3072", "3584", "4096", "4097", "5120", "6144", "7168", "8192", "8193", "10240",
                "12288", "14336", "16384", "16385", "20480", "24576", "28672", "32768", "32769",
                "40960", "49152", "57344", "65536", "65537", "81920", "98304", "114688", "131072",
                "131073", "163840", "196608", "229376", "262144", "262145", "327680", "393216",
                "458752", "524288", "524289", "655360", "786432", "917504", "1048576"));
  }
}
