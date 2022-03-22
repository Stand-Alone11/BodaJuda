from image_maker import image_maker as im
from snack_classifier import snack_model_maker as smm

origin_image_path = '/home/team1/AEye/data/image/snack_4'
input_image_path = origin_image_path + '_rota'
model_name = 'snack_v8'
model_path = '/home/team1/AEye/model_float16'
key_path = '/home/team1/AEye/keys'
key_name = 'aeye-9c1ee-firebase-adminsdk-9glw7-8f233a41fb.json'
im(origin_image_path)
smm(model_name,input_image_path,model_path,key_path, key_name)
