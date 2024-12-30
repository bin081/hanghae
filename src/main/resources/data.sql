-- 사용자 데이터 삽입
INSERT INTO user_info (name) VALUES ('test1');
INSERT INTO user_info (name) VALUES ('test2');

-- 특강 데이터 삽입
INSERT INTO lecture_info (current_participants, date, end_time, max_participants, speaker, start_time, title)
VALUES (11, '2024-12-25', '2024-12-25', 30, 'speaker1', '2024-12-25', 'JAVA1');
INSERT INTO lecture_info (current_participants, date, end_time, max_participants, speaker,start_time, title)
VALUES (12, '2024-12-25', '2024-12-25', 30, 'speaker2', '2024-12-25', 'JAVA2');
