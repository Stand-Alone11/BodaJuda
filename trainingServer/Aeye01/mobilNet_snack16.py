from calendar import EPOCH
import os
import numpy as np
import tensorflow as tf
assert tf.__version__.startswith('2')
from tflite_model_maker import model_spec
from tflite_model_maker import image_classifier
from tflite_model_maker.config import ExportFormat
from tflite_model_maker.config import QuantizationConfig
from tflite_model_maker.image_classifier import DataLoader
import matplotlib.pyplot as plt
import pathlib

# GPU 사용 여부 확인
# from tensorflow.python.client import device_lib
# print(device_lib.list_local_devices())

image_path = pathlib.Path('/home/team1/AEye/data/image/16_category')
data = DataLoader.from_folder(image_path)

train_data, rest_data = data.split(0.8)
validation_data, test_data = rest_data.split(0.5)

import cv2
data = DataLoader.from_folder(image_path)

model = image_classifier.create(train_data, validation_data=validation_data)
model.summary()
loss, accuracy = model.evaluate(test_data)
print("loss, accuracy")
print(loss)
print(accuracy)

# model.export(export_dir='./model/')
# model.evaluate_tflite('./model/model.tflite', test_data)


config = QuantizationConfig.for_float16()
model.export(export_dir='./model_float16', tflite_filename='snack_v2.tflite', quantization_config=config)


from tensorflow import keras
import firebase_admin
from firebase_admin import ml


os.environ["GOOGLE_APPLICATION_CREDENTIALS"]='./keys/' + 'aeye-9c1ee-firebase-adminsdk-9glw7-8f233a41fb.json'

firebase_admin.initialize_app(
    options={'projectId': "aeye-9c1ee", 
              'storageBucket': "aeye-9c1ee" + '.appspot.com' })

print("파일 옵션 설정 확인")

# This uploads it to your bucket as mmnist_v2.tflite
source = ml.TFLiteGCSModelSource.from_tflite_model_file('./model_float16/snack_v2.tflite')
print (source.gcs_tflite_uri)

print("모델 가져오기")

# Create a Model Format
model_format = ml.TFLiteFormat(model_source=source)

print("모델 tflite로 변환")

# Create a Model object
# format 어떻게 구해야하지....
sdk_model_1 = ml.Model(display_name="snack_v2", model_format=model_format)
# sdk_model_1 = ml.Model(display_name="snack_model")

print("firebase 모델 이름 저장")

# Make the Create API call to create the model in Firebase
firebase_model_1 = ml.create_model(sdk_model_1)
print(firebase_model_1.as_dict())

print("firebase 모델 생성")

# Publish the model
model_id = firebase_model_1.model_id
firebase_model_1 = ml.publish_model(model_id)

print("firebase에 업로드")