package org.activiti.core.common.spring.identity;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.activiti.core.common.spring.identity.ExtendedInMemoryUserDetailsManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ExtendedInMemoryUserDetailsManagerIT {

    @Autowired
    private ExtendedInMemoryUserDetailsManager extendedInMemoryUserDetailsManager;

    @Test
    public void checkGroupAuthorities() {
        List<String> groups = extendedInMemoryUserDetailsManager.getGroups();
        assertThat(groups)
            .isNotNull()
            .allSatisfy(x -> assertThat(x).contains("GROUP"));
    }

    @Test
    public void checkUsers() {
        List<String> users = extendedInMemoryUserDetailsManager.getUsers();
        assertThat(users)
            .isNotNull()
            .contains("admin");
    }

}
