package dngsoftware.spoolid;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.io.File;

@Database(entities = {Filament.class}, version = 1, exportSchema = false)
public abstract class filamentDB extends RoomDatabase {
    public abstract MatDB matDB();
    public static volatile filamentDB INSTANCE;

    public static filamentDB getInstance(Context context, String pType) {
        closeInstance();
        synchronized (filamentDB.class) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), filamentDB.class, "material_database_" + pType)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();

        }
        return INSTANCE;
    }

    public static File getDatabaseFile(Context context, String pType) {
        return context.getDatabasePath("material_database_" + pType);
    }

    public static void closeInstance() {
        if (INSTANCE != null && INSTANCE.isOpen()) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }

}