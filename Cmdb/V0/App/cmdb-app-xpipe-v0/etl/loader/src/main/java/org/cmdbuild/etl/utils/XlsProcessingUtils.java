/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.utils;

import static com.google.common.base.Strings.nullToEmpty;
import java.io.IOException;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import java.util.List;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.loader.EtlFileFormat;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import org.cmdbuild.utils.lang.CmStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XlsProcessingUtils {

    public static List<List<Object>> getRecordsFromXlsFile(DataSource data) {
        return getRecordsFromXlsFile(data, parseEnum(FilenameUtils.getExtension(data.getName()), EtlFileFormat.class), null, 0);
    }

    public static List<List<Object>> getRecordsFromXlsFile(DataSource data, EtlFileFormat fileFormat, @Nullable Integer columnNumber, int columnOffset) {
        return new XlsProcessor(columnNumber, columnOffset).getRecordsFromXlsFile(data, fileFormat);
    }

    public static Object lazyRecordToString(List<? extends Object> record) {
        return lazyString(() -> record.stream().map(CmStringUtils::toStringOrEmpty).collect(joining(" | ")));
    }

    public static String dateFormatPatternToXls(String pattern) {
        return pattern.toLowerCase().replaceAll("[^ymdhs -/.,:]", "");
    }

    public static String dateTimeFormatPatternToXls(String pattern) {
        return pattern.toLowerCase().replaceAll("[^ymdhsa -/.,:]", "").replaceAll("a", "AM/PM");
    }

    private static class XlsProcessor {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Integer columnNumber;
        private final int columnOffset;
        private final List<Object> dummyRecordForEmptyRow;

        public XlsProcessor(@Nullable Integer columnNumber, int columnOffset) {
            this.columnNumber = columnNumber;
            this.columnOffset = columnOffset;
            dummyRecordForEmptyRow = isNotNullAndGtZero(this.columnNumber) ? unmodifiableList(IntStream.range(0, this.columnNumber).mapToObj(i -> null).collect(toList())) : emptyList();
        }

        public List<List<Object>> getRecordsFromXlsFile(DataSource data, EtlFileFormat fileFormat) {
            try {
                Workbook workbook;
//                WorkbookFactory.
                switch (fileFormat) {
                    case EFF_XLS ->
                        workbook = new HSSFWorkbook(data.getInputStream());
                    case EFF_XLSX ->
                        workbook = new XSSFWorkbook(data.getInputStream());
                    default ->
                        throw new EtlException("unsupported file format = %s", fileFormat);
                }
                List<List<Object>> list = list();
                int activeSheetIndex = workbook.getActiveSheetIndex();
                Sheet sheet = workbook.getSheetAt(activeSheetIndex);
                if (workbook.getNumberOfSheets() > 1) {
                    logger.warn(marker(), "expected file with a single sheet, but found {}; will load data from active sheet with index = {} and name =< {} > ", workbook.getNumberOfSheets(), activeSheetIndex, sheet.getSheetName());
                } else {
                    logger.debug("load from sheet with index = {} and name =< {} >", activeSheetIndex, sheet.getSheetName());
                }
                logger.debug("last row index = {}", sheet.getLastRowNum());
                for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    try {
                        List<Object> parsedRow = parseRow(row);
                        logger.trace("loaded row index = {} data = {}", rowIndex, lazyRecordToString(parsedRow));
                        list.add(parsedRow);
                    } catch (Exception ex) {
                        throw new EtlException(ex, "error while parsing line = %s", rowIndex);
                    }
                }
                logger.debug("loaded {} records from file", list.size());
                return list;
            } catch (IOException ex) {
                throw new EtlException(ex);
            }
        }

        private List<Object> parseRow(@Nullable Row row) {
            if (row == null) {
                logger.trace("row is empty, returning dummy empty row");
                return dummyRecordForEmptyRow;
            } else {
                return (List) list().accept(m -> {
                    int count = isNotNullAndGtZero(columnNumber) ? columnNumber : row.getLastCellNum();
                    logger.trace("load row, processing max col = {}, row max col = {}", count, row.getLastCellNum());
                    for (int i = 0; i < count; i++) {
                        int columnIndex = i + columnOffset;
                        try {
                            Cell cell = row.getCell(columnIndex, CREATE_NULL_AS_BLANK);
                            Object value = getCellValue(cell);
                            m.add(value);
                        } catch (Exception ex) {
                            throw new EtlException(ex, "error while parsing column = %s", columnIndex);
                        }
                    }
                });
            }
        }

        @Nullable
        private Object getCellValue(Cell cell) {
            return switch (cell.getCellTypeEnum()) {
                case BLANK, STRING ->
                    nullToEmpty(cell.getStringCellValue());
                case NUMERIC ->
                    readNumericOrDate(cell);
                case BOOLEAN ->
                    cell.getBooleanCellValue();
                case FORMULA -> {
                    logger.trace("get cached result for formula =< {} >", cell.getCellFormula());
                    yield switch (cell.getCachedFormulaResultTypeEnum()) {
                        case NUMERIC ->
                            readNumericOrDate(cell);
                        case STRING ->
                            nullToEmpty(cell.getStringCellValue());
                        default ->
                            throw new EtlException("unsupported formula return type = %s", cell.getCachedFormulaResultTypeEnum());
                    };
                }
                default ->
                    throw new EtlException("unsupported cell type = %s", cell.getCellTypeEnum());
            };
        }
    }

    @Nullable
    private static Object readNumericOrDate(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        } else {
            double value = cell.getNumericCellValue();
            if (value % 1 == 0) {
                return (long) value;
            } else {
                return value;
            }
        }
    }
}
