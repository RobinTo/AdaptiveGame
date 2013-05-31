from pylab import *

X = np.linspace(-2, 2, 256,endpoint=False)
C = X*X*X - X
plt.title(r'X$^3$ - X', fontsize=20)



plot(X, C, color="blue", linewidth=1.0, linestyle="-")

show()