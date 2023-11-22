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
import matplotlib

matplotlib.use("PDF")
import matplotlib.pyplot as plt

latex_preamble = ""
with open("paper/symbols.tex") as f:
    for l in f.readlines():
        if not "%" in l:
            latex_preamble += l[:-1]

latex_preamble += r"\RequirePackage[T1]{fontenc} \RequirePackage[tt=false, type1=true]{libertine} \RequirePackage[varqu]{zi4} \RequirePackage[libertine]{newtxmath}\RequirePackage{amsmath}"
matplotlib.use("PDF")

plt.rc("text", usetex=True)
plt.rc("text.latex", preamble=latex_preamble)

colors = {
    "JH": "#ffa600",
    "JBH": "#dd5182",
    "JBH*": "#003f5c",
    "Modulo": "#955196",
    "Random": "#ff6e54",
    "Dummy": "#444e86",
}
linestyles = {
    "JH": "solid",
    "JBH": "solid",
    "JBH*": "solid",
    "Modulo": "dashdot",
    "Random": (0, (1, 1)),
    "Dummy": "dotted",
}
