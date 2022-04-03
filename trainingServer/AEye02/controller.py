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

ORIGIN_IMAGE_PATH = '/home/team1/AEye/data/j6s004.p.ssafy.io/downloads'
# ORIGIN_IMAGE_PATH = '/home/team1/AEye/data/image/snack_30'
# INPUT_IMAGE_PATH = ORIGIN_IMAGE_PATH + '_rota'
INPUT_IMAGE_PATH = ORIGIN_IMAGE_PATH + '_preprocessing'
MODEL_PATH = '/home/team1/AEye/model_float16'
KEY_PATH = '/home/team1/AEye/keys'
KEY_NAME = 'aeye-9c1ee-firebase-adminsdk-9glw7-8f233a41fb.json'
PROJECT_ID = 'aeye-9c1ee'
BASE_URL = 'https://firebaseremoteconfig.googleapis.com'
REMOTE_CONFIG_ENDPOINT = 'v1/projects/' + PROJECT_ID + '/remoteConfig'
REMOTE_CONFIG_URL = BASE_URL + '/' + REMOTE_CONFIG_ENDPOINT
JSON_PATH = '/home/team1/AEye/remote_config_info/config.json'
SLACK_TOKEN = "xoxb-3304749042240-3295636403457-PND6zGxAgmSk7NhoQYSnINeF"

# slack 메세지 보내기 ----------------------------------------------------------------
slack = WebClient(SLACK_TOKEN)
channel_id = get_channel_id(slack,"sw-report")
x = dt.datetime.now()
slack.chat_postMessage(
    channel=channel_id,
    blocks=[
        {
			"type": "divider"
		},
		{
			"type": "header",
			"text": {
				"type": "plain_text",
				"text": f"{x.year}년 {x.month}월 {x.day}일 {x.hour}시 {x.minute}분",
				"emoji": True
			}
		}
    ]
)


# config.json 파일 읽기 ---------------------------------------------------------------------------
_get()

with open(JSON_PATH) as f:
   json_data = json.load(f)
# print(json_data)
# print(json.dumps(json_data, indent="\t") )

# config.json 파일 파라미터 수정 ------------------------------------------------------
model_name = json_data['parameters']['next_model']['defaultValue']['value']
next_name = 'snack_v' + str(int(model_name.split('_v')[1])+1)

print("remote config 파라미터 변경 완료")
print(f'model_name : {model_name}')
print(f'next_name : {next_name}')

x = dt.datetime.now()
slack.chat_postMessage(
    channel=channel_id,
    blocks=[
        {
			"type": "divider"
		},
		{
			"type": "section",
			"text": {
				"type": "plain_text",
				"text": f"현재 배포 모델 버전 : {model_name}",
				"emoji": True
			}
		},
		{
			"type": "section",
			"text": {
				"type": "plain_text",
				"text": f"{x.hour }시 {x.minute}분 이미지 전처리 시작",
				"emoji": True
			}
		}
    ]
)


# config.json 파일 수정 --------------------------------------------------------------
json_data['parameters']['model_name']['defaultValue']['value'] = model_name
json_data['parameters']['next_model']['defaultValue']['value'] = next_name

with open(JSON_PATH, 'w', encoding='utf-8') as make_file:
    json.dump(json_data, make_file, indent="\t")


# 이미지 전처리 수행 ------------------------------------------------------------------
dir_list = im(ORIGIN_IMAGE_PATH)

tmp = ''
for i, dir_idx in enumerate(dir_list):
   tmp += f'idx : {i:>3d}, category : {dir_list[dir_idx].split("_")[1]} \n'

x = dt.datetime.now()
slack.chat_postMessage(
    channel=channel_id,
    blocks=[
      {
			"type": "section",
			"text": {
				"type": "plain_text",
				"text": tmp,
				"emoji": True
			}
		},
		{
			"type": "section",
			"text": {
				"type": "plain_text",
				"text": f"{x.hour}시 {x.minute}분 이미지 전처리 완료",
				"emoji": True
			}
		}
    ]
)



# 모델 학습 --------------------------------------------------------------------------
smm(model_name,INPUT_IMAGE_PATH,MODEL_PATH,KEY_PATH, KEY_NAME)

x = dt.datetime.now()
slack.chat_postMessage(
    channel=channel_id,
    blocks=[
		{
			"type": "section",
			"text": {
				"type": "plain_text",
				"text": f"{x.hour}시 {x.minute}분 {model_name} 학습 완료",
				"emoji": True
			}
		}
    ]
)



image_upload(slack, channel_id,"./Classification_Probability.png")

image_upload(slack, channel_id,"./Classification_Confidence_Mean.png")

image_upload(slack, channel_id,"./Classification_Confidence_Mean_All.png")

# config.json 파일 내용으로 firebase 프로젝트 업데이트 --------------------------------
headers = {
    'Authorization': 'Bearer ' + _get_access_token()
  }
resp = requests.get(REMOTE_CONFIG_URL, headers=headers)

_publish(format(resp.headers['ETag']))

x = dt.datetime.now()
slack.chat_postMessage(
    channel=channel_id,
    blocks=[
		{
			"type": "section",
			"text": {
				"type": "plain_text",
				"text": f"{x.hour}시 {x.minute}분 {model_name} 업로드 완료",
				"emoji": True
			}
		}
    ]
)