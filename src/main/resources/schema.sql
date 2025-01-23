-- 4주차 테이블 설계 (20250104)--

create table user_queue
(
    queue_id                    bigint unsigned auto_increment comment '대기열id',
    token						varchar(1000)	  not null comment '대기열 토큰',
    entered_at 					timestamp(0)      not null comment '대기열진입시간',
    expired_at   				timestamp(0)      not null comment '대기여만료시간',
    status                  	varchar(100)	  comment '대기열상태',
    primary key (queue_id)
);

create table user
(
    id                     		bigint      unsigned auto_increment comment '사용자id',
    name						varchar(100)	not null comment '사용자명',
    primary key (id)
);

create table concert
(
    id							bigint	    	unsigned auto_increment  comment '콘서트id',
    name						varchar(100)    not null comment '콘서트명',
    singer						varchar(100) 	not null comment '가수',
    location					varchar(100) 	not null comment '장소',
    reservation_start_at		timestamp(0)    not null comment '예약가능시작시간',
    reservation_end_at			timestamp(0)    not null comment '예약가능종료시간',
    create_date					timestamp(0)    not null comment '생성일시',
    update_date					timestamp(0)    not null comment '수정일시',
    primary key (id)
);

CREATE TABLE concert_schedual
(
    id                  	    BIGINT UNSIGNED AUTO_INCREMENT COMMENT '콘서트스케줄id',
    concert_id             		BIGINT UNSIGNED NOT NULL COMMENT '콘서트 id',  -- BIGINT UNSIGNED로 수정
    title                 		VARCHAR(100) NOT NULL COMMENT '콘서트제목',
    concert_start_at       		TIMESTAMP(0) NOT NULL COMMENT '콘서트시작시간',
    concert_end_at         		TIMESTAMP(0) NOT NULL COMMENT '콘서트종료시간',
    create_date            		TIMESTAMP(0) NOT NULL COMMENT '생성일시',
    update_date            		TIMESTAMP(0) NOT NULL COMMENT '수정일시',
    status                		VARCHAR(100) NOT NULL COMMENT '좌석마감상태',
    PRIMARY KEY (id),
    CONSTRAINT fk_concert_id FOREIGN KEY (concert_id) REFERENCES concert(id) ON DELETE CASCADE ON UPDATE CASCADE
);


create table seatEntityConcurrency
(
    id                  	    BIGINT UNSIGNED AUTO_INCREMENT COMMENT '콘서트좌석id',
    concert_schedual_id			BIGINT UNSIGNED NOT NULL  COMMENT '콘서트스케줄id',
    seatNum						BIGINT		  NOT NULL COMMENT '좌석 번호',
    price						DOUBLE		  NOT NULL COMMENT'좌석 금액',
    create_date					TIMESTAMP(0)  NOT NULL COMMENT '생성일시',
    update_date					TIMESTAMP(0)  NOT NULL COMMENT '수정일시',
    status                  	VARCHAR(100)  COMMENT '좌석상태(예약가능,임시배정,예약완료)',
    primary key (id),
    CONSTRAINT fk_concert_schedual_id FOREIGN KEY (concert_schedual_id) REFERENCES concert_schedual(id) ON DELETE CASCADE ON UPDATE CASCADE
);

create table reservation
(
    id                  	    BIGINT UNSIGNED AUTO_INCREMENT COMMENT '예약id',
    user_id						BIGINT UNSIGNED NOT NULL  COMMENT  '사용자 id',
    concert_seat_id				BIGINT UNSIGNED NOT NULL  COMMENT  '콘서트좌석id',
    price						DOUBLE		 NOT NULL  COMMENT '콘서트 금액',
    create_date					TIMESTAMP(0) NOT NULL  COMMENT'생성일시',
    update_date					TIMESTAMP(0) NOT NULL  COMMENT '수정일시',
    expired_date				TIMESTAMP(0) NOT NULL  COMMENT '만료일시',
    status                  	VARCHAR(100) COMMENT '좌석상태(예약,결제완료,취소)',
    primary key (id),
    CONSTRAINT fk_concert_user_id FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_concert_seat_id FOREIGN KEY (concert_seat_id) REFERENCES concert_seat(id) ON DELETE CASCADE ON UPDATE CASCADE
);


create table payment
(
    id                    	    BIGINT UNSIGNED AUTO_INCREMENT COMMENT '잔액id',
    user_id                     BIGINT UNSIGNED NOT NULL  COMMENT '사용자id',
    amount 						DOUBLE		 NOT NULL  COMMENT '좌석 금액',
    created_at 					TIMESTAMP(0) NOT NULL  COMMENT'결제일시',
    udpated_at					TIMESTAMP(0) NOT NULL  COMMENT'수정일시',
    primary key (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
);