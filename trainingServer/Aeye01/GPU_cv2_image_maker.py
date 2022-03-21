import os
import cv2
import numpy as np
from matplotlib import pyplot as plt


path = '/home/team1/AEye/data/image/16_category'

# print(os.listdir(path))

def createFolder(directory):
    try:
        if not os.path.exists(directory):
            os.makedirs(directory)
    except OSError:
        print ('Error: Creating directory. ' +  directory)

for i in os.listdir(path):
   print(path+'/'+i +"경로 이미지 작업 중...")
   for j in os.listdir(path+'/'+i):
      # 이미지 값 /255 로 정규화...?
      image = cv2.imdecode(np.fromfile(path+'/'+i+'/'+j, dtype=np.uint8), cv2.IMREAD_COLOR)/255
      createFolder(path + '_rota/'+i)
      h, w, c = image.shape
      for degree in range(0,361,5):
         matrix = cv2.getRotationMatrix2D((h/2,w/2), degree, 1)
         rota_image = cv2.warpAffine(image, matrix, (h, w))
         new_img_name = path + '_rota/' + i +'/rota'+str(degree)+'_'+j
         print(new_img_name)
         extension = os.path.splitext(new_img_name)[1]
         result, encoded_img = cv2.imencode(extension, rota_image)
         if result:
            with open(new_img_name, mode='w+b') as f:
               encoded_img.tofile(f)
         # cv2.imwrite(path + '_rota/' + i +'/rota'+str(degree)+'_'+j,rota_image)
      os.remove(path+'/'+i+'/'+j)