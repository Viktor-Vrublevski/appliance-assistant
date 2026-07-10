package epam.course.appliance.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import epam.course.appliance.dto.UserChatData;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserContextHolderTest {

    private static final String SESSION_ID_1 = "session-abc-123";
    private static final String SESSION_ID_2 = "session-xyz-789";

    @BeforeEach
    void setUp() {
        UserContextHolder.getUserChatDataMap().clear();
    }

    @Test
    void testSetUserChatDataShouldStoreDataCorrectly() {
        UserChatData mockData = mock(UserChatData.class);

        UserContextHolder.setUserChatData(SESSION_ID_1, mockData);

        UserChatData retrievedData = UserContextHolder.getUserChatData(SESSION_ID_1);
        assertThat(retrievedData)
                .isNotNull()
                .isSameAs(mockData);
    }

    @Test
    void testGetUserChatDataWhenSessionDoesNotExistShouldReturnNull() {
        UserChatData retrievedData = UserContextHolder.getUserChatData("non-existent-session");

        assertThat(retrievedData).isNull();
    }

    @Test
    void testSetUserChatData_WhenOverwritingExistingSession_ShouldUpdateValue() {
        UserChatData firstData = mock(UserChatData.class);
        UserChatData secondData = mock(UserChatData.class);
        UserContextHolder.setUserChatData(SESSION_ID_1, firstData);

        UserContextHolder.setUserChatData(SESSION_ID_1, secondData);

        UserChatData retrievedData = UserContextHolder.getUserChatData(SESSION_ID_1);
        assertThat(retrievedData)
                .isSameAs(secondData)
                .isNotSameAs(firstData);
    }

    @Test
    void testRemoveUserChatData_ShouldRemoveOnlyTargetedSession() {
        UserChatData data1 = mock(UserChatData.class);
        UserChatData data2 = mock(UserChatData.class);
        UserContextHolder.setUserChatData(SESSION_ID_1, data1);
        UserContextHolder.setUserChatData(SESSION_ID_2, data2);

        UserContextHolder.removeUserChatData(SESSION_ID_1);

        assertThat(UserContextHolder.getUserChatData(SESSION_ID_1)).isNull();
        assertThat(UserContextHolder.getUserChatData(SESSION_ID_2)).isSameAs(data2);
    }

    @Test
    void testGetUserChatDataMap_ShouldReturnUnderlyingMap() {
        UserChatData data = mock(UserChatData.class);
        UserContextHolder.setUserChatData(SESSION_ID_1, data);

        Map<String, UserChatData> internalMap = UserContextHolder.getUserChatDataMap();

        assertThat(internalMap)
                .isNotNull()
                .containsKey(SESSION_ID_1)
                .containsValue(data);
    }
}
