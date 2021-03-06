from pylab import *

X = np.linspace(-3, 3, 256, endpoint=False)
C = 2-X*X

plt.title(r'Stochastic Hill Climbing Example - Global Maximum', fontsize=20)
plt.plot(X, C, color="blue", linewidth=1.0, linestyle="-")

plt.text(-1.55,3,r'This jump achieves the global maximum.')

plt.plot([0],[2],'yo')
plt.text(0, 2, r'(0,2) Global maximum')


plt.plot([-0.5],[1.75],'ro')
plt.text(-2.5, 1.75, r'(-0.5,1.75) Previous position')

plt.axis([-3,3,-3,4])

show()