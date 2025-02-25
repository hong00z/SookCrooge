# SookCrooge
![스크린샷 2025-02-02 221526](https://github.com/user-attachments/assets/42590195-847e-4629-a900-7275fa9e8571)

가계부 어플에 ‘거지방’ 기능을 합친 어플, 숙크루지를 제작하는 프로젝트입니다.

## 개발 기간
2023.11.03 ~ 2023.12.21

## 개발 환경
![Android Studio](https://img.shields.io/badge/android%20studio-346ac1?style=for-the-badge&logo=android%20studio&logoColor=white)![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)![Firebase](https://img.shields.io/badge/firebase-a08021?style=for-the-badge&logo=firebase&logoColor=ffcd34) <br>
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)![Figma](https://img.shields.io/badge/figma-%23F24E1E.svg?style=for-the-badge&logo=figma&logoColor=white)
- Android Studio
- Kotlin
- Firebase
- Github, Figma

---

## 기능별 화면
### 1. 로그인/회원가입
- 초기화면
  
  ![image](https://github.com/user-attachments/assets/9ff9585c-bc38-43c3-9c14-7e8c2ba2942d)

  처음 어플을 구동했을 때 나오는 화면입니다. 로그인, 회원 가입, 네이버 가입을 선택할 수 있습니다.
  
- 회원가입 화면
  
  ![image](https://github.com/user-attachments/assets/3604c10d-63cd-4240-bc8f-744a6310cb9e)
  ![image](https://github.com/user-attachments/assets/c84fa3f2-764c-4ad6-838f-9b6fa888c462)

  초기화면에서 회원가입 버튼을 누르면 이동됩니다. 이름, 닉네임, 이메일, 비밀번호를 입력하고 회원가입 버튼을 누르면 가입됩니다. 닉네임과 이메일은 중복이 있는지 확인하며, 비밀번호는 영문과 숫자가 섞인 8자리 이상이어야 합니다. <br>
  액션바의 뒤로가기 버튼을 누르면 초기화면으로 돌아가며, 회원가입을 마치면 로그인 화면으로 이동합니다. <br>
  회원가입이 완료되면 다음과 같이 파이어베이스에 저장됩니다.
  
  ![image](https://github.com/user-attachments/assets/8ba0f109-6284-426c-a9fb-243c2365c901)

- 로그인 화면

  ![image](https://github.com/user-attachments/assets/4c5d9fd2-a247-4eaa-bc5f-57e20ad86984)

  회원 가입을 마치거나, 초기화면에서 로그인 버튼을 누르면 이동하는 화면입니다. 이메일과 비밀번호를 입력하여 로그인을 하거나, 비밀번호를 재설정하거나, 네이버로 로그인 할 수 있습니다. <br>

- 비밀번호 재설정 화면

  ![image](https://github.com/user-attachments/assets/e87cf063-7b7b-439b-8614-81bd548ee637)

  이메일을 입력한 후 인증번호 발송 버튼을 눌러 이메일로 인증번호를 전송합니다. 인증번호를 맞게 입력하지 않으면 인증번호가 올바르지 않다는 메세지가 출력됩니다.

  ![image](https://github.com/user-attachments/assets/8e097372-509c-4e72-b468-a015705e7c2a)
  ![image](https://github.com/user-attachments/assets/8c3c15f4-5ce9-4396-9263-ed1a3162a81c)

  위와 같이 입력한 이메일로 메일이 전송되며, 맞게 입력하면 인증이 완료되었다는 문구가 뜹니다. 그 후 새 비밀번호와 비밀번호를 재입력하면 비밀번호가 변경됩니다.

  ![image](https://github.com/user-attachments/assets/07bfa6db-fe0f-4e79-84c4-a855ae4c1581)

  비밀번호 변경 버튼을 누르면 위와 같이 파이어베이스에 변경된 값이 저장됩니다.

- 네이버 로그인 화면

  ![image](https://github.com/user-attachments/assets/ad364964-93fd-4dd8-b768-44e0fcd932a4)

  초기화면에서 네이버로 가입하기 버튼이나 로그인 화면에서 네이버로 로그인 버튼을 누르면 네이버로 연결됩니다. <br>
  네이버와 첫 연결이라면 다음과 같이 이름과 이메일 주소 제공 동의 화면이 뜹니다. 제공 항목에 체크 한 후 동의하기 버튼을 누르면 네이버로 로그인이 가능합니다. 이미 동의가 된 상태라면 앱의 메인화면으로 이동합니다. <br>
  (* 네이버 로그인 기능은 개발자 ID만 가능)

### 2. 메인 기능 - 가계부 기록
- 메인 화면
  
  로그인이 되면 다음과 같은 메인화면으로 이동합니다. 메인화면에는 전체 회원 중 이번 달 가장 많이 좋아요 이모지를 많이 받은 절약과 싫어요 이모지를 많이 받은 소비가 나타납니다. 하단에는 내 가계부, 회원 정보, 채팅방으로 이동하는 메뉴가 있습니다.

  ![image](https://github.com/user-attachments/assets/43954a35-dd8c-4060-922f-d20e6edcc6c8)

  이미 로그인된 상태에서 어플을 종료하면 이 화면에서부터 시작합니다.

- 내 가계부 화면
  
  ![image](https://github.com/user-attachments/assets/894b5210-e355-4927-9b47-025fc9af1422)

  메인화면의 하단 내 가계부 버튼을 누르면 이동하는 화면입니다. 실행하면 오늘 날짜에 해당하는 월의 달력이 보여지고, 달력의 왼쪽 화살표와 오른쪽 화살표를 눌러 월을 이동할 수 있습니다. 달력의 아래에는 해당 월의 총 소비금액과 전체 소비와 절약 내용을 볼 수 있습니다. <br>
  오른쪽 아래에 있는 플로팅 액션 버튼을 클릭해 가계부 추가하기 화면으로 이동할 수 있습니다.

- 가계부 추가하기 화면
  
  ![image](https://github.com/user-attachments/assets/1d3906a8-a0ee-43a0-8abb-6aa1adbc71a7)
  ![image](https://github.com/user-attachments/assets/ed1322e2-6c00-44c5-bdfe-5af4ab1dbc33)

  내 가계부 화면에서 + 플로팅 버튼을 누르면 이동하는 화면입니다. 다음과 같이 저축 및 지출 선택, 사용 금액, 분류, 메모, 날짜를 입력한 후 확인 버튼을 누르면 내 가계부의 내용에 추가됩니다.

### 3. 회원 정보 수정
- 프로필 네비게이션 바
  
  ![image](https://github.com/user-attachments/assets/17248d46-0af5-44e9-918e-cf840a885401)

  메인 화면에서 하단의 가운데 프로필 버튼을 누르면 프로필 네비게이션 바가 나타납니다. 프로필 사진을 변경하거나, 정보 수정 페이지로 이동하거나, 로그아웃 할 수 있습니다. <br>
  사진 우하단에 있는 카메라 버튼을 클릭하면 사용자로부터 권한을 허용받고 갤러리 앱으로 넘어갑니다. 사진이 선택되면 프로필이 변경됩니다.

- 정보 수정 화면
  
  ![image](https://github.com/user-attachments/assets/6a7dcf04-ec70-44f8-a6cb-3245025d0368)
  ![image](https://github.com/user-attachments/assets/2aa20f7f-189f-4fe2-8911-08bfa3a0d4ab)

  프로필 네비게이션 바에서 정보 수정 버튼을 누르면 이동 가능합니다. <br>
  이름과 닉네임을 변경할 수 있고, 현재 비밀번호를 입력하면 수정할 수 있습니다. 닉네임은 중복이 있는지 확인하며 중복된 닉네임이 있다면 수정되지 않습니다. <br>
  회원 탈퇴 버튼을 누르면 회원 탈퇴를 묻는 다이얼로그가 뜨고, 네 버튼을 누르면 파이어베이스 데이터베이스에서 정보가 삭제됩니다.

### 4. 메인 기능 - 채팅  
- 채팅방 화면

  ![image](https://github.com/user-attachments/assets/b43a8909-d1c1-4df2-ab46-d083132fed86)
  ![image](https://github.com/user-attachments/assets/1cbd9f1b-2cb8-4863-bedb-d03e09dd6b52)

  메인 화면의 하단 채팅방 버튼을 누르면 채팅방 화면으로 이동합니다. <br>
  모든 채팅방과 내가 참여 중인 채팅방 목록을 볼 수 있습니다. 채팅방을 클릭하면 채팅방에 입장할 수 있으며, 우상단 메뉴를 누르면 채팅방 개설이 가능합니다. <br>
  아래 예시는 채팅방 이름이 test인 방을 생성하였으며, 해당 채팅방이 채팅방 목록에 추가되는 것을 확인할 수 있습니다.

  ![image](https://github.com/user-attachments/assets/b8d51df7-088e-4ade-a4e7-2c4fa47b6a5e)
  ![image](https://github.com/user-attachments/assets/2727ee89-932a-4d36-be70-59cb41ec4217)

  내가 참여 중인 채팅방을 길게 누르면 채팅방에서 나가기가 가능합니다. <br>
  아래 예시는 test 채팅방을 나갔으며, 내 채팅방 목록에서 test 채팅방이 사라진 것을 확인할 수 있습니다.

  ![image](https://github.com/user-attachments/assets/acb7bea9-2bd3-493f-b0dc-e31bb3d08510)
  ![image](https://github.com/user-attachments/assets/03c25b12-de88-46bf-aa59-5764ce1e9e50)
  
- 채팅 화면

  ![image](https://github.com/user-attachments/assets/8477957d-d8ca-4d98-8446-fa348cf9fa56)

  채팅방 화면에서 각 채팅방을 선택하면 이동하는 화면입니다. 액션바에는 채팅방 이름이 표시되며, 그 아래에 전체 채팅 인원을 볼 수 있습니다. 각 유저를 클릭하면 해당 유저의 오늘 소비/절약 내용을 확인할 수 있으며, 가계부 이동하기 버튼을 눌러 해당 유저의 가계부로 이동할 수 있습니다. <br>
  메세지 입력 창에 메세지를 입력하고 전송 버튼을 누르면 채팅방에 내용이 전송됩니다.
  
- 다른 유저의 가계부

  ![image](https://github.com/user-attachments/assets/0436b521-7b93-4707-a9d8-338642f94818)
  
  채팅 화면에서 다른 유저의 프로필을 클릭한 후 가계부로 이동하기 버튼을 누르면 이동할 수 있습니다. 내 가계부 화면과 유사하지만 새 가계부를 추가할 수 없습니다. 좋아요 이모지와 싫어요 이모지를 클릭하여 해당 소비/절약을 피드백할 수 있습니다.
