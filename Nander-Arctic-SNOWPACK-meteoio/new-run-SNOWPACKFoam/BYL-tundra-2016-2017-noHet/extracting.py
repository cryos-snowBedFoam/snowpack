import matplotlib
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker
import matplotlib.dates as mdates
import numpy as np
from matplotlib.dates import MonthLocator, YearLocator,strpdate2num, num2date,datestr2num
from datetime import datetime
from matplotlib.ticker import MaxNLocator
import os,sys
import math
from scipy import integrate
from scipy.signal import savgol_filter
import matplotlib.ticker as ticker


working_dir= os.getcwd()

for root, dirs, files in os.walk(working_dir):
	for dir in dirs:
		if dir.startswith("processor"):
			print("for ", dir, " extracting process .......")
			for file in os.listdir(working_dir+"/"+dir):
				if file.endswith(".tar"):
					print(file)
					cmd="cd "+dir+"; "+"tar -xvf "+file+" ; "+"rm "+file+";"
					os.system(cmd)
