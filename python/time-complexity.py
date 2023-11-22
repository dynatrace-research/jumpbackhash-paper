#
# Copyright (c) 2024 Dynatrace LLC. All rights reserved.
#
# This software and associated documentation files (the "Software")
# are being made available by Dynatrace LLC for the sole purpose of
# illustrating the implementation of certain algorithms which have
# been published by Dynatrace LLC. Permission is hereby granted,
# free of charge, to any person obtaining a copy of the Software,
# to view and use the Software for internal, non-production,
# non-commercial purposes only – the Software may not be used to
# process live data or distributed, sublicensed, modified and/or
# sold either alone or as part of or in combination with any other
# software.
#
# The above copyright notice and this permission notice shall be
# included in all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
# EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
# OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
# NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
# HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
# WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
# DEALINGS IN THE SOFTWARE.
#
import preamble
import csv
import matplotlib.pyplot as plt
from math import log, floor, sqrt


def theoretical_mean_jumphash(num_bucket_list):
    result = []
    current_num_buckets = 0
    sum = 0
    for num_buckets in num_bucket_list:
        while current_num_buckets < num_buckets:
            current_num_buckets += 1
            sum += 1 / current_num_buckets
        result.append(sum)
    return result


def theoretical_variance_jumphash(num_bucket_list):
    result = []
    current_num_buckets = 0
    sum = 0
    for num_buckets in num_bucket_list:
        while current_num_buckets < num_buckets:
            current_num_buckets += 1
            sum += 1 / current_num_buckets * (1 - 1 / current_num_buckets)
        result.append(sum)
    return result


def calculate_alpha(n):
    return pow(2.0, floor(log(n - 1, 2)) + 1) / n


def theoretical_mean_jumpbackhash32(num_bucket_list):
    return [1 + calculate_alpha(n) if n > 1 else 0 for n in num_bucket_list]


def calculate_variance_jumpbackhash32(n):
    if n <= 1:
        return 0
    a = calculate_alpha(n)
    return a * (a - 1)


def theoretical_variance_jumpbackhash32(num_bucket_list):
    return [calculate_variance_jumpbackhash32(n) for n in num_bucket_list]


def theoretical_mean_jumpbackhash(num_bucket_list):
    return [
        (
            1
            + (calculate_alpha(n) - 1)
            * calculate_alpha(n)
            / (2 * calculate_alpha(n) - 1)
            if n > 1
            else 0
        )
        for n in num_bucket_list
    ]


def calculate_variance_jumpbackhash(n):
    if n <= 1:
        return 0
    a = calculate_alpha(n)
    return a * (a - 1) * (a**2 - a + 1) / (2 * a - 1) ** 2


def theoretical_variance_jumpbackhash(num_bucket_list):
    return [calculate_variance_jumpbackhash(n) for n in num_bucket_list]


def read_data(data_file):
    info = {}

    with open(data_file, "r") as file:
        reader = csv.reader(file, skipinitialspace=True, delimiter=";")
        row_counter = 0
        headers = []
        values = []
        for r in reader:
            if row_counter == 0:
                for i in r:
                    if i != "":
                        d = i.split("=")
                        info[d[0].strip()] = d[1].strip()

            elif row_counter == 1:
                for i in r:
                    if i != "":
                        headers.append(i.strip())
                        values.append([])
            elif row_counter >= 2:
                k = 0
                for i in r:
                    if i != "":
                        values[k].append(float(i))
                        k += 1
            row_counter += 1

    data = {h: v for h, v in zip(headers, values)}
    size = row_counter - 2
    return info, data, size


def max_relative_error(experiment, theory):
    assert len(experiment) == len(theory)
    size = min(len(experiment), len(theory))
    m = 0
    for i in range(size):
        assert theory[i] < float("inf")
        diff = abs(experiment[i] - theory[i])
        if diff > 0:
            m = max(m, diff)
    return m


data = read_data("results/random_value_consumption_simulation.csv")
values = data[1]

