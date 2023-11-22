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
import json
import matplotlib.pyplot as plt


def plot_data_chart(ax, data, test_name, label, color, linestyle):
    d = {}

    for r in data:
        if (
            r["benchmark"]
            != "com.dynatrace.jumpbackhash." + test_name + "PerformanceTest.getBucket"
        ):
            continue

        n = int(r["params"]["numBuckets"])
        time = float(r["primaryMetric"]["score"])
        d[n] = time

    ax.set_xscale("log", base=10)
    ax.set_yscale("log", base=10)
    ax.set_xlim(1, 1e6)
    ax.set_ylim(1, 200)

    xvals = sorted(d)
    yvals = [d[x] for x in xvals]

    ax.plot(xvals, yvals, label=label, color=color, linestyle=linestyle)


def plot_data(input_file, output_file):

    f = open(input_file)
    data = json.load(f)

    fig, axs = plt.subplots(1, 1, sharex=True, sharey=True)
    fig.set_size_inches(5, 4)

    plot_data_chart(
        axs,
        data,
        "ModuloMapper",
        "Modulo",
        preamble.colors["Modulo"],
        preamble.linestyles["Modulo"],
    )
    plot_data_chart(
        axs,
        data,
        "RandomMapper",
        "Random",
        preamble.colors["Random"],
        preamble.linestyles["Random"],
    )
    plot_data_chart(
        axs,
        data,
        "JumpHash",
        "JumpHash",
        preamble.colors["JH"],
        preamble.linestyles["JH"],
    )
    plot_data_chart(
        axs,
        data,
        "DummyOperation",
        "Dummy operation",
        preamble.colors["Dummy"],
        preamble.linestyles["Dummy"],
    )
    plot_data_chart(
        axs,
        data,
        "JumpBackHash",
        r"JumpBackHash*",
        preamble.colors["JBH*"],
        preamble.linestyles["JBH*"],
    )

    axs.set_ylabel(r"computation time (ns)")
    axs.set_xlabel(r"number of buckets $\symNumBuckets$")
    axs.grid(which="major", linestyle="-")

    fig.legend(
        ncol=3,
        loc="lower center",
    )

    fig.subplots_adjust(
        top=0.995, bottom=0.25, left=0.1, right=0.98, hspace=0.07, wspace=0.05
    )

    fig.savefig(
        output_file,
        format="pdf",
        dpi=1200,
        metadata={"CreationDate": None, "ModDate": None},
    )
    plt.close(fig)


plot_data("results/benchmark-results.json", "paper/benchmark.pdf")
