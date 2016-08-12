#!/usr/bin/python

import os, sys, csv

def AppendData(filename, data):
  data[filename] = {}
  with open(filename, 'rb') as csvfile:
    spamreader = csv.reader(csvfile, delimiter=',', quotechar='"')
    for i,row in enumerate(spamreader):
      data[filename][i] = {}
      for j,cell in enumerate(row):
        data[filename][i][j] = cell

def is_number(s):
    try:
        float(s)
        return True
    except ValueError:
        return False

def ComputeData(data, max_data, sum_data):
  for filename in data:
    for i in data[filename]:
      if len(max_data) < len(data[filename]):
        max_data.append([])
      if len(sum_data) < len(data[filename]):
        sum_data.append([])
      for j in data[filename][i]:
        # append new 0s if any
        if len(max_data[i]) < len(data[filename][i]):
          max_data[i].append(0)

        if len(sum_data[i]) < len(data[filename][i]):
            sum_data[i].append(0)

        # if cell is a number, then we can update our numbers.
        if is_number(data[filename][i][j]):
          if len(max_data[i]) < len(data[filename][i]):
            max_data[i].append(0)

          if len(sum_data[i]) < len(data[filename][i]):
            sum_data[i].append(0)

          if i != 0:
            f_data = float(data[filename][i][j])
            if is_number(max_data[i][j]):
              f_max = float(max_data[i][j])

              # compute max data
              if f_max < f_data:
                max_data[i][j] = f_data

            # compute sum data
            if is_number(sum_data[i][j]):
              sum_data[i][j] += f_data
        else:
          if i == 0 and j == 0:
            max_data[i][j] = ""
            sum_data[i][j] = ""
          else:
            max_data[i][j] = data[filename][i][j]
            sum_data[i][j] = data[filename][i][j]

def DumpData(output_name, data, max_data, sum_data):
  if len(data) == 0:
    return

  with open(output_name, 'wb') as myfile:
    wr = csv.writer(myfile, delimiter=',', quoting=csv.QUOTE_ALL)
    wr.writerow(['Max'])
    for row in max_data:
      wr.writerow(row)
    wr.writerow(['Sum'])
    for row in sum_data:
      wr.writerow(row)

if len(sys.argv) != 2:
  print "Usage: ./gathering.py [folder-name]"
  sys.exit(0)

data = {}
folder_name = sys.argv[1]
nb_files = 0
print "Collecting data from folder " + folder_name + "..."
for filename in os.listdir(folder_name):
  if filename.endswith(".hash.csv"):
    filename = folder_name + "/" + filename
    AppendData(filename, data)
    nb_files = nb_files + 1

if nb_files == 0:
  print "There is no CSV file in folder " + folder_name
else:
  max_data = []
  sum_data = []
  print "Computing data..."
  ComputeData(data, max_data, sum_data)

  print "Dumping computed data..."
  output_filename = "summary.csv"
  DumpData(output_filename, data, max_data, sum_data)
  print "Summarized " + str(nb_files) + " files in file " + output_filename
