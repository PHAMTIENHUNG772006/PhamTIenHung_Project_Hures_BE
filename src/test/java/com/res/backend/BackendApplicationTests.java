package com.res.backend;

import com.restaurant.erp.RestaurantErpApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = RestaurantErpApplication.class)
@ActiveProfiles("dev")
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
