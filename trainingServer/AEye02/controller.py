from image_maker import image_maker as im
from snack_classifier import snack_model_maker as smm

ORIGIN_IMAGE_PATH = '/home/team1/AEye/data/image/snack_4'
INPUT_IMAGE_PATH = ORIGIN_IMAGE_PATH + '_rota'
MODEL_NAME = 'snack_v8'
MODEL_PATH = '/home/team1/AEye/model_float16'
KEY_PATH = '/home/team1/AEye/keys'
KEY_NAME = 'aeye-9c1ee-firebase-adminsdk-9glw7-8f233a41fb.json'


im(ORIGIN_IMAGE_PATH)
smm(MODEL_NAME,INPUT_IMAGE_PATH,MODEL_PATH,KEY_PATH, KEY_NAME)
