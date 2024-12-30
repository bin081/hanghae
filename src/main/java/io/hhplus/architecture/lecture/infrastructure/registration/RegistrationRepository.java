package io.hhplus.architecture.lecture.infrastructure.registration;

import io.hhplus.architecture.lecture.domain.lecture.LectureInfo;
import io.hhplus.architecture.lecture.domain.registration.RegistrationInfo;
import io.hhplus.architecture.lecture.domain.user.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<RegistrationInfo, Integer> {
    List<RegistrationInfo> findByUserId(Integer userId);
    List<RegistrationInfo> findByLectureId(Integer lectureId);

    // 특정 사용자 ID와 신청 상태(status)가 "completed"인 등록 내역을 조회하는 메서드
    List<RegistrationInfo> findByUserIdAndStatus(Integer userId, String status);

}
