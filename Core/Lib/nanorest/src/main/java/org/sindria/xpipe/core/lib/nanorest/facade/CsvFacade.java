package org.sindria.xpipe.core.lib.nanorest.facade;

import java.io.IOException;
import java.util.List;

import org.sindria.xpipe.core.lib.nanorest.helper.CsvHelper;

public class CsvFacade {

    private static final CsvHelper csvHelper = new CsvHelper();

    private CsvFacade() {
        // Private constructor to prevent instantiation
    }

    public static List<List<String>> parseCsv(String file, String separator) throws IOException {
        return csvHelper.csvParser(file, separator);
    }
}

