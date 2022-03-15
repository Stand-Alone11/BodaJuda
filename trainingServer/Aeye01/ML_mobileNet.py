# Import dependencies
import tensorflow as tf
from tensorflow import keras

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import random

print("TensorFlow version:", tf.__version__)

# GPU 사용 여부 확인
from tensorflow.python.client import device_lib
print(device_lib.list_local_devices())

# Import MNIST dataset
mnist = keras.datasets.mnist
(train_images, train_labels), (test_images, test_labels) = mnist.load_data()

# Normalize the input image so that each pixel value is between 0 to 1.
train_images = train_images / 255.0
test_images = test_images / 255.0

# Add a color dimension to the images in "train" and "validate" dataset to
# leverage Keras's data augmentation utilities later.
train_images = np.expand_dims(train_images, axis=3)
test_images = np.expand_dims(test_images, axis=3)

# Define data augmentation configs
datagen = keras.preprocessing.image.ImageDataGenerator(
  rotation_range=30,
  width_shift_range=0.25,
  height_shift_range=0.25,
  shear_range=0.25,
  zoom_range=0.2
)

# Generate augmented data from MNIST dataset
train_generator = datagen.flow(train_images, train_labels)
test_generator = datagen.flow(test_images, test_labels)

# Define and train the Keras model.
model = keras.Sequential([
  keras.layers.InputLayer(input_shape=(28, 28, 1)),
  keras.layers.Conv2D(filters=32, kernel_size=(3, 3), activation=tf.nn.relu),
  keras.layers.Conv2D(filters=64, kernel_size=(3, 3), activation=tf.nn.relu),
  keras.layers.MaxPooling2D(pool_size=(2, 2)),
  keras.layers.Dropout(0.25),
  keras.layers.Flatten(),
  keras.layers.Dense(10, activation=tf.nn.softmax)
])
model.compile(optimizer='adam',
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy'])
hist = model.fit(train_generator, epochs=5, validation_data=test_generator)

print(hist.history['loss'])

# Convert Keras model to TF Lite format and quantize.
converter = tf.lite.TFLiteConverter.from_keras_model(model)
converter.optimizations = [tf.lite.Optimize.DEFAULT]
tflite_model = converter.convert()
with open('./model/mnist_v5.tflite', "wb") as f:
  f.write(tflite_model)

import os
import json
import firebase_admin
from firebase_admin import ml


with open("./keys/aeye01-firebase-adminsdk-als38-67adf4ae6e.json",'r') as f:
   uploaded = json.load(f)

print("파일 로드 성공")


os.environ["GOOGLE_APPLICATION_CREDENTIALS"]='/content/' + 'aeye01-firebase-adminsdk-als38-67adf4ae6e.json'

firebase_admin.initialize_app(
    options={'projectId': uploaded['project_id'], 
              'storageBucket': uploaded['project_id'] + '.appspot.com' })

print("파일 옵션 설정 확인")

# This uploads it to your bucket as mmnist_v2.tflite
source = ml.TFLiteGCSModelSource.from_keras_model(model, './model/mnist_v5.tflite')
print (source.gcs_tflite_uri)

print("모델 가져오기")

# Create a Model Format
model_format = ml.TFLiteFormat(model_source=source)

print("모델 tflite로 변환")

# Create a Model object
sdk_model_1 = ml.Model(display_name="mnist_v5", model_format=model_format)

print("firebase 모델 이름 저장")

# Make the Create API call to create the model in Firebase
firebase_model_1 = ml.create_model(sdk_model_1)
print(firebase_model_1.as_dict())

print("firebase 모델 생성")

# Publish the model
model_id = firebase_model_1.model_id
firebase_model_1 = ml.publish_model(model_id)

print("firebase에 업로드")