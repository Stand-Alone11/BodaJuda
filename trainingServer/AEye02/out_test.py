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
from image_maker import image_maker

ORIGIN_IMAGE_PATH = '/home/team1/AEye/data/image/snack_20+5'
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
list = image_maker('/home/team1/AEye/data/image/snack_4')
for idx, value in enumerate(list):
   print(value)
   print(list[idx])



# model = image_classifier.create(train_data, validation_data=validation_data, epochs=1)


# label_idx = dict()
# idx_cnt = 0
# for i, name in enumerate(test_data.index_to_label):
#    label_idx[name] = i


# datatype_len = len(test_data.index_to_label)
# predicts = model.predict_top_k(test_data, k=datatype_len)

# confuse_matrix_cnt = [[0]*datatype_len for i in range(datatype_len)]
# confuse_matrix_acc = [[0.0]*datatype_len for i in range(datatype_len)]
# confuse_matrix_acc_all = [[0.0]*datatype_len for i in range(datatype_len)]


# for i, (image, label) in enumerate(test_data.gen_dataset().unbatch().take(-1)):
#   predict_label = label_idx[predicts[i][0][0]]
#   target_label = label.numpy()
#   confuse_matrix_cnt[target_label][predict_label] += 1
#   confuse_matrix_acc[target_label][predict_label] += predicts[i][0][1]
#   for j in predicts[i]:
#      idx = label_idx[j[0]]
#      confuse_matrix_acc_all[target_label][idx] += j[1]

# for i in range(datatype_len):
#    sumCnt = 0
#    for j in range(datatype_len):
#       sumCnt += confuse_matrix_cnt[i][j]

#    for j in range(datatype_len):
#       if confuse_matrix_cnt[i][j] != 0:
#          confuse_matrix_acc[i][j] = round( confuse_matrix_acc[i][j]/confuse_matrix_cnt[i][j],2)
#       confuse_matrix_cnt[i][j] = round( confuse_matrix_cnt[i][j]/sumCnt,2)
#       confuse_matrix_acc_all[i][j] = round( confuse_matrix_acc_all[i][j]/sumCnt,2)
      

# plt.figure(figsize=(16,8))
# sns.heatmap(confuse_matrix_cnt, xticklabels=range(datatype_len), yticklabels=range(datatype_len), 
#           annot=True, fmt='g')
# plt.title('Classification Probability')
# plt.xlabel('Prediction')
# plt.ylabel('Target')
# plt.savefig('./Classification_Probability.png',dpi = 300)

# plt.figure(figsize=(16,8))
# sns.heatmap(confuse_matrix_acc, xticklabels=range(datatype_len), yticklabels=range(datatype_len), 
#           annot=True, fmt='g')
# plt.title('Classification Confidence Mean')
# plt.xlabel('Prediction')
# plt.ylabel('Target')
# plt.savefig('./Classification_Confidence_Mean.png',dpi = 300)

# plt.figure(figsize=(16,8))
# sns.heatmap(confuse_matrix_acc_all, xticklabels=range(datatype_len), yticklabels=range(datatype_len), 
#           annot=True, fmt='g')
# plt.title('Classification Confidence Mean')
# plt.xlabel('Prediction')
# plt.ylabel('Target')
# plt.savefig('./Classification_Confidence_Mean_All.png',dpi = 300)