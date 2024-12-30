package io.hhplus.architecture.lecture.service;

import io.hhplus.architecture.lecture.common.exception.LectureException;
import io.hhplus.architecture.lecture.domain.lecture.LectureInfo;
import io.hhplus.architecture.lecture.domain.registration.RegistrationInfo;
import io.hhplus.architecture.lecture.domain.user.UserInfo;
import io.hhplus.architecture.lecture.infrastructure.lecture.LectureRepository;
import io.hhplus.architecture.lecture.infrastructure.registration.RegistrationRepository;
import io.hhplus.architecture.lecture.infrastructure.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LectureService {

    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;

    @Autowired
    public LectureService(RegistrationRepository registrationRepository,
                               UserRepository userRepository,
                               LectureRepository lectureRepository) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.lectureRepository = lectureRepository;
    }

    // 특강 신청
    public RegistrationInfo registerForLecture(Integer userId, Integer lectureId) {

        UserInfo user = userRepository.findByUserId(userId).orElseThrow(() -> new LectureException.UserNotFoundException("User not found"));
        LectureInfo lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new LectureException.LectureNotFoundException("Lecture not found"));

        // 중복 신청 방지 로직
        Optional<RegistrationInfo> existingRegistration = registrationRepository.findByUserId(userId)
                .stream()
                .filter(reg -> reg.getLectureId()==lectureId)  // 특정 userId와 lectureId로 필터링
                .findFirst();

        if (existingRegistration.isPresent()) {
            throw new RuntimeException("User has already registered for this lecture");
        }

        RegistrationInfo registration = new RegistrationInfo();
        registration.setUserId(user.getUserId());
        registration.setLectureId(lecture.getLectureId());
        registration.setRegistration_time(LocalDate.now());
        registration.setStatus("completed");

        return registrationRepository.save(registration);
    }

    // 날짜별로 신청 가능한 특강 목록 조회
    public List<LectureInfo> getLecturesByDate(LocalDate date) {
        return lectureRepository.findByDate(date);
    }

    // 신청 완료된 특강 목록 조회 (특정 사용자)
    public List<LectureInfo> getCompletedLecturesForUser(Integer userId) {

        // 사용자별 신청 완료된 특강 정보 조회
        List<RegistrationInfo> completedRegistrations = registrationRepository.findByUserIdAndStatus(userId, "completed");

        // 신청 완료된 특강 목록 반환
        return completedRegistrations.stream()
                .map(reg -> lectureRepository.findById(reg.getLectureId())
                        .orElseThrow(() -> new RuntimeException("Lecture not found")))
                .collect(Collectors.toList());
    }


}
