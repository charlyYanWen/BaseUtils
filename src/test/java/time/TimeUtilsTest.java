package time;

import com.base.utils.time.LocalDateTimeUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

/**
 * @description: 时间单元测试
 * @author: yanwen
 * @create: 2022-04-01 11:20
 **/
public class TimeUtilsTest {

    /**
     * String形式的字符串转LocalDateTime时间
     */
    @Test
    void test(){
        LocalDateTime times = LocalDateTimeUtils.strToLocalDateTime("2022-03-23 17:30:00");
        System.out.println("times =="+times);
    }
}
