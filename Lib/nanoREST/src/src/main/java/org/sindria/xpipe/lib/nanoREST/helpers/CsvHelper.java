package org.sindria.xpipe.lib.nanoREST.helpers;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CsvHelper {

    public List<List> csvParser(String file, String separator) throws IOException {

        try (
                InputStream is = this.getClass().getResourceAsStream(file);
        ) {
            assert is != null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader br = new BufferedReader(isr);
                 Stream<String> lines = br.lines();

            ) {
                boolean header = true;
                int rowCounter = 0;

                List<List> records = new ArrayList<>();

                for (String line; (line = br.readLine()) != null;) {
                    if (header) {
                        header = false;
                    } else {
                        rowCounter ++;
                        String[] items = line.split(separator);
                        List<String> fields = Arrays.asList(items);
                        records.add(fields);
                    }
                }
                return records;
            }
        }
    }

}
