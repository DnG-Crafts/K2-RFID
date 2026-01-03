package dngsoftware.spoolid;

import androidx.annotation.NonNull;
import org.json.JSONObject;

public class PrinterOption {
    public String displayName;
    public JSONObject data;

    public PrinterOption(String displayName, JSONObject data) {
        this.displayName = displayName;
        this.data = data;
    }

    @NonNull
    @Override
    public String toString() {
        return displayName;
    }
}