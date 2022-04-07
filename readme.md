# **BODA / JUDA**

<img src="/uploads/25e11bfce5f4ea8897ce6aa91ad4f819/image.png" alt="BODA-logos_transparent_흰배경" width="400" height="400"/>
<img src="/uploads/1485627ac32411e36128e1670f63843c/image.png" alt="JUDA-logos_transparent_흰배경" width="400" height="400"/>

 ### **세상을 이해하는 또다른 눈, BODA** / **세상을 보여주는 경험, JUDA**<br>
> **BODA** <br> 
시각장애인 분들이 혼자서도 자유로운 쇼핑이 가능할 수 있도록 눈이 되겠습니다. <br>
핸드폰 카메라만을 가지고도 여러 종류의 과자 뿐만 아니라 음료까지 혼자 구매할 수 있습니다. <br>
**JUDA**<br>
여러분들도 핸드폰만 있다면 누군가의 눈이 되어 줄 수 있습니다.<br>
시각장애인 분들의 눈이 되어주세요.
> 

📷 [프로젝트 UCC](https://drive.google.com/file/d/11b7lxRec43bNLdPAZlp0RkygJLadYXoc/view?usp=sharing)

<br>

## **프로젝트 목차**

- BODA / JUDA
    - 프로젝트 목차
    - 1️⃣ [프로젝트 소개](#1️⃣-프로젝트-소개)
        - [기술 스택](#기술-스택)
        - [디자인](#디자인)
    - 2️⃣ [프로젝트 파일 구조](#2️⃣-프로젝트-파일-구조)
        - Backend Server
        - Boda(app1)
        - Juda(app2)
    - 3️⃣ [프로젝트 산출물](#3️⃣-프로젝트-산출물)
    - 4️⃣ [프로젝트 결과물](#4️⃣-프로젝트-결과물)

<br><br>

## **1️⃣ 프로젝트 소개**

- 일정: 2022.02.22 ~ 2022.04.08 (총 7주)
- 인원 (총 6인)
    - 김유진: 팀장, AI, firebase, AWS EC2
    - 김민수: Android
    - 박동진: Backend, AWS EC2
    - 박상현: Android
    - 서상용: AWS EC2, firebase, Android
    - 임다훈: Backend, AWS EC2
<br>

### **기술 스택**

1. 이슈 관리 : JIRA
2. 형상 관리 : Gitlab
3. 커뮤니케이션 : Notion, Mattermost
4. 디자인 : Figma
5. UCC : Movavi Video Editor Plus 2020
6. CI/CD : Jenkins
7. 개발 환경
- IDE
    - IntelliJ : intellij IDEA 2021.3.1
    - Android Studio : Bumblebee | 2021.1.1 Patch 2 for Windows 64-bit
- OS : Windows 10, Monterey 12.3 (Mac OS)
- Database : MySQL
- Web Server : AWS EC2 (MobaXterm)
    - Ubuntu 20.04.4 LTS
    - Nginx
- GPU Server
    - Ubuntu 20.04.1 LTS

8. 상세 사용

- Web Server
    - Java : 11
    - Spring Boot : 2.5.10
    - Spring Boot Gradle : 7.4
    - lombok, gson 2.8.9, querydsl 5.0.0, swagger 2.9.2, google-api-client 1.32.1,
    jjwt 0.11.2, slack-api 1.20.1, bolt 1.20.1, okhttp3 4.9.1, qlrm 3.0.4, gcp 3.0.0
- GPU Server
    - anaconda python : 3.8.8
    - pip : 22.0.4
    - opencv-opencv-python : 4.5.5.64
    - tensorflow : 2.8.0, tensorflow-gpu : 2.8.0, tflite_model_maker : 0.3.4,
    firebase_admin : 5.2.0, oauth2client : 4.1.3, seaborn : 0.11.2
- Android
    - Android Studio
        - Build #AI-211.7628.21.2111.8193401, built on February 17, 2022
        - Runtime version: 11.0.11+0-b60-7772763 aarch64
        - VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
    - Android Gradle plugin version: 7.1.2
    - Gradle version: 7.4.1
    - Compile sdk version: 31
<br>

### **디자인**

**app1 ( BODA )**

<br>
<img src="/uploads/52f44d05ef78d92f20171abb922519f5/image.png" alt="BODA_영상" width="400" height="700" />
<br><br>

**app2 ( JUDA )**
<br>
![image](/uploads/fb64c81e44772a3eafc8692570a76196/image.png)
<br><br>

## **2️⃣ 프로젝트 파일 구조**

### Backend Server

```
└─ com
	└─ aeye
		└─ thirdeye
			├─ api
			├─ config
			│  ├─ Auth
			│  ├─ filter
			│  └─ interceptor
			├─ controller
			├─ dto
			│  ├─ auth
			│  ├─ property
			│  ├─ request
			│  └─ response
			├─ entity
			│  └─ auth
			├─ exception
			├─ info
			│  └─ impl
			├─ repository
			├─ scheduler
			├─ service
			│  └─ auth
			├─ token
			└─ utils
```
<br>

### **Android (App1)**

```
main
	├─assets
  │  ├─custom_models
	│	 └─html
  ├─ java
  │   └─ com
  │       └─ aeye
  │           └─ thirdeye
  │               ├─objectdetector
  │               ├─preference
  │               ├─sound
  │               ├─tts
  │               └─vibrator
  └─res
      ├─drawable
      ├─layout
      ├─menu
      ├─mipmap-anydpi-v26
      ├─mipmap-hdpi
      ├─mipmap-mdpi
      ├─mipmap-xhdpi
      ├─mipmap-xxhdpi
      ├─mipmap-xxxhdpi
      ├─raw
      └─values
```
<br>

### **Android (App2)**

```
main
├─java
│  └─com
│      └─aeye
│          └─nextlabel
│              ├─feature
│              │  ├─camera
│              │  ├─common
│              │  ├─labeling
│              │  ├─main
│              │  │  ├─community
│              │  │  ├─home
│              │  │  └─profile
│              │  └─user
│              ├─global
│              ├─model
│              │  ├─dto
│              │  └─network
│              │      ├─api
│              │      └─response
│              ├─repository
│              └─util
└─res
    ├─drawable
    ├─drawable-v24
    ├─layout
    ├─menu
    ├─mipmap-anydpi-v26
    ├─mipmap-hdpi
    ├─mipmap-mdpi
    ├─mipmap-xhdpi
    ├─mipmap-xxhdpi
    ├─mipmap-xxxhdpi
    ├─navigation
    ├─values
    └─values-night
```
<br><br>

## **3️⃣ 프로젝트 산출물**

- [Notion ( 공유 / 메모 )](https://www.notion.so/aeye/AEye-473dcc17ae144a3fb7e23d0c548d0f62)
- [컨벤션](./docs/컨벤션.md)
- [기획서](./docs/프로젝트 계획서.pdf)
- [요구사항 명세서](./docs/요구사항 명세서.md)
- [시퀀스 다이어그램](./docs/시퀀스다이어그램.md)
- [아키텍처](./docs/아키텍처.md)
- [와이어프레임(디자인)](./docs/와이어프레임.md)
- [ERD](./docs/ERD.md)
- [성능테스트](./docs/성능테스트.md)
- [테스트케이스 문서](./docs/테스트케이스.pdf)
<br><br>

## **4️⃣ 프로젝트 결과물**

- [포팅메뉴얼](./exec/AEye_포팅매뉴얼.pdf)
- [시연 시나리오](./exec/AEye_시연시나리오.pdf)
- [발표자료]
