import logging
# logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger('test')
import os
from slack_sdk import WebClient
import datetime as dt
from slack_sdk.errors import SlackApiError



TOKEN = "xoxb-3304749042240-3295636403457-PND6zGxAgmSk7NhoQYSnINeF"


def get_channel_id(client, channel_name):
    """
    슬랙 채널ID 조회
    """
    # conversations_list() 메서드 호출
    result = client.conversations_list()
    # 채널 정보 딕셔너리 리스트
    channels = result.data['channels']
    # 채널 명이 'test'인 채널 딕셔너리 쿼리
    channel = list(filter(lambda c: c["name"] == channel_name, channels))[0]
    # 채널ID 파싱
    channel_id = channel["id"]
    return channel_id

def image_upload(client,channel_id,image_file_path ):
    file_name = image_file_path
    # ID of channel that you want to upload file to
    # channel_id = "C12345"

    try:
        # Call the files.upload method using the WebClient
        # Uploading files requires the `files:write` scope
        result = client.files_upload(
            channels=channel_id,
            initial_comment=image_file_path.split('/')[1].split('.')[0],
            file=file_name,
        )
        # Log the result
        logger.info(result)

    except SlackApiError as e:
        logger.error("Error uploading file: {}".format(e))

