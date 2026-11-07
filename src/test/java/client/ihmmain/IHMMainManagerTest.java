package client.ihmmain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class IHMMainManagerTest {

    @Test
    void viewsAreOnClasspath() {
        assertNotNull(IHMMainManager.class.getResource(IHMMainManager.MAIN_VIEW));
        assertNotNull(IHMMainManager.class.getResource(IHMMainManager.LOGIN_VIEW));
        assertNotNull(IHMMainManager.class.getResource(IHMMainManager.HOME_VIEW));
        assertNotNull(IHMMainManager.class.getResource(IHMMainManager.STYLESHEET));
    }
}
