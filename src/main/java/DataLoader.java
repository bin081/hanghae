import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DataLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        jdbcTemplate.execute("INSERT INTO user_info (user_id, username) VALUES (1, 'test1')");
        jdbcTemplate.execute("INSERT INTO user_info (user_id, username) VALUES (2, 'test2')");
        jdbcTemplate.execute("INSERT INTO lecture_info (lecture_id, speaker_id, start_time, end_time, max_participants, speaker_name, lecture_date, title) VALUES (1, 11, '2024-12-25 15:30:00', '2024-12-25 15:30:00', 30, 'speaker1', '2024-12-25 15:30:00', 'JAVA1')");
        jdbcTemplate.execute("INSERT INTO lecture_info (lecture_id, speaker_id, start_time, end_time, max_participants, speaker_name, lecture_date, title) VALUES (2, 12, '2024-12-25 15:30:00', '2024-12-25 15:30:00', 30, 'speaker2', '2024-12-25 15:30:00', 'JAVA2')");
    }
}
