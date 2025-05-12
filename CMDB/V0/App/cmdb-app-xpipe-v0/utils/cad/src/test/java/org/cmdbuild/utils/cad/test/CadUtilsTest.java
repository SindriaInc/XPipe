/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.utils.cad.CadGeometryUtils.getCenter;
import static org.cmdbuild.utils.cad.CadUtils.dwgToDxf;
import static org.cmdbuild.utils.cad.CadUtils.parseDwgFile;
import static org.cmdbuild.utils.cad.CadUtils.parseDxfFile;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.utils.cad.dxfparser.DxfStreamProcessor;
import org.cmdbuild.utils.cad.dxfparser.model.DxfDocument;
import org.cmdbuild.utils.cad.dxfparser.model.DxfEntity;
import static org.cmdbuild.utils.cad.geo.GeoUtils.parseTransformationRules;
import static org.cmdbuild.utils.cad.geo.GeoUtils.serializeTransformationRules;
import org.cmdbuild.utils.cad.geo.PointTransformationRule;
import org.cmdbuild.utils.cad.geo.PointTransformationRuleImpl;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.junit.Assert.assertEquals;
import org.cmdbuild.utils.cad.model.CadEntity;
import static org.cmdbuild.utils.cad.model.CadPoint.point;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.testutils.IgnoreSlowTestRule;
import org.cmdbuild.utils.testutils.Slow;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import org.junit.Rule;

public class CadUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Rule
    public IgnoreSlowTestRule rule = new IgnoreSlowTestRule();

    @Test
    public void testTranslationRulesToJson() {
        String json = serializeTransformationRules(list(PointTransformationRuleImpl.rotation(1.23, 10, 20), PointTransformationRuleImpl.scaling(0.5, 0.6, 15, 25), PointTransformationRuleImpl.translation(19, 29), PointTransformationRuleImpl.rcs("ONE", "TWO")));
        assertEquals("[{\"rotation_angle\":1.23,\"rotation_center_x\":10.0,\"rotation_center_y\":20.0},{\"scaling_x\":0.5,\"scaling_y\":0.6,\"scaling_center_x\":15.0,\"scaling_center_y\":25.0},{\"translation_x\":19.0,\"translation_y\":29.0},{\"source_crs\":\"ONE\",\"target_crs\":\"TWO\"}]", json);
        List<PointTransformationRule> rules = parseTransformationRules(json);
        assertEquals(4, rules.size());
        assertEquals("1.23", rules.get(0).getRotationAngle().toString());
        assertEquals(point(10, 20), rules.get(0).getRotationCenter());
        assertEquals(point(0.5, 0.6), rules.get(1).getScaling());
        assertEquals(point(15, 25), rules.get(1).getScalingCenter());
        assertEquals(point(19, 29), rules.get(2).getTranslation());
        assertEquals("ONE", rules.get(3).getSourceCoordinateReferenceSystem());
        assertEquals("TWO", rules.get(3).getTargetCoordinateReferenceSystem());
    }

    @Test
    @Slow
    public void testDwgToDxf1() {
        byte[] dxf = dwgToDxf(toByteArray(getClass().getResourceAsStream("/test_file_1.dwg")));
        assertNotNull(dxf);
        assertTrue(dxf.length > 0);
    }

    @Test
    @Slow
    public void testDwgToDxf2() {
        byte[] dxf = dwgToDxf(toByteArray(getClass().getResourceAsStream("/test_file_2.dwg")));
        assertNotNull(dxf);
        assertTrue(dxf.length > 0);
        //TODO fixme
//        assertEquals(readToString(getClass().getResourceAsStream("/test_file_2.dxf")), new String(dxf));//TODO charset
    }

    @Test
    @Slow
    public void testDwgToDxf7() {
        byte[] dxf = dwgToDxf(toByteArray(getClass().getResourceAsStream("/test_file_7.dwg")));
        assertNotNull(dxf);
        assertTrue(dxf.length > 0);
    }

