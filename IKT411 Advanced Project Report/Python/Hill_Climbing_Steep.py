import pylab
from pylab import *
import numpy

X = numpy.linspace(-15,15,100) # 100 linearly spaced numbers
Y = numpy.sin(X)/(X*X*X) # computing the values of sin(x)/x

plt.title(r'sin(X)/X$^3$', fontsize=20)

# compose plot
pylab.plot(X,Y) # 2*sin(x)/x and 3*sin(x)/x

pylab.show() # show the plot