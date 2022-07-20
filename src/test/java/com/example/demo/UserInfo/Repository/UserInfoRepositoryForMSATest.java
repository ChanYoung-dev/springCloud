package com.example.demo.UserInfo.Repository;

import com.example.demo.UserInfo.Service.UserInfoService;
import com.example.demo.UserInfo.domain.UserInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class UserInfoRepositoryForMSATest {

    @Autowired EntityManager em;
    @Autowired
    UserInfoRepository userAccountRepository;

    @Autowired
    UserInfoService userInfoService;



    @Test
    void findUserById() {
        userInfoService.register("emrhssla", "김찬숙", "emrhssla@gmail.com");
        em.flush();
        UserInfo emrhssla = userAccountRepository.findUserById("emrhssla");
        em.flush();
        Assertions.assertThat(emrhssla.getUserId()).isEqualTo("emrhssla");
    }
}