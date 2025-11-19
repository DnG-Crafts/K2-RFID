package dngsoftware.spoolid;

import static dngsoftware.spoolid.Utils.filamentTypes;
import static dngsoftware.spoolid.Utils.filamentVendors;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.security.SecureRandom;
import java.util.Locale;

public class jsonAdapter extends RecyclerView.Adapter<jsonAdapter.ViewHolder> {

    private final jsonItem[] jsonItems;
    private final Context context;

    public jsonAdapter(Context context, jsonItem[] items) {
        this.context = context;
        jsonItems = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_json, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        jsonItem currentItem = jsonItems[position];

        holder.itemKey.setText(currentItem.jKey);

        if (currentItem.jValue.toString().equalsIgnoreCase("true") || currentItem.jValue.toString().equalsIgnoreCase("false"))
        {
            holder.itemValue.setVisibility(View.INVISIBLE);
            holder.itemSpin.setVisibility(View.VISIBLE);
            holder.spinBorder.setVisibility(View.VISIBLE);
            if (currentItem.jValue.toString().equalsIgnoreCase("false")) {
                holder.itemSpin.setSelection(0);
            }
            else {
                holder.itemSpin.setSelection(1);
            }

            holder.itemSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    currentItem.jValue =  parentView.getItemAtPosition(position).toString().toLowerCase();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });

        }else {

            holder.itemValue.setVisibility(View.VISIBLE);
            holder.itemSpin.setVisibility(View.INVISIBLE);
            holder.spinBorder.setVisibility(View.INVISIBLE);

            if (currentItem.jKey.equalsIgnoreCase("id")) {
                SecureRandom random = new SecureRandom();
                currentItem.jValue = String.format(Locale.getDefault(), "%05d", random.nextInt(99999));
                holder.itemValue.setText(currentItem.jValue.toString());
                InputFilter[] editFilters = holder.itemValue.getFilters();
                InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
                System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                newFilters[editFilters.length] = new InputFilter.LengthFilter(5);
                holder.itemValue.setFilters(newFilters);

            } else {
                holder.itemValue.setText(currentItem.jValue.toString().trim());
            }

            if (currentItem.jKey.equalsIgnoreCase("brand") || currentItem.jKey.equalsIgnoreCase("filament_vendor")) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, filamentVendors);
                holder.itemValue.setAdapter(adapter);
            }

            if (currentItem.jKey.equalsIgnoreCase("meterialtype") || currentItem.jKey.equalsIgnoreCase("filament_type")) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, filamentTypes);
                holder.itemValue.setAdapter(adapter);
            }

            if (currentItem.jKey.equalsIgnoreCase("brand") && currentItem.jValue.equals(currentItem.hintValue)) {
                holder.itemValue.setText("");
                holder.itemValue.setHint(currentItem.hintValue);
                holder.itemValue.setHintTextColor(Color.parseColor("#D3D3D3"));
                holder.itemKey.setTextColor(Color.parseColor("#ff0000"));
            }

            if (currentItem.jKey.equalsIgnoreCase("meterialtype") && currentItem.jValue.equals(currentItem.hintValue)) {
                holder.itemValue.setText("");
                holder.itemValue.setHint(currentItem.hintValue);
                holder.itemValue.setHintTextColor(Color.parseColor("#D3D3D3"));
                holder.itemKey.setTextColor(Color.parseColor("#ff0000"));
            }

            if (currentItem.jKey.equalsIgnoreCase("name") && currentItem.jValue.equals(currentItem.hintValue)) {
                holder.itemValue.setText("");
                holder.itemValue.setHint(currentItem.hintValue);
                holder.itemValue.setHintTextColor(Color.parseColor("#D3D3D3"));
                holder.itemKey.setTextColor(Color.parseColor("#ff0000"));
            }

            if (holder.itemValue.getTag() instanceof TextWatcher) {
                holder.itemValue.removeTextChangedListener((TextWatcher) holder.itemValue.getTag());
            }


            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentItem.jValue = s.toString().trim();

                    if (currentItem.jKey.equalsIgnoreCase("id")) {

                        if (currentItem.jValue.toString().isBlank() || currentItem.jValue.toString().isEmpty()) {
                            holder.itemKey.setTextColor(Color.parseColor("#ff0000"));
                        } else {
                            holder.itemKey.setTextColor(Color.parseColor("#000000"));
                        }
                    }

                    if (currentItem.jKey.equalsIgnoreCase("brand")) {

                        if (currentItem.jValue.toString().isBlank() || currentItem.jValue.toString().isEmpty()) {
                            holder.itemKey.setTextColor(Color.parseColor("#ff0000"));
                        } else {
                            holder.itemKey.setTextColor(Color.parseColor("#000000"));
                        }
                    }

                    if (currentItem.jKey.equalsIgnoreCase("meterialtype")) {
                        if (currentItem.jValue.toString().isBlank() || currentItem.jValue.toString().isEmpty()) {
                            holder.itemKey.setTextColor(Color.parseColor("#ff0000"));
                        } else {
                            holder.itemKey.setTextColor(Color.parseColor("#000000"));
                        }
                    }

                    if (currentItem.jKey.equalsIgnoreCase("name")) {
                        if (currentItem.jValue.toString().isBlank() || currentItem.jValue.toString().isEmpty()) {
                            holder.itemKey.setTextColor(Color.parseColor("#ff0000"));
                        } else {
                            holder.itemKey.setTextColor(Color.parseColor("#000000"));
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };
            holder.itemValue.addTextChangedListener(textWatcher);
            holder.itemValue.setTag(textWatcher);

            holder.itemValue.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    holder.itemValue.setHint("");
                    try {
                        Double.parseDouble(holder.itemValue.getText().toString());
                        holder.itemValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    } catch (Exception ignored) {
                        try {
                            Integer.parseInt(holder.itemValue.getText().toString());
                            holder.itemValue.setInputType(InputType.TYPE_CLASS_NUMBER);
                        } catch (Exception pass) {
                            holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT);
                        }
                    }
                } else {
                    if (currentItem.jKey.equalsIgnoreCase("brand") && currentItem.jValue.toString().isEmpty() || currentItem.jValue.toString().isBlank()) {
                        holder.itemValue.setHint(currentItem.hintValue);
                        holder.itemValue.setHintTextColor(Color.parseColor("#D3D3D3"));
                    }

                    if (currentItem.jKey.equalsIgnoreCase("meterialtype") && currentItem.jValue.toString().isEmpty() || currentItem.jValue.toString().isBlank()) {
                        holder.itemValue.setHint(currentItem.hintValue);
                        holder.itemValue.setHintTextColor(Color.parseColor("#D3D3D3"));
                    }

                    if (currentItem.jKey.equalsIgnoreCase("name") && currentItem.jValue.toString().isEmpty() || currentItem.jValue.toString().isBlank()) {
                        holder.itemValue.setHint(currentItem.hintValue);
                        holder.itemValue.setHintTextColor(Color.parseColor("#D3D3D3"));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return jsonItems.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemKey;
        AutoCompleteTextView  itemValue;
        Spinner itemSpin;
        View spinBorder;

        ViewHolder(View itemView) {
            super(itemView);
            itemKey = itemView.findViewById(R.id.itemKey);
            itemValue = itemView.findViewById(R.id.itemValue);
            itemSpin = itemView.findViewById(R.id.itemSpin);
            spinBorder = itemView.findViewById(R.id.spinBorder);
            ArrayAdapter<String> adapter = new ArrayAdapter<>( itemView.getContext(), R.layout.adapter_spinner_item, new String[]{"false", "true"});
            itemSpin.setAdapter(adapter);
        }
    }
}