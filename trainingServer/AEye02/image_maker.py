import os
import cv2
import numpy as np
import math

# path = '/home/team1/AEye/data/image/snack_4'
# path = 'C:/Users/deter/Desktop/snack_4'

# print(os.listdir(path))

def createFolder(directory):
    try:
        if not os.path.exists(directory):
            os.makedirs(directory)
    except OSError:
        print ('Error: Creating directory. ' +  directory)

def image_maker(path):
   list = dict()
   a = 0
   for i in os.listdir(path):
      print(path+'/'+i +" 경로 이미지 전처리 중...")
      list[a] = i
      a += 1
      for j in os.listdir(path+'/'+i):
         # 이미지 값 /255 로 정규화...? 여기서 하면 이미지파일 생성 단계에서 문제가 발생한다. 학습시키기 전에 정규화를 거치자!!!
         image = cv2.imdecode(np.fromfile(path+'/'+i+'/'+j, dtype=np.uint8), cv2.IMREAD_COLOR)
         
         h, w, c = image.shape
         s = (1-(1/math.sqrt(2)))/2
         if(h>w):
            tb = int(h*s)
            rl = int((h-w)/2 + h*s)
            image = cv2.copyMakeBorder(image, tb,tb, rl,rl,cv2.BORDER_CONSTANT ,0 )
            h = int(h*(1+s*2))
            w = h
         else: 
            tb = int((w-h)/2 + w*s)
            rl = int(w*s)
            image = cv2.copyMakeBorder(image, tb,tb, rl,rl,cv2.BORDER_CONSTANT ,0   )
            w = int(w*(1+s*2))
            h = w
         
         createFolder(path + '_rota/'+i)

         
         # for c in range(4):
         #    new_img_name = path + '_rota/' + i +'/crop'+str(c)+'_'+j
         #    if os.path.isfile(new_img_name):
         #       continue
         #    cxs = 0
         #    cxe = w
         #    cys = 0
         #    cye = h
         #    if c < 2:
         #       cys = int(h * 0.1)
         #       cye = int(h * 0.7)
         #    else:
         #       cys = int(h * 0.3)
         #       cye = int(h*0.9)
         #    if (c % 2 == 0):
         #       cxs = int(w*0.1)
         #       cxe = int(w*0.7)
         #    else:
         #       cxs = int(w*0.3)
         #       cxe = int(w*0.9)

         #    crop_image = image[cys:cye,cxs:cxe]
         #    extension = os.path.splitext(new_img_name)[1]
         #    result, encoded_img = cv2.imencode(extension, crop_image)
         #    if result:
         #       with open(new_img_name, mode='w+b') as f:
         #          encoded_img.tofile(f)

         for mag in range(4):
            new_img_name = path + '_rota/' + i +'/mag'+str(mag)+'_'+j
            if os.path.isfile(new_img_name):
               continue
            mxs = int(w*(0.1 + 0.1 * mag ))
            mxe = int(w*(0.9 - 0.1 * mag ))
            mys = int(h*(0.1 + 0.1 * mag ))
            mye = int(h*(0.9 - 0.1 * mag ))
           
            mag_image = image[mys:mye,mxs:mxe]
            extension = os.path.splitext(new_img_name)[1]
            result, encoded_img = cv2.imencode(extension, mag_image)
            if result:
               with open(new_img_name, mode='w+b') as f:
                  encoded_img.tofile(f)

         
         # for degree in range(0,361,3):
         #    new_img_name = path + '_rota/' + i +'/rota'+str(degree)+'_'+j
         #    if os.path.isfile(new_img_name):
         #       continue
         #    matrix = cv2.getRotationMatrix2D((h/2,w/2), degree, 1)
         #    rota_image = cv2.warpAffine(image, matrix, (h, w))
         #    rota_image = cv2.resize(rota_image,[224,224])
         #    extension = os.path.splitext(new_img_name)[1]
         #    result, encoded_img = cv2.imencode(extension, rota_image)
         #    if result:
         #       with open(new_img_name, mode='w+b') as f:
         #          encoded_img.tofile(f)
                  
         # os.remove(path+'/'+i+'/'+j)
   return list