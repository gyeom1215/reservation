package util.student;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonDataHandler<T> {
    private final String filePath;
    private final Gson gson;
    private final Type type;

    public JsonDataHandler(String filePath, Type type) {
        this.filePath = filePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.type = type;
    }

    public List<T> loadData() {
        try (FileReader reader = new FileReader(filePath)) {
            List<T> data = gson.fromJson(reader, type);
            return data != null ? data : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void saveData(List<T> data) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void appendData(T item) {
        List<T> data = loadData();
        data.add(item);
        saveData(data);
    }

    public void updateData(List<T> newData) {
        saveData(newData);
    }
} 