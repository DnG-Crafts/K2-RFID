package dngsoftware.spoolid;

import androidx.annotation.NonNull;

public class MaterialItem {
    private final String materialName;
    private final String materialID;

    public MaterialItem(String brand, String id) {
        this.materialName = brand;
        this.materialID = id;
    }

    public String getMaterialName() {
        return materialName;
    }

    public String getMaterialID() {
        return materialID;
    }

    @NonNull
    @Override
    public String toString() {
        return materialName;
    }
}