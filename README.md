# 부하 테스트 계획 문서
## 1. 부하 테스트 대상 및 목적
### 목적
- 동시성 제어 검증: 대기열 시스템이 다수의 요청을 정상적으로 처리하는지 확인
- 성능 평가: 동시 접속자가 많을 때의 API 응답 시간, 처리량(Throughput), 오류율 확인
- 시스템 안정성 테스트: 예약, 결제, 대기열 기능이 높은 부하에서도 정상 동작하는지 검증
- 병목 지점 식별 및 최적화: 데이터베이스, 캐시(Redis), 대기열 큐 등에서 성능 저하가 발생하는 지점 파악
## 2. 테스트 시나리오
### (1) 대기열 토큰 발급 테스트
1-1) 목적: 다수의 사용자가 동시에 대기열 요청 시 정상적으로 발급되는지 확인
1-2) 테스트 조건:
- 1초에 1,000명의 사용자가 대기열 토큰 발급 요청
- 10,000명의 사용자가 1분 동안 지속적으로 요청
- 대기열의 응답 시간 및 처리 성공률 분석
### (2) 좌석 예약 요청 테스트
2-1) 목적: 여러 사용자가 동일한 좌석을 예약할 경우 동시성 제어가 정상적으로 동작하는지 검증
2-2) 테스트 조건:
- 동일한 좌석에 대해 100명의 사용자가 동시 요청
- 1초 동안 500명의 사용자가 좌석 예약 시도
- 예약 성공률 및 충돌 발생 여부 확인
### (3) 결제 요청 테스트
3-1) 목적: 예약된 좌석에 대한 결제가 정상적으로 수행되는지 확인
3-2) 테스트 조건:
- 1초에 200건의 결제 요청
- 5분 동안 5,000건의 결제 요청 발생
- 결제 실패율, DB 부하 분석
### (4) 예약 만료 시나리오
4-1) 목적: 결제가 이루어지지 않은 좌석이 정상적으로 만료되는지 확인
4-2) 테스트 조건:
- 100명이 좌석 예약 후 결제 없이 5분간 대기
- 5분 후 해당 좌석이 자동으로 반환되는지 검증
### (5) 장애 대응 시나리오
5-1) 목적: 특정 컴포넌트 장애 발생 시 시스템이 정상적으로 복구되는지 확인
5-2) 테스트 조건:
- Redis 장애 발생 시 대기열이 정상적으로 동작하는지 확인
- DB 응답 속도 지연 발생 시 예약 서비스의 영향 분석

## 3. 성능 측정 지표
- 응답 시간(Response Time): API의 평균 및 최대 응답 속도
- 처리량(Throughput): 초당 처리 가능한 요청 수
- 오류율(Error Rate): 요청 대비 실패율 분석
- CPU 및 메모리 사용률: Docker 컨테이너 리소스 사용률 확인
- DB 부하(Query Performance): 주요 SQL 쿼리의 실행 시간 분석

------------------------------------------------------------------------------------------------------------------------------------
# 부하테스트 스크립트

1️⃣ JMeter 설치
JMeter 공식 사이트에서 다운로드 후 설치.
설치 후 bin/jmeter.bat (Windows) 또는 bin/jmeter (Mac/Linux) 실행.

2️⃣ 테스트 계획 구성

🔹 유저 대기열 토큰 발급 테스트
Thread Group 추가

Number of Threads (Users): 1000 (1000명의 사용자)
Ramp-Up Period: 60 (1분 동안 점진적으로 증가)
Loop Count: 1 (한 번만 실행)
HTTP Request 추가

Server Name: localhost
Port: 8080
Method: POST
Path: /api/queue/token
Body Data (JSON 입력)
json
{ "userId": "${__UUID()}" }
Content-Type: application/json
Assertions 추가

Response Assertion 추가 → Text Response에서 "status": "success" 포함 확인

🔹 좌석 예약 요청 테스트
새로운 Thread Group 생성

Number of Threads (Users): 500
Ramp-Up Period: 10 (10초 동안 점진적 증가)
HTTP Request 추가

Method: POST
Path: /api/reserve
Body Data
json
{
  "userId": "${__UUID()}",
  "seatNumber": "${__Random(1,50)}",
  "date": "2025-02-25"
}

