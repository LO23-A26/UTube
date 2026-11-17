package client.ihmmain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ServerControllerTest {

    @Test
    void validate() {
        assertNull(ServerController.validate("127.0.0.1", "8080"));
        assertNull(ServerController.validate(" 192.168.1.10 ", " 65535 "));
        assertNotNull(ServerController.validate("", "8080"));
        assertNotNull(ServerController.validate("127.0.0.1", " "));
        assertNotNull(ServerController.validate("127.0.0.1", "abc"));
        assertNotNull(ServerController.validate("127.0.0.1", "0"));
        assertNotNull(ServerController.validate("127.0.0.1", "70000"));
    }
}
