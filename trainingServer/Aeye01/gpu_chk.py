from tensorflow.python.client import device_lib
print(device_lib.list_local_devices())

# import tensorflow as tf
# import os
# os.environ["CUDA_VISIBLE_DEVICES"] = "0"

# tf.debugging.set_log_device_placement(True)

# # 텐서 생성
# a = tf.constant([[1.0, 2.0, 3.0], [4.0, 5.0, 6.0]])
# b = tf.constant([[1.0, 2.0], [3.0, 4.0], [5.0, 6.0]])
# c = tf.matmul(a, b)

# print(c)

