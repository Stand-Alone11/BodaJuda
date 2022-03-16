import cv2
import numpy as np
from matplotlib import pyplot as plt

path = 'C:/Users/deter/Downloads/30_cate-20220316T055655Z-001/30_cate'
img_name = '/농심매운새우깡_90G/10093_0_m_1.jpg'
full_path = path + img_name
# print(full_path)
img_array = np.fromfile(full_path, np.uint8)
# print("img_array :")
# print(img_array)
 

# image = cv2.imread(img_array, cv2.IMREAD_COLOR)
image = cv2.imdecode(np.fromfile(full_path, dtype=np.uint8), cv2.IMREAD_COLOR)
# matplotlib 에서는 이미지 포맷을 바꿔줘야 보인다.
image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR) 
print('chk2')


# plt.plot(image)
# plt.figure(figsize=(6,8))
# plt.show(block = True)




image = cv2.resize(image, dsize=(640,640), interpolation=cv2.INTER_AREA)
h, w, c = image.shape

print('width:  ', w)
print('height: ', h)
print('channel:', c)

image1 = cv2.resize(image, dsize=(640, 480), interpolation=cv2.INTER_AREA)
image2 = cv2.resize(image, dsize=(0, 0), fx=0.4, fy=0.4,interpolation=cv2.INTER_AREA)

plt.subplot(331)
plt.imshow(image1)
# cv2.imshow("새우깡", image1)
plt.subplot(332)
plt.imshow(image2)
# cv2.imshow("aaa", image2)

image3 = cv2.rotate(image, cv2.ROTATE_90_CLOCKWISE)
# cv2.imshow("clock", image3)
plt.subplot(333)
plt.imshow(image3)

image4 = cv2.rotate(image, cv2.ROTATE_180)
cv2.imshow("clock", image4)
plt.subplot(334)
plt.imshow(image4)

image5 = cv2.rotate(image, cv2.ROTATE_90_COUNTERCLOCKWISE)
# cv2.imshow("clock", image5)
plt.subplot(335)
plt.imshow(image5)


matrix =cv2.getRotationMatrix2D((h/2,w/2), 60, 1)
image6 = cv2.warpAffine(image, matrix, (h, w))
plt.subplot(336)
plt.imshow(image6)


plt.show()
plt.pause(1)
plt.close()

cv2.waitKey() # 키보드 입력이 들어올 때까지 창을 유지
cv2.destroyAllWindows() # 모든 윈도우 창을 제거