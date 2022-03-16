import os
import sys
import cv2
import numpy as np
from matplotlib import pyplot as plt


path = 'C:/Users/deter/Downloads/30_cate-20220316T055655Z-001/16_category'

# print(os.listdir(path))

for i in os.listdir(path):
   train_images = cv2.imread(path + '/' + i)
   plt.imshow(train_images)
   # print(type(train_images))
   # print(sys.getsizeof(train_images))
   # print()

   # for j in os.listdir(path+'/'+i):
   #    print(j)