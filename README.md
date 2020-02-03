![logo](./readme_images/logo.png)

# Penstagram
Penstagram(PENpal + inSTAGRAM) is a penpal app, where you can find new friends who are interested in them.

펜스타그램은 사진을 업로드하고, 업로드한 사진을 통해 새로운 외국인 친구들을 만들 수 있는 펜팔 앱입니다.

## What is it?
2019년 겨울 몰입캠프 2주차 과제로 제작한 안드로이드 어플리케이션입니다.

## Used
/*
Kotlin
Android Studio
MongoDB
Nodejs server(?????)
*/

## Features
### 1. Register and log in with Facebook
![pic1-1](./readme_images/pic1-1.png)
![pic1-3](./readme_images/pic1-3.png)
![pic1-4](./readme_images/pic1-4.png)
![pic1-5](./readme_images/pic1-5.png)

// Facebook login 기능 설명

### 2. Upload your photos
![pic2-1](./readme_images/pic2-1.png)

갤러리 탭에서는 RecyclerView로 펜스타그램을 이용하는 다른 사용자들이 올린 사진들을 볼 수 있습니다. (현재 테스트 버전에서는 사용자와 사진 수가 적어 각 사용자가 첫 번째로 올린 사진들을 모두 보여주고 있으나, 랜덤하게 일정 개수만 보여주는 방법으로 수정될 예정입니다.) <br>
우측 하단에 위치한 floating action button을 누르면 upload 버튼과 refresh 버튼을 열고 닫을 수 있습니다.<br>
* upload 버튼을 누르면 사용자 디바이스의 사진첩으로 이동하여 사용자가 올리고 싶은 사진을 선택할 수 있습니다. 선택된 사진은 서버를 통해 데이터베이스에 저장됩니다. (본인이 올린 사진들을 확인할 수 있는 기능은 구현 예정입니다.) <br>
* refresh 버튼을 누르면 서버를 통해 데이트베이스에 새로 추가된 사진이 있는지 확인한 후, 있다면 그 사진들 다운로드하여 사진들의 순서를 뒤섞어 갤러리를 재구성합니다. <br>

### 3. Check other users' photos and add them as friends
![pic3-1](./readme_images/pic3-1.png)
![pic3-2](./readme_images/pic3-2.png)

갤러리 탭의 RecyclerView는 사진들의 썸네일만을 보여줍니다. 각 썸네일을 클릭하면 해당 사진에 대한 activity로 이동하여 사진을 올린 사용자에 대한 정보 (이름, 프로필사진, 상태메시지, 국가, 해시태그)와 그 사진 전체의 ImageView를 보여줍니다. <br>
하단의 친구추가 버튼을 누르면 사진을 올린 사용자와 친구를 맺을 수 있고, 자신의 친구관계는 연락처 탭에서 확인할 수 있습니다. // ????????????????친구관계가 나아 친구목록이나아?<br>
// 여기서 연락처 탭 설명해주세요,, <br>
// 채팅 버튼 누르면 채팅ㅇ으로 연결된다는 거도 써야하는데 이거는 밑에 4번에서 언급할 거 같아서 어떻게설명해야할지모르겠네 이거도우민이가쓰자 ^^....


### 4. Chat with your friends
![pic4-1](./readme_images/pic4-1.png)
![pic4-2](./readme_images/pic4-2.png)

// 연락처 탭에서 사람 누르면 연결되는 기능, 채팅 탭, 채팅방 기능 설명

### 5. Enhance your languae skills
![pic5-1](./readme_images/pic5-1.png)
![pic5-2](./readme_images/pic5-2.png)

// 구글번역 API 부분 설명
