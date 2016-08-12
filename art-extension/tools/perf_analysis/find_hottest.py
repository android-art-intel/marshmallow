#!/usr/bin/python

import os, sys, csv, operator, heapq

def GetMethodName(filename):
  with open(filename, 'rb') as csvfile:
    spamreader = csv.reader(csvfile, delimiter=';', quotechar='"')
    for row in spamreader:
      for cell in row:
        return cell

def AppendData(filename, data):
  data[filename] = {}
  with open(filename, 'rb') as csvfile:
    spamreader = csv.reader(csvfile, delimiter=';', quotechar='"')
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

          f_data = float(data[filename][i][j])
          f_max = float(max_data[i][j])

          # compute max data
          if f_max < f_data:
            max_data[i][j] = f_data

          # compute sum data
          sum_data[i][j] += f_data
        else:
          max_data[i][j] = data[filename][i][j]
          sum_data[i][j] = data[filename][i][j]

def ComputeSumPerMethod(data, sum_per_method, metric_id):
  for filename in data:
    sum_per_method[filename] = 0
    for i in data[filename]:
      if is_number(data[filename][i][metric_id]):
        sum_per_method[filename] += float(data[filename][i][metric_id])

def DumpData(output_name, data, max_data, sum_data):
  if len(data) == 0:
    return

  with open(output_name, 'wb') as myfile:
    wr = csv.writer(myfile, quoting=csv.QUOTE_ALL)
    wr.writerow(['Max'])
    for row in max_data:
      wr.writerow(row)
    wr.writerow(['Sum'])
    for row in sum_data:
      wr.writerow(row)

def DumpHottestMethods(output_name, sorted_methods, sum_per_method):
  if len(data) == 0:
    return

  with open(output_name, 'wb') as myfile:
    wr = csv.writer(myfile, quoting=csv.QUOTE_ALL)
    for filename in sorted_methods:
      wr.writerow([GetMethodName(filename), sum_per_method[filename]])

if len(sys.argv) != 2:
  print "Usage: ./find_hottest.py [folder-name]"
  sys.exit(0)

data = {}
folder_name = sys.argv[1]
found_file = False
print "Collecting data..."
for filename in os.listdir(folder_name):
  if filename.endswith(".csv"):
    filename = folder_name + "/" + filename
    AppendData(filename, data)
    found_file = True

if found_file == False:
  print "There is no CSV file in folder " + folder_name
else:
  sum_per_method = {}
  print "Computing sum per method..."
  ComputeSumPerMethod(data, sum_per_method, 1)
  print "Sorting data..."

  # sorted_methods = heapq.nlargest(10, list(sum_per_method))
  sorted_methods = list(sorted(sum_per_method, key=sum_per_method.__getitem__, reverse=True))
  print "Dumping data..."
  DumpHottestMethods("hottest_methods.csv", sorted_methods, sum_per_method)


  sys.stdout.write("\nDone.\n")
