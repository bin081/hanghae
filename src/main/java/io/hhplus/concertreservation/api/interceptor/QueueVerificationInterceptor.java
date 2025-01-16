package io.hhplus.concertreservation.api.interceptor;

import io.hhplus.concertreservation.api.data.entity.UserQueue;
import io.hhplus.concertreservation.api.data.repository.UserQueueRepository;
import io.hhplus.concertreservation.api.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
public class QueueVerificationInterceptor implements HandlerInterceptor {

    @Autowired
    private final UserQueueRepository userQueueRepository;
    @Autowired
    private final QueueService queueService;

    @Autowired
    public QueueVerificationInterceptor(UserQueueRepository userQueueRepository, QueueService queueService) {
        this.userQueueRepository = userQueueRepository;
        this.queueService = queueService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 요청에서 userToken을 추출
        String userToken = request.getHeader("Authorization");  // 또는 request.getParameter("userToken");
        if (userToken == null || userToken.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User token is required");
            return false;
        }

        // 대기열 검증
        Optional<UserQueue> userQueue = userQueueRepository.findByToken(userToken);
        if (userQueue.isEmpty()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User not in queue");
            return false;
        }

        // 대기열에 포함되어 있는지 확인하고, 예약 가능 상태인지 검증
        if (!queueService.isUserEligible(userQueue)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User not eligible for reservation");
            return false;
        }

        return true;  // 대기열 검증이 통과되면 요청을 계속 처리하도록 함
    }
}
