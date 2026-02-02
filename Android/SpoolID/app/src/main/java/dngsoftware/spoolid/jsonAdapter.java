package dngsoftware.spoolid;

import static dngsoftware.spoolid.Utils.*;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
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

        if (holder.itemValue.getTag() instanceof TextWatcher) {
            holder.itemValue.removeTextChangedListener((TextWatcher) holder.itemValue.getTag());
        }

        holder.itemKey.setHint(currentItem.jKey);
        String val = currentItem.jValue != null ? currentItem.jValue.toString() : "";

        if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false")) {
            holder.itemValue.setInputType(InputType.TYPE_NULL);
            holder.itemValue.setBackgroundColor(Color.TRANSPARENT);
            holder.itemValue.setFocusable(false);
            holder.itemValue.setCursorVisible(false);
            String[] options = new String[]{"false", "true"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.adapter_spinner_item, options);
            holder.itemValue.setAdapter(adapter);
            holder.itemValue.setText(val.toLowerCase(), false);
            holder.itemValue.setOnItemClickListener((parent, view, position1, id) -> {
                currentItem.jValue = parent.getItemAtPosition(position1).toString().toLowerCase();
            });

        } else {
            holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT);
            holder.itemKey.setEndIconMode(TextInputLayout.END_ICON_NONE);
            holder.itemValue.setFilters(new InputFilter[0]);

            if (currentItem.jKey.equalsIgnoreCase("brand") || currentItem.jKey.equalsIgnoreCase("filament_vendor")) {
                holder.itemValue.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, filamentVendors));
            } else if (currentItem.jKey.equalsIgnoreCase("meterialtype") || currentItem.jKey.equalsIgnoreCase("filament_type")) {
                holder.itemValue.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, filamentTypes));
            } else {
                holder.itemValue.setAdapter(null);
            }

            if (currentItem.jKey.equalsIgnoreCase("id")) {
                if (holder.itemKey.getTag(R.id.itemKey) == null) {
                    SecureRandom random = new SecureRandom();
                    val = String.format(Locale.getDefault(), "%05d", random.nextInt(99999));
                    currentItem.jValue = val;
                    holder.itemKey.setTag(R.id.itemKey, "randomized");
                } else {
                    val = currentItem.jValue.toString();
                }
                holder.itemValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
            } else {
                holder.itemValue.setFilters(new InputFilter[0]);
            }

            boolean isTarget = currentItem.jKey.equalsIgnoreCase("brand") ||
                    currentItem.jKey.equalsIgnoreCase("meterialtype") ||
                    currentItem.jKey.equalsIgnoreCase("name");

            if (isTarget && val.equals(currentItem.hintValue) && holder.itemKey.getTag() == null) {
                holder.itemValue.setText("", false);
                holder.itemValue.setHint(currentItem.hintValue);
                holder.itemValue.setHintTextColor(ContextCompat.getColor(context, R.color.text_hint));
                holder.itemKey.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_error)));
                holder.itemKey.setDefaultHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_error)));
            } else {
                holder.itemValue.setText(val, false);
                holder.itemValue.setHint("");
                ColorStateList blue = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_brand));
                holder.itemKey.setHintTextColor(blue);
                holder.itemKey.setDefaultHintTextColor(blue);
            }

            TextWatcher textWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String input = s.toString().trim();
                    currentItem.jValue = input;

                    if (!input.isEmpty()) {
                        holder.itemKey.setTag("modified");
                    }

                    if (input.isEmpty() || (isTarget && input.equals(currentItem.hintValue) && holder.itemKey.getTag() == null)) {
                        holder.itemKey.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_error)));
                        holder.itemKey.setDefaultHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_error)));
                    } else {
                        holder.itemKey.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_brand)));
                        holder.itemKey.setDefaultHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_brand)));
                    }
                }
                @Override public void afterTextChanged(Editable s) {}
            };

            holder.itemValue.addTextChangedListener(textWatcher);
            holder.itemValue.setTag(textWatcher);

            holder.itemValue.setOnItemClickListener((parent, view, pos, id) -> {
                currentItem.jValue = parent.getItemAtPosition(pos).toString();
                holder.itemKey.setTag("modified");
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(holder.itemValue.getWindowToken(), 0);
                }
                holder.itemValue.clearFocus();
                holder.itemKey.requestFocus();
                holder.itemValue.setHint("");
                ColorStateList blue = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_brand));
                holder.itemKey.setHintTextColor(blue);
                holder.itemKey.setDefaultHintTextColor(blue);
            });

            holder.itemValue.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    holder.itemValue.setHint("");
                } else {
                    if (isTarget && currentItem.jValue.toString().isEmpty()) {
                        holder.itemValue.setHint(currentItem.hintValue);
                        holder.itemValue.setHintTextColor(ContextCompat.getColor(context, R.color.text_hint));
                    }
                }
            });
        }
    }

    @Override public int getItemCount() { return jsonItems.length; }
    @Override public long getItemId(int position) { return position; }
    @Override public int getItemViewType(int position) { return position; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextInputLayout itemKey;
        MaterialAutoCompleteTextView itemValue;
        ViewHolder(View itemView) {
            super(itemView);
            itemKey = itemView.findViewById(R.id.itemKey);
            itemValue = itemView.findViewById(R.id.itemValue);
        }
    }
}