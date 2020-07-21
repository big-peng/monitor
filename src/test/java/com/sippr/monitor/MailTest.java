package com.sippr.monitor;

import com.sippr.monitor.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ChenXiangpeng
 */
@SpringBootTest
public class MailTest {
    @Autowired
    private MailService mailService;

    @Test
    public void sendMail(){
        mailService.sendSimpleMailMessge("859695668@qq.com","123","123");
    }
}
