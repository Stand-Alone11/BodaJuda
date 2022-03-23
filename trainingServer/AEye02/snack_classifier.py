from calendar import EPOCH
import os
import numpy as np
import tensorflow as tf
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

# GPU 사용 여부 확인
# from tensorflow.python.client import device_lib
# print(device_lib.list_local_devices())
# model_name = "snack_v8"

# image_path = pathlib.Path('/home/team1/AEye/data/image/snack_4_rota')
# image_path = pathlib.Path('C:/Users/deter/Desktop/snack_4')


def snack_model_maker(model_name, image_path, model_path, key_path, key_name):
    image_path = pathlib.Path(image_path)
    data = DataLoader.from_folder(image_path)

    train_data, rest_data = data.split(0.8)
    validation_data, test_data = rest_data.split(0.5)

    # https://www.tensorflow.org/lite/api_docs/python/tflite_model_maker/image_classifier/create 파라미터
    # model = image_classifier.create(train_data, validation_data=validation_data, epochs=10, dropout_rate=0.2, batch_size=1024, learning_rate = 0.001)
    model = image_classifier.create(train_data, validation_data=validation_data, epochs=10)
    model.summary()
    loss, accuracy = model.evaluate(test_data)
    print("loss, accuracy")
    print(f"loss : {loss:.4f}")
    print(f"accuracy : {accuracy:.4f}")

    # model.export(export_dir='./model/',tflite_filename=(model_name+'_n.tflite'))
    # model.evaluate_tflite('./model/'+model_name+'_n.tflite', test_data)


    config = QuantizationConfig.for_float16()
    model.export(export_dir=model_path, tflite_filename=(model_name+'_f16.tflite'), quantization_config=config)
    model.evaluate_tflite(model_path+'/'+model_name+'_f16.tflite', test_data)

    os.environ["GOOGLE_APPLICATION_CREDENTIALS"]=key_path+'/' + key_name
    pj_name = key_name.split('-firebase')
    firebase_admin.initialize_app(
        options={'projectId': pj_name[0], 
                'storageBucket': pj_name[0] + '.appspot.com' })

    print("firebase 저장소 키 확인")

    # This uploads it to your bucket as mmnist_v2.tflite
    source = ml.TFLiteGCSModelSource.from_tflite_model_file(model_path+'/'+model_name+'_f16.tflite')
    print (source.gcs_tflite_uri)
    print("f16 모델 가져오기")

    # Create a Model Format
    model_format = ml.TFLiteFormat(model_source=source)
    print("모델 포맷 확인")

    # Create a Model object
    # format 어떻게 구해야하지....
    sdk_model_1 = ml.Model(display_name=model_name, model_format=model_format)
    print("firebase 저장소에 저장할 모델 이름 설정")

    # Make the Create API call to create the model in Firebase
    firebase_model_1 = ml.create_model(sdk_model_1)
    print("firebase f16 모델 생성")

    # Publish the model
    model_id = firebase_model_1.model_id
    firebase_model_1 = ml.publish_model(model_id)
    print("firebase에 모델 업로드")
