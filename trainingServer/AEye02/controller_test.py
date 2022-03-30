from pydoc import plain
import requests
import json
from firebase_quickstart_python_remote_config import _get_access_token
from firebase_quickstart_python_remote_config import _publish
from firebase_quickstart_python_remote_config import _get
from image_maker import image_maker as im
from snack_classifier import snack_model_maker as smm
from slack_post import get_channel_id
from slack_post import image_upload
from slack_sdk import WebClient
import datetime as dt

ORIGIN_IMAGE_PATH = '/home/team1/AEye/data/image/snack_1'
INPUT_IMAGE_PATH = ORIGIN_IMAGE_PATH + '_rota'
MODEL_PATH = '/home/team1/AEye/model_float16'
KEY_PATH = '/home/team1/AEye/keys'
KEY_NAME = 'aeye-9c1ee-firebase-adminsdk-9glw7-8f233a41fb.json'
PROJECT_ID = 'aeye-9c1ee'
BASE_URL = 'https://firebaseremoteconfig.googleapis.com'
REMOTE_CONFIG_ENDPOINT = 'v1/projects/' + PROJECT_ID + '/remoteConfig'
REMOTE_CONFIG_URL = BASE_URL + '/' + REMOTE_CONFIG_ENDPOINT
JSON_PATH = '/home/team1/AEye/remote_config_info/config.json'
SLACK_TOKEN = "xoxb-3304749042240-3295636403457-PND6zGxAgmSk7NhoQYSnINeF"

# 이미지 전처리 수행 ------------------------------------------------------------------
dir_list = im(ORIGIN_IMAGE_PATH)

tmp = ''
for i, dir_idx in enumerate(dir_list):
   tmp += f'idx : {i:>3d}, category : {dir_list[dir_idx].split("_")[1]} \n'
