package utils;

import core.ParkingRecord;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {
    public static void saveRecord(ParkingRecord record) throws IOException {
        try (FileWriter fw = new FileWriter("parking_records.txt", true)) {
            fw.write(record.toString() + "\n");
        }
    }
}