🔹 결제 요청 테스트
새로운 Thread Group 생성

Number of Threads (Users): 200
Ramp-Up Period: 5
Loop Count: 1
HTTP Request 추가

Method: POST
Path: /api/payment
Body Data
{
  "userId": "${__UUID()}",
  "seatNumber": "${__Random(1,50)}",
  "amount": 50000
}

3️⃣ JMeter 실행
GUI에서 실행
JMeter 실행 (bin/jmeter)
위 설정대로 구성 후 Start 버튼 클릭
CLI에서 실행
.jmx 파일 저장 (load_test.jmx)
터미널에서 실행:
sh
jmeter -n -t load_test.jmx -l result.jtl -e -o report
-n: GUI 없이 실행
-t: 실행할 .jmx 파일 지정
-l: 결과 로그 저장 (.jtl)
-e -o report: HTML 리포트 생성

-------------------------------------------------------------------------------------------------------------------------------------------------------

# 성능 분석, 병목 탐색 및 개선 방안, 장애 대응 문서

## 1️⃣ 성능 지표 분석
### 📌 주요 성능 지표
응답 시간 (ms)	최대 응답 시간 (ms)	성공률 (%)	TPS (Transactions Per Second)
## 2️⃣ 병목 탐색 및 원인 분석
### 🔹 문제 1: 좌석 예약 요청 시 응답 지연 발생
🔍 원인:
다수의 사용자가 같은 좌석을 요청하면서 DB의 락 경합 발생
예약 시 Pessimistic Lock 사용으로 인해 트랜잭션 대기 시간 증가
Redis 캐시가 없는 상태에서 예약 가능 좌석 조회 시 DB 부하 증가

✅ 개선 방안:
좌석 예약 상태 캐싱 적용
Redis를 활용해 예약 가능 좌석을 캐싱하여 DB 부하 감소
SETNX 또는 Sorted Set 기반 대기열 방식 활용
비관적 락 → 낙관적 락(Optimistic Lock) 변경 고려
다수의 사용자가 같은 좌석을 요청할 경우, 실패한 요청만 재시도하도록 개선
### 🔹 문제 2: 결제 처리 응답 시간 증가
🔍 원인:
결제 요청 시 DB 트랜잭션 범위가 큼
좌석 상태 업데이트와 결제 내역 저장이 한 트랜잭션에서 처리됨

✅ 개선 방안:
비동기 결제 처리 적용

결제 API를 Kafka Producer로 변경하여 비동기 처리
DB 트랜잭션 시간 단축
DB 테이블 인덱스 최적화

결제 내역 테이블 조회 최적화를 위해 인덱스 추가
### 🔹 문제 3: 서버 리소스 부족으로 인한 응답 지연
🔍 원인:
CPU 사용률 80% 이상 지속
GC(Garbage Collection) 지연 발생

✅ 개선 방안:
Docker 리소스 스케일링

docker-compose.yml에서 CPU & Memory 제한 조정
yaml
services:
  app:
    deploy:
      resources:
        limits:
          cpus: "2"
          memory: "2GB"
서버 증설 및 부하 분산

Nginx를 이용한 로드 밸런싱
Auto Scaling 적용 고려
## 3️⃣ (가상) 장애 대응 문서
### 📌 장애 시나리오 1: Redis 장애 발생
🛑 현상:

대기열 시스템이 정상적으로 동작하지 않음
좌석 예약 시 DB 부하 증가

🛠 대응 방안:

Redis Sentinel 구성으로 장애 감지 및 자동 복구
Fallback Mechanism 적용
Redis 장애 시 DB 조회로 전환 (성능 저하 고려)
### 📌 장애 시나리오 2: 데이터베이스 연결 장애
🛑 현상:

좌석 예약 및 결제 요청 실패
API 응답 시간 초과

🛠 대응 방안:

DB Connection Pool 적용 및 Failover 설정
Retry 로직 적용 (예: Spring Retry)
### 📌 장애 시나리오 3: 특정 서버 과부하로 다운
🛑 현상:

일부 사용자가 서비스 이용 불가
서버 응답 시간 증가

🛠 대응 방안:

Auto Scaling 적용
로드 밸런서 설정 (Nginx, AWS ALB 등)
