package org.gvfbla;

import com.google.gson.*;
import java.lang.reflect.Type;

/**
 * Custom deserializer for the abstract 'account' class.
 * Determines the concrete subclass to instantiate based on the 'type' field in JSON.
 */
public class AccountDeserializer implements JsonDeserializer<account> {

    @Override
    public account deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Extract the 'type' field to determine the subclass
        JsonElement typeElement = jsonObject.get("type");
        if (typeElement == null) {
            throw new JsonParseException("Missing 'type' field in JSON for account deserialization.");
        }

        String type = typeElement.getAsString();
        Class<? extends account> accountClass;

        switch (type) {
            case "AdminAccount":
                accountClass = AdminAccount.class;
                break;
            case "EmployerAccount":
                accountClass = EmployerAccount.class;
                break;
            case "StudentAccount":
                accountClass = StudentAccount.class;
                break;
            default:
                throw new JsonParseException("Unknown account type: " + type);
        }

        // Delegate deserialization to the appropriate subclass
        return context.deserialize(json, accountClass);
    }
}
