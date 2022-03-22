import os
import cv2
import numpy as np
from matplotlib import pyplot as plt


path = '/home/team1/AEye/data/image/snack_4'
# path = 'C:/Users/deter/Desktop/snack_4'

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
      # 이미지 값 /255 로 정규화...? 여기서 하면 이미지파일 생성 단계에서 문제가 발생한다. 학습시키기 전에 정규화를 거치자!!!
      image = cv2.imdecode(np.fromfile(path+'/'+i+'/'+j, dtype=np.uint8), cv2.IMREAD_COLOR)
      
      h, w, c = image.shape
      s = 0.2
      if(h>w):
         tb = int(h*s)
         rl = int((h-w)/2 + h*s)
         image = cv2.copyMakeBorder(image, tb,tb, rl,rl,cv2.BORDER_CONSTANT ,0 )
         h = int(h*1.4)
         w = h
      else: 
         tb = int((w-h)/2 + w*s)
         rl = int(w*s)
         image = cv2.copyMakeBorder(image, tb,tb, rl,rl,cv2.BORDER_CONSTANT ,0   )
         w = int(1.4*w)
         h = w
      

      createFolder(path + '_rota/'+i)

      for degree in range(0,361,5):
         
         matrix = cv2.getRotationMatrix2D((h/2,w/2), degree, 1)
         rota_image = cv2.warpAffine(image, matrix, (h, w))
         rota_image = cv2.resize(rota_image,[224,224])
         new_img_name = path + '_rota/' + i +'/rota'+str(degree)+'_'+j
         # print(new_img_name)
         # plt.imshow(rota_image)
         # plt.show()
         # plt.pause(1)
         # plt.close()
         extension = os.path.splitext(new_img_name)[1]
         result, encoded_img = cv2.imencode(extension, rota_image)
         # print(encoded_img )
         if result:
            with open(new_img_name, mode='w+b') as f:
               encoded_img.tofile(f)
         # cv2.imwrite(path + '_rota/' + i +'/rota'+str(degree)+'_'+j,rota_image)
      # os.remove(path+'/'+i+'/'+j)