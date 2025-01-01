### 1. 대기열 토큰 발급 API
: 사용자가 대기열에 추가되고, 해당 대기열의 고유 토큰을 발급받습니다.

POST /queue/token

[요청]
- URL: /queue/token
- HTTP 메서드: POST
- 헤더:
Content-Type: application/json
- 본문 (Request Body) 

 {
  "userId": "user-1234" 
 }


[응답]
- HTTP 상태 코드: 200 OK (성공), 400 Bad Request (잘못된 요청)
- 본문 (Response Body)

 {
  "status": "success",
  "message": "Token issued successfully",
  "data": {
    "queueToken": "abc123-token",
    "position": 1,
    "estimatedWaitTime": 300
   }
 }

* 필드 설명:
status: 요청 처리 상태 (success 또는 error).
message: 상태 메시지 (예: "Token issued successfully").
data:
queueToken: 발급된 고유 대기열 토큰.
position: 사용자의 현재 대기열 내 위치.
estimatedWaitTime: 대기 예상 시간 (초 단위).
