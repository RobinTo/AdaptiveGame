from pylab import *

X = np.linspace(-3, 3, 256, endpoint=False)
C = 2-X*X

plt.title(r'Stochastic Hill Climbing Example - Attemted jump', fontsize=20)
plt.plot(X, C, color="blue", linewidth=1.0, linestyle="-")

plt.text(-2,3,r'Second jump is not an improvement, and is discarded.')

plt.plot([-0.5],[1.75],'ro')
plt.text(-0.5, 1.75, r'(-0.5,1.75) First jump position')


plt.plot([1.2],[0.56],'wo')
plt.text(0, 0.56, r'(1.2,0.56) Attempted second jump')

plt.axis([-3,3,-3,4])

show()