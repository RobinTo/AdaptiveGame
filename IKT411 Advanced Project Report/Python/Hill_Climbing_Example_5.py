from pylab import *

X = np.linspace(-3, 3, 256, endpoint=False)
C = 2-X*X

plt.title(r'Stochastic Hill Climbing Example - Further Jumps', fontsize=20)
plt.plot(X, C, color="blue", linewidth=1.0, linestyle="-")

plt.text(-2.5,3,r'Further jumps will not be valued better than global maximum,')
plt.text(-2.5,2.78,r'and will be discarded.')

plt.plot([0],[2],'ro')
plt.text(0, 2, r'(0,2) Global maximum')


plt.plot([1],[1],'wo')
plt.text(1, 1, r'(1,1) Attempted jump')

plt.plot([-1.3],[0.31],'wo')
plt.text(-1.3, 0.31, r'(-1.3,0.31) Attempted jump')

plt.axis([-3,3,-3,4])

show()