import os
import json
import firebase_admin
from firebase_admin import ml


with open("./keys/aeye01-firebase-adminsdk-als38-ca1cc3f0d6.json",'r') as f:
   uploaded = json.load(f)
# uploaded = files.upload()
print(uploaded['type'])

for fn in uploaded.keys():
  print('User uploaded file "{name}" with length {length} bytes'.format(
      name=fn, length=len(uploaded[fn])))
#   os.environ["GOOGLE_APPLICATION_CREDENTIALS"]='/content/' + fn
  projectID = fn.rsplit("-firebase")[0]
  firebase_admin.initialize_app(
      options={'projectId': projectID, 
               'storageBucket': projectID + '.appspot.com' })


# import json
# import os
# # import firebase_admin
# # from firebase_admin import ml

# with open("./keys/aeye01-firebase-adminsdk-als38-ca1cc3f0d6.json",'r') as f:
#    key_data = json.load(f)
# #    os.environ["GOOGLE_APPLICATION_CREDENTIALS"]='/content/' + f

# for a in key_data.keys():
#     print(a)

# print(json.dumps(key_data, indent="\n"))
# print(key_data['name'])

# import firebase_admin
# from firebase_admin import credentials
# from firebase_admin import storage

# cred = credentials.Certificate('./keys/aeye01-firebase-adminsdk-als38-ca1cc3f0d6.json')
# firebase_admin.initialize_app(cred, {
#     'storageBucket': 'aeye01.appspot.com'
# })

# bucket = storage.bucket()





# from google.cloud import storage
# client = storage.Client()

# bucket = storage.Bucket(client, "my-bucket-name", user_project="my-project")
# all_blobs = list(client.list_blobs(bucket))
# # for bucket in all_blobs:
# #     print(bucket)
# for bucket in client.list_buckets():
#     print(bucket)




