from calendar import EPOCH
from doctest import testfile
import os
import numpy as np
import tensorflow as tf
import seaborn as sns
from tensorflow import keras
import firebase_admin
from firebase_admin import ml
assert tf.__version__.startswith('2')
from tflite_model_maker import model_spec
from tflite_model_maker import image_classifier
from tflite_model_maker.config import ExportFormat
from tflite_model_maker.config import QuantizationConfig
from tflite_model_maker.image_classifier import DataLoader
import matplotlib.pyplot as plt
import pathlib

ORIGIN_IMAGE_PATH = '/home/team1/AEye/data/image/snack_4'
INPUT_IMAGE_PATH = ORIGIN_IMAGE_PATH + '_rota'

image_path = pathlib.Path(INPUT_IMAGE_PATH)
data = DataLoader.from_folder(image_path)

train_data, rest_data = data.split(0.8)
validation_data, test_data = rest_data.split(0.5)

# A helper function that returns 'red'/'black' depending on if its two input
# parameter matches or not.
# def get_label_color(val1, val2):
#   if val1 == val2:
#     return 'black'
#   else:
#     return 'red'

# Then plot 100 test images and their predicted labels.
# If a prediction result is different from the label provided label in "test"
# dataset, we will highlight it in red color.

# plt.figure(figsize=(20, 20))
model = image_classifier.create(train_data, validation_data=validation_data, epochs=5)
predicts = model.predict_top_k(test_data)
# for i, (image, label) in enumerate(test_data.gen_dataset().unbatch().take(100)):
#   ax = plt.subplot(10, 10, i+1)
#   plt.xticks([])
#   plt.yticks([])
#   plt.grid(False)
#   plt.imshow(image.numpy(), cmap=plt.cm.gray)

#   predict_label = predicts[i][0][0]
#   color = get_label_color(predict_label,
#                           test_data.index_to_label[label.numpy()])
#   ax.xaxis.label.set_color(color)
#   plt.xlabel('Predicted: %s' % predict_label)
#   if (i+1)%10 == 0:
#     print(f'{label.numpy():>2d} ')
#   else:
#     print(f'{label.numpy():>2d} ' , end = '')
# plt.show()

datatype_len = len(test_data.index_to_label)

confuse_matrix = [[0]*datatype_len for i in range(datatype_len)]

# for i in predicts[0:10]:
#    for j in i:
#       for k in j:
#          print(f'{k} ' , end = '')
#       print()

# for i, (image, label) in enumerate(test_data.gen_dataset().unbatch().take(2)):
#   predict_label = predicts[i][0][0] # str
#   for j in predicts[i][0]:
#    print(j)


def findidx(label):
   idx = -1
   for i, v in enumerate(test_data.index_to_label):
      if(label==v):
         idx = i
   return idx


for i, (image, label) in enumerate(test_data.gen_dataset().unbatch().take(-1)):
  predict_label = findidx(predicts[i][0][0]) # str
#   target_label = test_data.index_to_label[label.numpy()] # str
  target_label = label.numpy()
#   print(f'type : {type(predict_label)}')
#   print(f'type : {type(target_label)}')
#   print(f'content : {(predict_label)}')
#   print(f'content : {(target_label)}')
  confuse_matrix[int(predict_label)][target_label] += 1

  
# print(confuse_matrix)

for i in range(4):
   for j in range(4):
      print(f'{confuse_matrix[i][j]:>3d} ', end ='')
   print()

plt.figure(figsize=(10, 8))
sns.heatmap(confuse_matrix, xticklabels=test_data.index_to_label, yticklabels=test_data.index_to_label, 
          annot=True, fmt='g')
plt.xlabel('Prediction')
plt.ylabel('Label')
plt.show()