fig, axs = plt.subplots(2, 1, sharex=True)
fig.set_size_inches(5, 6)

num_buckets = values["number of buckets"]
jump_back_hash_32_empirical_mean = values["meanJumpBackHash32"]
jump_back_hash_32_empirical_variance = values["varianceJumpBackHash32"]
jump_back_hash_32_theory_mean = theoretical_mean_jumpbackhash32(num_buckets)
jump_back_hash_32_theory_variance = theoretical_variance_jumpbackhash32(num_buckets)

jump_back_hash_empirical_mean = values["meanJumpBackHash"]
jump_back_hash_empirical_variance = values["varianceJumpBackHash"]
jump_back_hash_theory_mean = theoretical_mean_jumpbackhash(num_buckets)
jump_back_hash_theory_variance = theoretical_variance_jumpbackhash(num_buckets)

jump_hash_empirical_mean = values["meanJumpHash"]
jump_hash_empirical_variance = values["varianceJumpHash"]
jump_hash_theory_mean = theoretical_mean_jumphash(num_buckets)
jump_hash_theory_variance = theoretical_variance_jumphash(num_buckets)

axs[0].set_xscale("log", base=10)
axs[0].set_xlim([1, 1e6])
axs[0].grid()
axs[1].grid()

axs[0].plot(
    num_buckets,
    jump_hash_empirical_mean,
    label="JumpHash",
    color=preamble.colors["JH"],
    linestyle=preamble.linestyles["JH"],
)
axs[0].plot(
    num_buckets,
    jump_back_hash_32_empirical_mean,
    label="JumpBackHash",
    color=preamble.colors["JBH"],
    linestyle=preamble.linestyles["JBH"],
)
axs[0].plot(
    num_buckets,
    jump_back_hash_empirical_mean,
    label=r"JumpBackHash*",
    color=preamble.colors["JBH*"],
    linestyle=preamble.linestyles["JBH*"],
)
axs[1].plot(
    num_buckets,
    jump_hash_empirical_variance,
    color=preamble.colors["JH"],
    linestyle=preamble.linestyles["JH"],
)
axs[1].plot(
    num_buckets,
    jump_back_hash_32_empirical_variance,
    color=preamble.colors["JBH"],
    linestyle=preamble.linestyles["JBH"],
)
axs[1].plot(
    num_buckets,
    jump_back_hash_empirical_variance,
    color=preamble.colors["JBH*"],
    linestyle=preamble.linestyles["JBH*"],
)

axs[0].set_ylabel(r"mean")
axs[1].set_ylabel(r"variance")
axs[1].set_xlabel(r"number of buckets $\symNumBuckets$")
fig.legend(
    ncol=3,
    loc="lower center",
)

fig.subplots_adjust(left=0.095, bottom=0.14, right=0.98, top=0.995, hspace=0.05)

fig.savefig(
    "paper/time_complexity.pdf",
    format="pdf",
    dpi=1200,
    metadata={"CreationDate": None, "ModDate": None},
)

plt.close(fig)

print(
    f"max_relative_error_jump_back_hash_32_mean = {max_relative_error(jump_back_hash_32_empirical_mean, jump_back_hash_32_theory_mean)}"
)
print(
    f"max_relative_error_jump_back_hash_mean = {max_relative_error(jump_back_hash_empirical_mean, jump_back_hash_theory_mean)}"
)
print(
    f"max_relative_error_jump_hash_mean = {max_relative_error(jump_hash_empirical_mean, jump_hash_theory_mean)}"
)
print(
    f"max_relative_error_jump_back_hash_32_variance = {max_relative_error(jump_back_hash_32_empirical_variance, jump_back_hash_32_theory_variance)}"
)
print(
    f"max_relative_error_jump_back_hash_variance = {max_relative_error(jump_back_hash_empirical_variance, jump_back_hash_theory_variance)}"
)
print(
    f"max_relative_error_jump_hash_variance = {max_relative_error(jump_hash_empirical_variance, jump_hash_theory_variance)}"
)
