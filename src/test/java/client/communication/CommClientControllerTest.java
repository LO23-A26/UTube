package client.communication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;


@Timeout(10)
class CommClientControllerTest {

    private CommClientTestFixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new CommClientTestFixture();
    }

    @Test
    void runFailsWhenInterfacesAreNotInjected() {
        // controller without an external interface must refuse to start
        CommClientController controller = new CommClientController();

        assertThrows(IllegalStateException.class, controller::run);
    }

    @Test
    void runSendsAValidUuidToTheServer() throws Exception {
        try (FakeServer server = new FakeServer(CommClientTestFixture.SERVER_PORT)) {
            fixture.controller.run();

            String received = server.waitForUuid(2_000);
            assertNotNull(received, "The server did not receive anything from the client");
            // UUID.fromString throws if the client sent something that is not a UUID
            assertDoesNotThrow(() -> UUID.fromString(received));
        }
    }

    @Test
    void runDoesNotCrashWhenNoServerIsAvailable() {
        // no FakeServer started: the connection is refused
        assertDoesNotThrow(fixture.controller::run);
    }

    @Test
    void connectionAloneDoesNotCallOtherModules() throws Exception {
        // TODO (#48): once the full index is handled, replace this with
        //   verify(fixture.dataMock, timeout(2_000)).saveFullIndex(any(), any());
        try (FakeServer server = new FakeServer(CommClientTestFixture.SERVER_PORT)) {
            fixture.controller.run();
            server.waitForUuid(2_000);
        }

        verifyNoInteractions(fixture.dataMock, fixture.ihmShareMock, fixture.ihmMainMock);
    }
}