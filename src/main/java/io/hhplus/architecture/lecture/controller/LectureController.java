package io.hhplus.architecture.lecture.controller;

import io.hhplus.architecture.lecture.domain.lecture.LectureInfo;
import io.hhplus.architecture.lecture.domain.registration.RegistrationInfo;
import io.hhplus.architecture.lecture.infrastructure.lecture.LectureRepository;
import io.hhplus.architecture.lecture.infrastructure.registration.RegistrationRepository;
import io.hhplus.architecture.lecture.service.LectureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

//요구사항 1   /user/{uid}/lecture
// 요구사항2 /lecture?date={date}
// 요구사항3 /lecture/apply(post) {uid {uid}, lid{lid}}
// auto increment -> bigInt로 사용
// FK 를 잡는 것을 지양하는 경향이 있음.
// lecture 테이블에서 currentParticipants 컬럼 빼기, redis 사용하기 때문에 굳이 사용 안함
// 우리가 기대한 에러코드가 나오는지 , 유효하지 않은 exeception 이 나오는지




@RestController
@RequestMapping("lecture")
public class LectureController {
    // 도메인 엔티티 : 도메인 객체는 능동적으로 행동 할 수 있어야 한다.
    // 도메인 모델 패턴과 스크립트 패턴
    // 제약조건을 어떻게 설정하느냐에 따라서 다르고, 대신 내가 리뷰를 받아야 할 때 내가 생각한 제약조건을 명시해주어야 한다.
    // 유지보수 / 변경용이성 / 기능의 격리 -> 이렇게 해서 클린 + 레이어드에서 지키고자 했던 것
    // 도메인 로직이 중심이라고 했으니까 변경의 전파가 덜하도록 해야 함, 에그리거트 루트 개념(도메인 모델의 진입점) 으로 생각함
    // 조회에 집중하고 있는지, 연산에 집중하고 있는지
    // 실무에서 파사드 패턴을 많이 사용하나요? 많이 사용하고 있음
    // 조인을 안하도록 설계 한다기보다는 필요하다면 조인을 안할 수 있게끔 비정규화 / MV 를 만든다.
    // N:M 일때, N이 주인이냐 M이 주인이냐에 따라 다르다.
    // 서버 쪽에서는 에러면 에러 정확히 내려줄지를 결정하는 것이고 클라이언트에서 그 오류를 먹을지 말지를 결정하는 것이다.
    // 서비스에서 repository를 물어서 들어오게끔 만드는 것이 좋음, 순환 참조 보다는 하위에 참조하는 형식으로 구현할 것
    // 꼭 pasade를 다 있어야 한다는 것은 아니다.
    // 멘토링 (율무 코치님)
    //서비스 단위에서의 단위 테스트가 필요함


    private static final Logger log = LoggerFactory.getLogger(LectureController.class);
    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    /* 1. 특강신청 API - 특정 userid로 선착순으로 제공되는 특강을 신청 */
    // 특강 신청 API
    @PostMapping("/register")
    public ResponseEntity<String> registerForLecture(@RequestParam Integer userId, @RequestParam Integer lectureId) {
        try {
            RegistrationInfo registration = lectureService.registerForLecture(userId, lectureId);
            return ResponseEntity.ok("Registration successful for lecture: " + registration.getLectureId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    /* 2. 특강 신청 가능 목록 API - 날짜별로 신청가능한 특강 목록을 조회하는 API */
    // 날짜별로 신청 가능한 특강 목록 조회
    @GetMapping("/date")
    public ResponseEntity<List<LectureInfo>> getLecturesByDate(@RequestParam String date) {
        LocalDate lectureDate = LocalDate.parse(date);  // 'yyyy-MM-dd' 형식으로 날짜 파싱
        List<LectureInfo> lectures = lectureService.getLecturesByDate(lectureDate);
        return ResponseEntity.ok(lectures);
    }

    /* 3. 특강 신청 가능 목록 API - user별로 신청완료된 특강 목록을 조회하는 API*/
    @GetMapping("/user/{userId}/lectures")
    public ResponseEntity<List<LectureInfo>> getUserCompletedLectures(@PathVariable Integer userId) {
        try {
            List<LectureInfo> lectures = lectureService.getCompletedLecturesForUser(userId);
            return ResponseEntity.ok(lectures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
