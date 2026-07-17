package epam.course.appliance.brain.tool.prompt;

/**
 * Prompt for fetching appliance data.
 */
public class FetchAppliancePrompt {

    public static final String FETCH_APPLIANCE_PROMPT = """
            <tool_description>
                <name>fetchApplianceDataTool</name>
                <purpose>
                    Retrieves comprehensive appliance information for a specific user and enriches it with
                    relevant documentation from the RAG vector store to provide context-aware, model-specific guidance.
                    If user doesn't have any appliance registered, answer with "User doesn't have any appliance registered."
                </purpose>
                <when_to_use>
                    <scenario>User reports an appliance error, malfunction, or unexpected behavior</scenario>
                    <scenario>User asks troubleshooting questions (e.g., "Why is my refrigerator making noise?")</scenario>
                    <scenario>User requests maintenance or care instructions for their appliances</scenario>
                    <scenario>User asks general questions about their registered appliances</scenario>
                    <scenario>User needs warranty information or model specifications</scenario>
                    <note>
                        Always invoke this tool BEFORE providing appliance-specific recommendations to ensure
                        accuracy based on the user's actual equipment and maintenance history.
                    </note>
                </when_to_use>
                <parameters>
                    <parameter>
                        <name>category</name>
                        <type>String</type>
                        <required>true</required>
                        <description>
                            The category of the appliance for which the user is seeking information. This helps
                            the RAG system retrieve relevant manual sections specific to the appliance type.
                            Example categories include: "TV-set", "Refrigerator", "Washing Machine", "Dishwasher",
                             "Microwave", "Oven", "Air Conditioner", "Dryer", "Vacuum Cleaner", "Coffee Maker".
                        </description>
                    </parameter>
                    <parameter>
                        <name>request</name>
                        <type>String</type>
                        <required>true</required>
                        <description>
                            The user's original question or issue description. This contextualizes the response
                            and helps the RAG system retrieve relevant manual sections.
                        </description>
                        <example>My refrigerator is making a loud buzzing noise</example>
                        <example>How do I clean the lint filter on my dryer?</example>
                    </parameter>
                </parameters>
                <returns>
                    <description>
                        A ChatResponse containing the user's appliance data enriched with relevant documentation:
                    </description>
                    <includes>
                        <item>Serial numbers</item>
                        <item>Model numbers and names</item>
                        <item>Appliance categories (refrigerator, washer, dryer, etc.)</item>
                        <item>Manufacture dates</item>
                        <item>Warranty expiration dates</item>
                        <item>Relevant manual excerpts and troubleshooting guidance from the vector store</item>
                    </includes>
                </returns>
                <behavior>
                    <step>1. Queries the database for all appliances owned by the specified username</step>
                    <step>2. If no appliances found, returns a message indicating no appliances are registered</step>
                    <step>3. Formats appliance data with all specifications and warranty information</step>
                    <step>4. Sends the request + appliance data to the RAG chat client</step>
                    <step>5. RAG system retrieves relevant manual documentation from the vector store</step>
                    <step>6. Returns a comprehensive response combining appliance data with contextual guidance</step>
                </behavior>
            </tool_description>
            """;
}
