package client.ihmmain;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomeControllerTest {

    @Test
    void formatTags() {
        assertEquals("aucun", HomeController.formatTags(null));
        assertEquals("aucun", HomeController.formatTags(Set.of()));
        assertEquals("git, java, tuto", HomeController.formatTags(Set.of("tuto", "java", "git")));
    }
}
