/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ifc.test;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.MoreCollectors.onlyElement;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import org.apache.commons.beanutils.LazyDynaMap;
import org.apache.commons.jxpath.JXPathContext;
import org.bimserver.models.ifc2x3tc1.IfcOrganization;
import org.bimserver.models.ifc4.IfcApplication;
import org.cmdbuild.utils.ifc.IfcEntry;
import org.cmdbuild.utils.ifc.IfcModel;
import org.cmdbuild.utils.ifc.IfcModelEntriesReport;
import static org.cmdbuild.utils.ifc.utils.XktUtils.ifcToXkt;
import static org.cmdbuild.utils.ifc.utils.IfcUtils.loadIfc;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.lazyMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.codehaus.plexus.util.StringUtils.isNotBlank;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.utils.ifc.utils.IfcUtils.emptyModel;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import org.cmdbuild.utils.testutils.IgnoreSlowTestRule;
import org.cmdbuild.utils.testutils.Slow;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;

public class IfcUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Rule
    public IgnoreSlowTestRule rule = new IgnoreSlowTestRule();

    @Test
    @Slow
    public void testIfcToXkt() throws IOException {
        InputStream ifcInputStream = getClass().getResourceAsStream("/ifc4_example.ifc");
        byte[] xktByteArray = ifcToXkt(ifcInputStream.readAllBytes(), 300l);
        assertTrue(xktByteArray.length > 0);
    }

    @Test
    @Slow
    public void testIfcProcessing1() {
        IfcModel ifc = loadIfc(getClass().getResourceAsStream("/ifc2x3_example.ifc"));

        assertEquals(176810, ifc.getModel().getValues().size());

        IfcOrganization ifcOrganization = ifc.getModel().getAll(IfcOrganization.class).stream().filter(o -> isNotBlank(o.getName())).collect(onlyElement());
        assertEquals(16, ifcOrganization.getExpressId());
        assertEquals("Nemetschek AG", ifcOrganization.getName());

        IfcEntry ifcOrganizationEntry = ifc.getEntries("IfcOrganization").stream().filter(e -> isNotBlank(e.getString("Name"))).collect(onlyElement());
        assertEquals("Nemetschek AG", ifcOrganizationEntry.getString("Name"));

        IfcModelEntriesReport report = ifc.getReport();

        assertEquals(176810, report.getCount());
        assertEquals(1, report.getEntries().get("IfcOrganization").getCount());

        IfcEntry ifcBuilding = getOnlyElement(ifc.getEntries("IfcBuilding"));
        assertEquals("FZK-Haus", ifcBuilding.getString("Name"));
        assertEquals("FZK-Haus", ifcBuilding.queryValue("Name"));

        assertEquals(ifcBuilding.getId(), ifc.queryEntry("IfcBuilding").getId());

        assertEquals("FZK-Haus", ifc.queryValue("IfcBuilding/Name"));

        assertEquals("217973", ifc.queryEntry("IfcSpace[GlobalId='3CgvnK_5f8AxbTWHpnRdyg']").getString("_id"));
        assertEquals("217973", ifc.queryString("IfcSpace[GlobalId='3CgvnK_5f8AxbTWHpnRdyg']/_id"));
        assertEquals("3CgvnK_5f8AxbTWHpnRdyg", ifc.queryEntry("IfcSpace[_id='217973']").getString("GlobalId"));
        assertEquals("3CgvnK_5f8AxbTWHpnRdyg", ifc.queryValue("IfcSpace[_id='217973']/GlobalId"));
    }

    @Test
    @Slow
    public void testIfcProcessing2() {
        IfcModel ifc = loadIfc(getClass().getResourceAsStream("/ifc4_example.ifc"));

        assertEquals(44249, ifc.getModel().getValues().size());

        IfcApplication ifcApplication = getOnlyElement(ifc.getModel().getAll(IfcApplication.class));
        assertEquals(11, ifcApplication.getExpressId());
        assertEquals("ARCHICAD-64", ifcApplication.getApplicationFullName());

        IfcEntry ifcApplicationEntry = getOnlyElement(ifc.getEntries("IfcApplication"));
        assertEquals("ARCHICAD-64", ifcApplicationEntry.getString("ApplicationFullName"));

        IfcModelEntriesReport report = ifc.getReport();

        assertEquals(44249, report.getCount());
        assertEquals(2, report.getEntries().get("IfcOrganization").getCount());

        IfcEntry ifcBuilding = getOnlyElement(ifc.getEntries("IfcBuilding"));
        assertEquals("FZK-Haus", ifcBuilding.getString("Name"));
        assertEquals("FZK-Haus", ifcBuilding.queryValue("Name"));

        logger.info("ifc building = \n\n{}\n", mapToLoggableString(map(
                "expressId", ifcBuilding.getInner().getExpressId(),
                "oid", ifcBuilding.getInner().getOid(),
                "pid", ifcBuilding.getInner().getPid(),
                "rid", ifcBuilding.getInner().getRid()
        )));

        assertEquals(ifcBuilding.getId(), ifc.queryEntry("IfcBuilding").getId());

        assertEquals("FZK-Haus", ifc.queryValue("IfcBuilding/Name"));

        assertEquals("7", ifc.queryEntry("IfcSpace[GlobalId='2dQFggKBb1fOc1CqZDIDlx']").getString("Name"));
        assertEquals("7", ifc.queryValue("IfcSpace[GlobalId='2dQFggKBb1fOc1CqZDIDlx']/Name"));
        assertEquals("2dQFggKBb1fOc1CqZDIDlx", ifc.queryEntry("IfcSpace[Name='7']").getString("GlobalId"));
        assertEquals("2dQFggKBb1fOc1CqZDIDlx", ifc.queryValue("IfcSpace[Name='7']/GlobalId"));
    }

    @Test
    @Slow
    public void testIfcProcessing3() {
        IfcModel ifc = loadIfc(getClass().getResourceAsStream("/ifc_compressed_example.ifczip"));
        assertEquals(27481, ifc.getModel().getValues().size());
        assertEquals(27481, ifc.getReport().getCount());

        IfcEntry ifcBuilding = ifc.queryEntry("IfcBuilding");
        assertEquals("Building", ifcBuilding.getString("Name"));
        assertEquals("Building", ifcBuilding.queryValue("Name"));
        assertEquals("Building", ifc.queryValue("IfcBuilding/Name"));
    }

    @Test
    public void testIfcModel() {
        IfcModel ifc = emptyModel();
        assertEquals(set("Description", "ReferencesElements", "BuildingAddress", "ReferencedBy", "IsDefinedBy", "Decomposes", "Representation", "IsDecomposedBy", "OwnerHistory", "CompositionType", "HasAssignments", "GlobalId", "Name", "ElevationOfRefHeightAsString", "ObjectType", "HasAssociations", "LongName", "ServicedBySystems", "ContainsElements", "ObjectPlacement", "geometry", "ElevationOfTerrainAsString", "ElevationOfTerrain", "ElevationOfRefHeight"),
                ifc.getFeatures("IfcBuilding").keySet());
        assertEquals(817, ifc.getClasses().size());
        assertEquals(0, ifc.getAvailableClasses().size());
    }

    @Test
    @Ignore //TODO
    public void testSurface() {
        IfcModel ifc = loadIfc(getClass().getResourceAsStream("/1679-P0-M3-FM-MAIN_MODEL-AAS-181016.ifc"));
        IfcEntry room = ifc.queryEntry("IfcSpace[Name=146]");
//        IsDefinedBy/RelatingPropertyDefinition[Name='Dimensions']/HasProperties[Name='Area']/NominalValue/wrappedValue
//        ifc.getModel(). TODO
    }

    @Test
    public void testXpath() {
        JXPathContext xpath = JXPathContext.newContext(new LazyDynaMap(lazyMap(() -> map(
                "test", "value",
                "IfcOrganization", new LazyDynaMap(lazyMap(() -> map(
                "myAttr", "myValue"
        ))) {
            {
                setReturnNull(true);
                setRestricted(true);
            }
        }))) {
            {
                setReturnNull(true);
                setRestricted(true);
            }
        });

        assertEquals("myValue", getOnlyElement(list(xpath.iterate("//IfcOrganization/myAttr"))));
        assertEquals("myValue", getOnlyElement(xpath.selectNodes("//IfcOrganization/myAttr")));
        assertEquals("value", getOnlyElement(xpath.selectNodes("//test")));
        assertEquals("myValue", getOnlyElement(xpath.selectNodes("/IfcOrganization/myAttr")));
        assertEquals("myValue", getOnlyElement(xpath.selectNodes("IfcOrganization/myAttr")));
        assertEquals("myValue", getOnlyElement(xpath.selectNodes("/IfcOrganization[myAttr='myValue']/myAttr")));
    }

    @Test
    public void test3() {
        IfcModel ifc = loadIfc(getClass().getResourceAsStream("/76.ifc"));

        String spaceName = "761001", featureName = "761001-MEB-001";

        IfcEntry space = ifc.queryEntry(format("/IfcSpace[@Name='%s']", spaceName));
        logger.info("space = {}", space);
        logger.info("contains = {}", space.getValue("ContainsElements"));
        logger.info("contains = {}", space.getEntry("ContainsElements").getValue("RelatedElements"));

        IfcEntry element2 = ifc.queryEntry(format("/IfcFurnishingElement[@Name='%s']", featureName));
        logger.info("element = {}", element2);
        logger.info("contained = {}", element2.getValue("ContainedInStructure"));

        assertEquals(1, element2.getList("ContainedInStructure").size());

        assertEquals("761001", ifc.queryString("/IfcSpace[@Name='761001']/@Name"));
        assertEquals("3Lj$Q4NoXD0Q6oFcGdsHTI", ifc.queryString("/IfcSpace[@Name='761001']/@GlobalId"));

        assertEquals("761001-MEB-001", ifc.queryString("/IfcSpace[@Name='761001']/ContainsElements/RelatedElements[@Name='761001-MEB-001']/@Name"));
        assertEquals("3kCq1QLU1CkR2ZE1KJW931", ifc.queryString("/IfcSpace[@Name='761001']/ContainsElements/RelatedElements[@Name='761001-MEB-001']/@GlobalId"));

        assertEquals("761001-MEB-001", ifc.queryString("/IfcFurnishingElement[@Name='761001-MEB-001']/@Name"));
        assertEquals("3kCq1QLU1CkR2ZE1KJW931", ifc.queryString("/IfcFurnishingElement[@Name='761001-MEB-001']/@GlobalId"));

        assertEquals("761001", ifc.queryString("/IfcFurnishingElement[@Name='761001-MEB-001']/ContainedInStructure/RelatingStructure/@Name"));
        assertEquals("3Lj$Q4NoXD0Q6oFcGdsHTI", ifc.queryString("/IfcFurnishingElement[@Name='761001-MEB-001']/ContainedInStructure/RelatingStructure/@GlobalId"));
    }
}
