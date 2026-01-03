package dngsoftware.spoolid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.List;

public class spinnerAdapter extends ArrayAdapter<PrinterOption> {
    private final List<PrinterOption> allItems;
    private final List<String> selectedItems;

    public spinnerAdapter(Context context, List<PrinterOption> items, List<String> selected) {
        super(context, R.layout.spinner_item_icon, items);
        this.allItems = items;
        this.selectedItems = selected;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.spinner_item_icon, parent, false);
        }
        TextView text = row.findViewById(R.id.item_text);
        ImageView icon = row.findViewById(R.id.item_icon);
        PrinterOption option = allItems.get(position);
        text.setText(option.displayName);
        if (selectedItems.contains(option.displayName)) {
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.GONE);
        }
        return row;
    }

}