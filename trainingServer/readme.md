# id & pw

team1

ssafy711820#

# 버전

ubuntu 20.04.1 LTS

python 3.8.10





# 명령어

*  ubuntu 권한 확인 : ls -al
*  ubuntu 버전 확인 : lsb_release -a
*  python 버전 확인 : python3 --version
*  패키지 매니저 목록 업데이트 : sudo apt-get update
*  패키지 업그레이드 : sudo apt-get upgrade
*  하드웨어 정보 확인 :  sudo lshw -short | egrep 'process|display'
*  가상환경 생성 : python3 -m venv {가상환경 이름}
*  가상환경 실행 : source ./{가상환경 이름}/bin/activate    // 디렉토리 위치는 가상환경 상위
*  접속하고 anaconda 초기화를 해야함 : source ~/.bashrc
*  아나콘다 가상환경 목록 확인 : conda info --envs 

# 이슈 정리

2022년 03월 15일

[error] venv 가상환경 생성 시 activate.bat 파일 생성 안되는 문제

[solved] sudo apt install python3-venv 재설치 후 문제 해결

2022년 03월 14일

[error] GPU 서버에 python 설치를 위해서 목록 업데이트 도중 sudo 권한이 없음을 확인

[solved] root 권한 신청해서 해결

[error] sudo apt-get upgrade 진행후 gpu 드라이버와 라이브러리 버전 불일치 문제 발생

[solved] [코치님 도움](https://develop-ds.tistory.com/m/entry/Error-Failed-to-initialize-NVML-Driverlibrary-version-mismatch)으로 문제 해결

