package io.hhplus.tdd.point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final PointService pointService;

    // Constructor Injection을 통해 service를 주입받는다.
    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    /**
     * 특정 유저의 포인트를 조회하는 기능
     */
    @GetMapping("{id}")
    public UserPoint point(@PathVariable long id) throws Exception {
        // 서비스에서 포인트 조회
        return pointService.getUserPoint(id);
    }

    /**
     * 특정 유저의 포인트 충전/이용 내역을 조회하는 기능
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(@PathVariable long id) throws Exception {
        // 서비스에서 포인트 사용 내역 조회
        return pointService.getPointHistory(id);
    }

    /**
     * 특정 유저의 포인트를 충전하는 기능
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable long id,
            @RequestBody long amount) {
        try {
            // 서비스에서 포인트 충전 처리
            return pointService.chargePoint(id, amount);
        } catch (IllegalArgumentException e) {
            // 예외 처리: 충전 금액에 대한 검증이 실패하면 400 오류를 반환
            log.error("충전 실패: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * 특정 유저의 포인트를 사용하는 기능
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount) {
        try {
            // 서비스에서 포인트 사용 처리
            return pointService.usePoint(id, amount);
        } catch (IllegalArgumentException e) {
            // 예외 처리: 사용 금액에 대한 검증이 실패하면 400 오류를 반환
            log.error("사용 실패: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
