package io.hhplus.architecture.lecture.infrastructure.user;

import io.hhplus.architecture.lecture.domain.user.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, Integer> {

    Optional<UserInfo> findByUserId(Integer userId);
}
