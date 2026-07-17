package epam.course.appliance;

import java.util.Set;

public class ApplianceConstant {
    public static final String KEY_CONVERSATION_ID = "conversationId";
    public static final String KEY_USERNAME = "username";
    public static final Set<String> KEY_CATEGORIES = Set.of(
            "TV-set",
            "Refrigerator",
            "Washing Machine",
            "Dishwasher",
            "Microwave",
            "Oven",
            "Air Conditioner",
            "Dryer",
            "Vacuum Cleaner",
            "Coffee Maker"
    );
}
