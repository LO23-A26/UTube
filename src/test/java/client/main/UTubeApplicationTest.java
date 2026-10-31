package client.main;

import client.ihmmain.IHMMainManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UTubeApplicationTest {

    @Test
    void viewsAreOnClasspath() {
        assertNotNull(IHMMainManager.class.getResource(IHMMainManager.MAIN_VIEW));
        assertNotNull(IHMMainManager.class.getResource(IHMMainManager.LOGIN_VIEW));
        assertNotNull(IHMMainManager.class.getResource(IHMMainManager.STYLESHEET));
    }
}
