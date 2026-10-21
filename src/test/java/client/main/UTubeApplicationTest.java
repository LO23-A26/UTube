package client.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UTubeApplicationTest {

    @Test
    void viewsAreOnClasspath() {
        assertNotNull(UTubeApplication.class.getResource(UTubeApplication.MAIN_VIEW));
        assertNotNull(UTubeApplication.class.getResource(UTubeApplication.LOGIN_VIEW));
        assertNotNull(UTubeApplication.class.getResource(UTubeApplication.STYLESHEET));
    }
}
