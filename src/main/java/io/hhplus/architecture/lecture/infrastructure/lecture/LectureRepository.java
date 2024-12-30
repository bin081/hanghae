package io.hhplus.architecture.lecture.infrastructure.lecture;

import io.hhplus.architecture.lecture.domain.lecture.LectureInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
    public interface LectureRepository extends JpaRepository<LectureInfo, Integer>{
        List<LectureInfo> findByDate(LocalDate date);

}
