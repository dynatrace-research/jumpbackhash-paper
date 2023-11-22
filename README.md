# JumpBackHash: Say Goodbye to the Modulo Operation to Distribute Keys Uniformly to Buckets

This repository contains the source code to reproduce the results and figures presented in the paper ["JumpBackHash: Say Goodbye to the Modulo Operation to Distribute Keys Uniformly to Buckets"](TODO).

## Abstract
The distribution of keys to a given number of buckets is a fundamental task in distributed data processing and storage. A simple, fast, and therefore popular approach is to map the hash values of keys to buckets based on the remainder after dividing by the number of buckets. Unfortunately, these mappings are not stable when the number of buckets changes, which can lead to severe spikes in system resource utilization, such as network or database requests. Consistent hash algorithms can minimize remappings, but are either significantly slower than the modulo-based approach, require floating-point arithmetic, or are based on a family of hash functions rarely available in standard libraries. This paper introduces JumpBackHash, which uses only integer arithmetic and a standard pseudorandom generator. Due to its speed and simple implementation, it can safely replace the modulo-based approach to improve assignment and system stability. A production-ready Java implementation of JumpBackHash has been released as part of the Hash4j open source library.

## Steps to reproduce the results and figures presented in the paper
1. Create an Amazon EC2 [c5.metal](https://aws.amazon.com/ec2/instance-types/c5/) instance with Ubuntu Server 22.04 LTS and 20GiB of storage.
2. Clone the repository:
   ```
   git clone https://github.com/dynatrace-research/jumpbackhash-paper.git && cd jumpbackhash-paper
   ```
3. Install all required packages:
   ```
   sudo apt update && sudo NEEDRESTART_MODE=a apt --yes install openjdk-21-jdk python-is-python3 python3-pip texlive texlive-latex-extra texlive-fonts-extra texlive-science cm-super && pip install -r python/requirements.txt
   ```
4. To reproduce the random value consumption results `results/random_value_consumption_simulation.csv` run the `runRandomValueConsumptionSimulation` task (takes ~3min):
   ```
   ./gradlew runRandomValueConsumptionSimulation
   ```
5. To reproduce the performance benchmark results `results/benchmark-results.json` disable Turbo Boost ([set P-state to 1](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/processor_state_control.html)), run the `runBenchmarks` task (takes ~2h), and enable Turbo Boost again:
   ```
   sudo sh -c "echo 1 > /sys/devices/system/cpu/intel_pstate/no_turbo"; ./gradlew runBenchmarks; sudo sh -c "echo 0 > /sys/devices/system/cpu/intel_pstate/no_turbo"
   ```
6. To (re-)generate all figures in the `paper` directory execute the `pdfFigures` task (takes ~1min):
   ```
   ./gradlew pdfFigures
   ```
   This task also evaluates the maximum absolute deviations from the predicted expectation and variance of the number of consumed random values. The results can be found in `results/time-complexity-errors.txt`.
7. To run all unit tests including tests for monotonicity and uniformity execute the `test` task (takes ~5min):
   ```
   ./gradlew test
   ```
 