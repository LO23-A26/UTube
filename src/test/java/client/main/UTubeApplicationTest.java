package client.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UTubeApplicationTest {

    @Test
    void mainViewIsOnClasspath() {
        assertNotNull(UTubeApplication.class.getResource(UTubeApplication.MAIN_VIEW));
    }
}
