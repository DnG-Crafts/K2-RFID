package dngsoftware.spoolid;

import static dngsoftware.spoolid.Utils.*;
import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class PrinterManager {
    Context context;

    public PrinterManager(Context context) {
        this.context = context;
    }

    public void saveList(List<String> list) {
        JSONArray jsonArray = new JSONArray(list);
        SaveSetting(context, "printer_list", jsonArray.toString());
    }

    public List<String> getList() {
        List<String> list = new ArrayList<>();
        String json = GetSetting(context, "printer_list", null);
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.getString(i));
                }
            } catch (JSONException ignored) {}
        }
        return list;
    }

    public void addItem(String newItem) {
        List<String> currentList = getList();
        if (!currentList.contains(newItem)) {
            currentList.add(newItem);
            saveList(currentList);
        }
    }

    public void removeItem(String itemName) {
        List<String> currentList = getList();
        boolean removed = currentList.remove(itemName);
        if (removed) {
            saveList(currentList);
        }
    }
}