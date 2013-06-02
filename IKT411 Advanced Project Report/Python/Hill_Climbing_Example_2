from pylab import *

X = np.linspace(-3, 3, 256, endpoint=False)
C = 2-X*X

plt.title(r'Stochastic Hill Climbing Example - First jump', fontsize=20)
plt.plot(X, C, color="blue", linewidth=1.0, linestyle="-")

plt.text(-2.95,3.1,r'The jump is an improvement, because the new point has a higher y-value.')

plt.plot([-1],[1],'ro')
plt.text(-1, 1, r'(-1,1) Startpoint')

plt.plot([-0.5],[1.75],'yo')
plt.text(-0.5, 1.75, r'(-0.5,1.75) First jump position')
plt.axis([-3,3,-3,4])

show()