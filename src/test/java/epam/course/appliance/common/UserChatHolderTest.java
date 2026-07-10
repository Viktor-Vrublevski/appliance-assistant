package epam.course.appliance.common;

import static org.assertj.core.api.Assertions.assertThat;

import epam.course.appliance.dto.ChatMessageDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserChatHolderTest {

    private static final String USER_ALICE = "Alice";
    private static final String USER_BOB = "Bob";

    @BeforeEach
    void setUp() {
        UserChatHolder.getUserChatMemory().clear();
    }

    @Test
    void testAddMessageWhenUserDoesNotExistShouldCreateListAndAddMessage() {
        ChatMessageDto message = new ChatMessageDto("Hello Alice", "System", LocalDateTime.now());

        UserChatHolder.addMessage(USER_ALICE, message);

        List<ChatMessageDto> messages = UserChatHolder.getMessages(USER_ALICE);
        assertThat(messages)
                .isNotNull()
                .hasSize(1)
                .containsExactly(message);
    }

    @Test
    void testAddMessageWhenUserAlreadyExistsShouldAppendToExistingList() {
        ChatMessageDto msg1 = new ChatMessageDto("Hello", "User", LocalDateTime.now());
        ChatMessageDto msg2 = new ChatMessageDto("How can I help?", "AI", LocalDateTime.now());
        UserChatHolder.addMessage(USER_ALICE, msg1);

        UserChatHolder.addMessage(USER_ALICE, msg2);

        List<ChatMessageDto> messages = UserChatHolder.getMessages(USER_ALICE);
        assertThat(messages)
                .hasSize(2)
                .containsExactly(msg1, msg2);
    }

    @Test
    void testGetMessagesWhenUserDoesNotExistShouldReturnNull() {
        List<ChatMessageDto> messages = UserChatHolder.getMessages("NonExistentUser");

        assertThat(messages).isNull();
    }

    @Test
    void testClearShouldRemoveUserFromMemory() {
        ChatMessageDto message = new ChatMessageDto("Secret message", "Alice", LocalDateTime.now());
        UserChatHolder.addMessage(USER_ALICE, message);
        UserChatHolder.addMessage(USER_BOB, new ChatMessageDto("Hi Bob", "System", LocalDateTime.now()));

        UserChatHolder.clear(USER_ALICE);

        assertThat(UserChatHolder.getMessages(USER_ALICE)).isNull();
        assertThat(UserChatHolder.getMessages(USER_BOB)).isNotNull().hasSize(1);
    }

    @Test
    void testGetUserChatMemoryShouldReturnTheUnderlyingMap() {
        ChatMessageDto message = new ChatMessageDto("Direct Map Test", "System", LocalDateTime.now());
        UserChatHolder.addMessage(USER_ALICE, message);

        Map<String, List<ChatMessageDto>> memory = UserChatHolder.getUserChatMemory();

        assertThat(memory)
                .isNotNull()
                .containsKey(USER_ALICE);
        assertThat(memory.get(USER_ALICE)).containsExactly(message);
    }
}
