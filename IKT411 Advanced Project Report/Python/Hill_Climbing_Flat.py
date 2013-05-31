from pylab import *

X = np.linspace(-3, 3, 256, endpoint=False)
C = 0*X + 2

plt.title(r'Y = 2', fontsize=20)

plot(X, C, color="blue", linewidth=1.0, linestyle="-")



show()