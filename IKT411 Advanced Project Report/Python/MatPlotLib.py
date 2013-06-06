from pylab import *


def plotFunction(stat):
	fname = "D:/Dropbox/LibGDXExample/Logfiles/Logfile_Adrian.txt"
	with open(fname) as f:
	    content = f.readlines()
	    f.close()

	content = filter(None, content);

	counter = []
	count = 0
	values = []
	gamelengthmultiplier = 1.0


	for item in content:
		item = item.replace(" ", "")
		item = item.replace("Min", "")
		item = item.replace("Max", "")
		if stat in item and stat in item.split(':'):
			words = item.split(":")
			values.append(float(words[1])/gamelengthmultiplier)
			gamelengthmultiplier += 0.2
			counter.append(count)
			count = count+1

	plt.plot(counter, values)

	plt.ylim([0, 10.0])
	plt.xlim([0, 20])

def plotHPFunction(stat):
	fname = "D:/Dropbox/LibGDXExample/Logfiles/Logfile_Adrian.txt"
	with open(fname) as f:
	    content = f.readlines()
	    f.close()

	content = filter(None, content);

	counter = []
	count = 0
	values = []

	for item in content:
		item = item.replace(" ", "")
		item = item.replace("Min", "")
		item = item.replace("Max", "")
		if stat in item and stat in item.split(':'):
			words = item.split(":")
			values.append(words[1])
			counter.append(count)
			count = count+1

	plt.plot(counter, values)


plotHPFunction("GlobalMonsterHP")
plotFunction("Challengermetric")
show()