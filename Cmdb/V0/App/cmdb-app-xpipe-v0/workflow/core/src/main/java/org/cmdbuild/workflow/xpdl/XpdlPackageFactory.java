package org.cmdbuild.workflow.xpdl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.cmdbuild.workflow.model.WorkflowException;
import org.enhydra.jxpdl.XMLInterface;
import org.enhydra.jxpdl.XMLInterfaceImpl;
import org.enhydra.jxpdl.XPDLRepositoryHandler;
import org.enhydra.jxpdl.elements.Package;
import org.w3c.dom.Document;

public class XpdlPackageFactory {

    private static final boolean IS_XML_REPRESENTATION = true;

    public static Package readXpdl(InputStream is) {
        try {
            return readXpdl(IOUtils.toByteArray(is));
        } catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public static Package readXpdl(byte[] pkgContent) {
        try {
            return xmlInterface().openPackageFromStream(pkgContent, IS_XML_REPRESENTATION);
        } catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    private static XMLInterface xmlInterface() {
        return new XMLInterfaceImpl();
    }

    public static byte[] xpdlByteArray(Package pkg) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.newDocument();
            XPDLRepositoryHandler repH = new XPDLRepositoryHandler();
//        repH.setXPDLPrefixEnabled(true);
            repH.toXML(document, pkg);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("encoding", "UTF8");
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(document), new StreamResult(os));
            return os.toByteArray();
        } catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

}
