package org.sindria.xpipe.core.lib.nanorest.helper;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvHelper {

    public List<List<String>> csvParser(String file, String separator) throws IOException {
        try (InputStream is = this.getClass().getResourceAsStream(file)) {
            if (is == null) {
                throw new IOException("File not found: " + file);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                List<List<String>> records = new ArrayList<>();
                String line;

                // Skip header
                boolean header = true;

                while ((line = br.readLine()) != null) {
                    if (header) {
                        header = false;
                        continue;
                    }
                    records.add(Arrays.asList(line.split(separator)));
                }

                return records;
            }
        }
    }
}
