import argparse
import requests
import io

from oauth2client.service_account import ServiceAccountCredentials

# 참고 링크 https://github.com/firebase/quickstart-python/tree/master/config

PROJECT_ID = 'aeye-9c1ee'
BASE_URL = 'https://firebaseremoteconfig.googleapis.com'
REMOTE_CONFIG_ENDPOINT = 'v1/projects/' + PROJECT_ID + '/remoteConfig'
REMOTE_CONFIG_URL = BASE_URL + '/' + REMOTE_CONFIG_ENDPOINT
SCOPES = ['https://www.googleapis.com/auth/firebase.remoteconfig']

def _get_access_token():
  """Retrieve a valid access token that can be used to authorize requests.
  :return: Access token.
  """
  credentials = ServiceAccountCredentials.from_json_keyfile_name(
      'aeye-9c1ee-firebase-adminsdk-9glw7-8f233a41fb.json', SCOPES)
  access_token_info = credentials.get_access_token()
  return access_token_info.access_token

def _get():
  """Retrieve the current Firebase Remote Config template from server.
  Retrieve the current Firebase Remote Config template from server and store it
  locally.
  """
  headers = {
    'Authorization': 'Bearer ' + _get_access_token()
  }
  resp = requests.get(REMOTE_CONFIG_URL, headers=headers)

  if resp.status_code == 200:
    with io.open('config.json', 'wb') as f:
      f.write(resp.text.encode('utf-8'))


_get()