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


#print "This is the name of the script: ", sys.argv[0]
#print "Please type as: python", sys.argv[0],sys.argv[1],sys.argv[2],sys.argv[3]
#print "the minimum time after which the validation is possible ", 1.0/0.002
#u0=50 # the initail value for temperature
#timeInSecond=int(sys.argv[1])
#print math.pi, math.sin(0.5* math.pi)

#
print("how to use: ", " python3 densityDiff.py fileName.pro\n")

#
inputFile=sys.argv[1]
print(inputFile)

#
deltaT=float(inputFile.split("deltaT=")[1].split("-")[0])
porosity=float(inputFile.split("por=")[1].split("-")[0])
Ra=int(float(inputFile.split("Ra")[1].split(".")[0]))
H=float(inputFile.split("H=")[1].split("-")[0])
L=float(inputFile.split("L=")[1].split("-")[0])

#
read_pro=open(inputFile,"r")
lines_pro = read_pro.readlines()

#	
write_pro=open("modified_"+inputFile,"w")

# writing the header
loc=0
while(True):
	line=lines_pro[loc]
	if line.find("[DATA]") == 0:
		write_pro.write(line)
		loc=loc+1
		break
	elif line.find("0905,nElems, element") == 0:
		write_pro.write(line)
		write_pro.write("0906,nElems,element density diffrence compared to the initial density (kg m-3)\n")
		loc=loc+1		
	else:
		write_pro.write(line)
		loc=loc+1		
#write_pro.close()

#
#loc=12
while loc<len(lines_pro)-13:
	line_pro=lines_pro[loc]
	
	#
	if line_pro.split(",")[0]=="0500":
			# write the line 0500
			write_pro.write(line_pro)
			
			# write the line 0501
			numNodeOElement=int(lines_pro[loc+1].split(",")[1]) #0501
			l0501="0501"+","+str(numNodeOElement)
			i=0
			while(i<numNodeOElement):
				hei_node=float(lines_pro[loc+1].split(",")[i+2])/H
				l0501=l0501+","+str(hei_node)					
				i=i+1
			l0501=l0501+"\n"
			write_pro.write(l0501)
			
			# write the line 0502
			write_pro.write(lines_pro[loc+2])
			
			# write the line 0503
			write_pro.write(lines_pro[loc+3])
			
			# write the line 0515
			write_pro.write(lines_pro[loc+4])
			
			# write the line 0516
			write_pro.write(lines_pro[loc+5])
			
			# write the line 0520
			write_pro.write(lines_pro[loc+6])
			
			# write the line 0521
			write_pro.write(lines_pro[loc+7])
			
			# write the line 0901
			write_pro.write(lines_pro[loc+8])
			
			# write the line 0902
			write_pro.write(lines_pro[loc+9])
			
			# write the line 0903
			write_pro.write(lines_pro[loc+10])
			
			# write the line 0904
			write_pro.write(lines_pro[loc+11])
			
			# write the line 0905
			write_pro.write(lines_pro[loc+12])
			
			# write the line 0906
			numNodeOElement=int(lines_pro[loc+2].split(",")[1])
			l0906="0906"+","+str(numNodeOElement)
			i=0
			while(i<numNodeOElement):
				denDiff_el=float(lines_pro[loc+2].split(",")[i+2])-(1.0-porosity)*917.
				l0906=l0906+","+str(denDiff_el)					
				i=i+1
			l0906=l0906+"\n"
			write_pro.write(l0906)			
	#		
	loc+=1