//    @Test
//    public void testParseDxf1() {
//        DxfDocumentHelper document = parseDxfFile(toByteArray(getClass().getResourceAsStream("/test_file_1.dxf")));
//        assertNotNull(document);
//        logger.info("document content =\n\n{}\n", document.printDocumentContent());
//
//        document.getEntities(DXFPolyline.class).forEach(l -> {
//            logger.info("found polyline = {} layer = {}", l.getVertexes().map(v -> format("(%s,%s)", v.getX(), v.getY())).collect(joining(" - ")), l.getLayerName());
//        });
//    }
//
//    @Test
//    public void testParseDxf2() {
//        DxfDocumentHelper document = parseDxfFile(toByteArray(getClass().getResourceAsStream("/test_file_2.dxf")));
//        assertNotNull(document);
//        logger.info("document content =\n\n{}\n", document.printDocumentContent());
//
////        document.getEntities(DXFPolyline.class).forEach(l -> {
////            logger.info("found polyline = {} layer = {}", l.getVertexes().map(v -> format("(%s,%s)", v.getX(), v.getY())).collect(joining(" - ")), l.getLayerName());
////        });
//        document.getEntities(DXFEntity.class).filter(p -> p.getXdata().isNotEmpty()).forEach(c -> {
//            logger.info("found entity for layer = {} type = {} xdata = {}", c.getLayerName(), c.getType(), mapToLoggableStringInline(c.getXdata().getAllXDataAsMap()));
////            logger.info("found entry for layer = {} xdata1000 = {} vertexes = {}", c.getLayerName(), mapToLoggableStringInline(c.getXdata().getAllXDataAsMap()),
////                    c.getVertexes().map(v -> format("(%s,%s)", v.getX(), v.getY())).collect(joining(" - ")));
//        });
//
//    }
//    @Test
//    public void testParseDwg2() {
//        DxfDocumentHelper document = parseDwgFile(toByteArray(getClass().getResourceAsStream("/test_file_2.dwg")));
//        document.getEntities(DXFEntity.class).filter(p -> p.getXdata().isNotEmpty()).forEach(c -> {
//            logger.info("found entity for layer = {} type = {} xdata = {}", c.getLayerName(), c.getType(), mapToLoggableStringInline(c.getXdata().getAllXDataAsMap()));
//        });
//
//    }
    @Test
    public void testParseDxf2a() {
        new DxfStreamProcessor(e -> logger.trace("processing group with code = {} value =< {} >", e.getGroupCode(), e.getValue()))
                .processStream(new InputStreamReader(getClass().getResourceAsStream("/test_file_2.dxf"), StandardCharsets.UTF_8));//TODO charset
    }

    @Test
    public void testParseDxf2b() {
        DxfDocument document = parseDxfFile(getClass().getResourceAsStream("/test_file_2.dxf"));
        logger.debug("document header variables = \n\n{}\n", mapToLoggableString(document.getHeaderVariables()));

        assertEquals("AC1032", document.getHeaderVariables().get("$ACADVER").getStringValue());
        assertEquals("Standard", document.getHeaderVariables().get("$DIMSTYLE").getStringValue());

        List<DxfEntity> entities = document.getEntities();

        assertFalse(entities.isEmpty());

        entities.stream().filter(p -> p.hasXdata()).forEach(c -> {
            logger.info("found entity for layer = {} type = {} xdata = {} position = {}\n\tperimeter = {}",
                    c.getLayer(), c.getType(), mapToLoggableStringInline(c.getXdata().getXdata()), getCenter(c.getPerimeter()), c.getPerimeter());
        });

//        DxfGeodata geodata = document.getObject(DxfGeodata.class);
//        logger.info("found geodata =\n\n{}\n", geodata.getValues().stream().map(v -> format("\t%s", v)).collect(joining("\n")));
//        DxfGeoreferenceInfo info = document.getGeoreferenceInfo();
//        logger.info("georeference info = {}", info);
    }

    @Test
    public void testParseDxfToCadEntities2() {
        List<CadEntity> cadEntities = parseDxfFile(getClass().getResourceAsStream("/test_file_2.dxf")).getCadEntities("EPSG:4326", true);
        assertFalse(cadEntities.isEmpty());
        cadEntities.forEach(e -> logger.info("found cad entity = {} with metadata = {}", e, mapToLoggableStringInline(e.getMetadata())));
    }

    @Test
    public void testParseDxfToCadEntities2b() {
        List<CadEntity> cadEntities = parseDxfFile(getClass().getResourceAsStream("/test_file_2.dxf")).getCadEntities("EPSG:3857", true);
        assertFalse(cadEntities.isEmpty());
        cadEntities.forEach(e -> logger.info("found cad entity = {} with metadata = {}", e, mapToLoggableStringInline(e.getMetadata())));
//        assertEquals(20, cadEntities.size());
        assertEquals(12, cadEntities.size()); // TODO  check this
    }

    @Test
    public void testParseDxfToCadEntities3() {
        List<CadEntity> cadEntities = parseDxfFile(getClass().getResourceAsStream("/test_file_3.dxf")).getCadEntities("EPSG:4326", true);
        assertFalse(cadEntities.isEmpty());
        cadEntities.forEach(e -> logger.info("found cad entity = {} with metadata = {}", e, mapToLoggableStringInline(e.getMetadata())));
    }

    @Test
    public void testParseDxfToCadEntities4() {
        List<CadEntity> cadEntities = parseDxfFile(getClass().getResourceAsStream("/test_file_4.dxf")).getCadEntities("EPSG:4326", true);
        assertFalse(cadEntities.isEmpty());
        cadEntities.forEach(e -> logger.info("found cad entity = {} with metadata = {}", e, mapToLoggableStringInline(e.getMetadata())));
    }

    @Test
    public void testDxfmetadata7() {
        DxfDocument document = parseDxfFile(getClass().getResourceAsStream("/test_file_7.dxf"));
        logger.info("vars =\n {}", mapToLoggableString(document.getHeaderVariables()));
        Map<String, String> metadata = document.getMetadata();
        logger.info("metadata =\n {}", mapToLoggableString(metadata));
        assertThat(metadata, hasEntry("TT-Comment1", "mandi"));
        assertThat(metadata, hasEntry("TT-Comment2", "ciao"));
        assertThat(metadata, hasEntry("TT-Comment3", "hi"));
        assertThat(metadata, hasEntry("CustomKey01", "CustomVal01"));
        assertThat(metadata, hasEntry("CustomKey02", "CustomVal02"));
        assertThat(metadata, hasEntry("CustomKey03", "CustomVal03"));
        assertThat(metadata, hasEntry("$TITLE", "TT-Title"));
        assertThat(metadata, hasEntry("$SUBJECT", "TT-Subject"));
        assertThat(metadata, hasEntry("$KEYWORDS", "TT-Key1, TT-Key2"));

        assertThat(document.getMetadataFromComments(), hasEntry("TT-Comment1", "mandi"));
        assertThat(document.getMetadataFromComments(), hasEntry("TT-Comment2", "ciao"));
        assertThat(document.getMetadataFromComments(), hasEntry("TT-Comment3", "hi"));
    }

    @Test
    public void testParseDxfToCadEntities7() {
        List<CadEntity> cadEntities = parseDxfFile(getClass().getResourceAsStream("/test_file_7.dxf")).getCadEntities("EPSG:4326", true);
        assertFalse(cadEntities.isEmpty());
        cadEntities.forEach(e -> logger.info("found cad entity = {} with metadata = {}", e, mapToLoggableStringInline(e.getMetadata())));
    }

    @Test
    public void testParseDxfToCadEntities8() {
        List<CadEntity> cadEntities = parseDxfFile(getClass().getResourceAsStream("/test_file_8.dxf")).getCadEntities("EPSG:4326", true);
        assertFalse(cadEntities.isEmpty());
        cadEntities.forEach(e -> logger.info("found cad entity = {} with metadata = {}", e, mapToLoggableStringInline(e.getMetadata())));
    }

    @Test
    @Slow
    public void testParseDwgToCadEntities2() {
        List<CadEntity> cadEntities = parseDwgFile(getClass().getResourceAsStream("/test_file_2.dwg")).getCadEntities("EPSG:4326", true);
        assertFalse(cadEntities.isEmpty());
        cadEntities.forEach(e -> logger.info("found cad entity = {} with metadata = {}", e, mapToLoggableStringInline(e.getMetadata())));
    }

    @Test
    @Slow
    public void testParseDwgToCadEntities3() {
        List<CadEntity> cadEntities = parseDwgFile(getClass().getResourceAsStream("/test_file_3.dwg")).getCadEntities("EPSG:4326", true);
        assertFalse(cadEntities.isEmpty());
        cadEntities.forEach(e -> logger.info("found cad entity = {} with metadata = {}", e, mapToLoggableStringInline(e.getMetadata())));
    }

    @Test
    @Slow
    public void testParseDwgToCadEntities4() {
        List<CadEntity> cadEntities = parseDwgFile(getClass().getResourceAsStream("/test_file_3.dwg")).getCadEntities("EPSG:4326", true);
        assertFalse(cadEntities.isEmpty());
        cadEntities.forEach(e -> logger.info("found cad entity = {} with metadata = {}", e, mapToLoggableStringInline(e.getMetadata())));
    }
}
