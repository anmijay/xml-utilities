package org.xmlutils.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class XMLUtil {
    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory
            .newInstance();

    /**
     * Gotta keep Colbertura happy.
     */
    private XMLUtil() {
        super();
    }

    /**
     * This method is used for updating the value of a tag in a
     * <code>Document</code> object.
     * 
     * @param doc
     *            Document object
     * @param tagName
     *            name of the tag
     * @param tagValue
     *            the updated value of the tag
     */
    public static void replaceTagValue(Document doc, String tagName,
            String tagValue) {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        int j = nodeList.getLength();
        Node node;
        for (int i = 0; i < j; i++) {
            Node newNode = doc.createTextNode(tagValue);
            node = nodeList.item(i);
            if (node.getFirstChild() != null) {
                node.replaceChild(newNode, node.getFirstChild());
            } else {
                node.appendChild(newNode);
            }
        }
    }

    /**
     * This method is used to check whether a node having name tagname is
     * available in the Document object. If it exists it returs true otherwise
     * false. This method takes Document object and String tagname as argument
     * and returns boolean.
     * 
     * @param doc
     *            Document object.
     * @param tagname
     *            the name of tag
     * @return <code>true</code> if tag with <code>tagname</code> exists in the
     *         <code>Document</code> object. Otherwise returns
     *         <code>false</code>.
     */
    public static boolean checkTagExists(Document doc, String tagname) {
        boolean retVal = true;
        NodeList nodes = doc.getElementsByTagName(tagname);
        if ((nodes == null) || (nodes.getLength() == 0)) {
            retVal = false;
        }
        return retVal;
    }

    /**
     * This method is used to insert a new tag below the tag specified by
     * <code>appendTo</code> parameter.
     * 
     * @param d
     *            the <code>Document</code> object to which a new tag is to be
     *            inserted.
     * @param appendTo
     *            the tag below which a new tag needs to be inserted.
     * @param tagName
     *            the name of new tag
     * @param tagValue
     *            the value of new tag
     */
    public static Element insertNewTagBelow(Document d, String appendTo,
            String tagName, String tagValue) {
        Node element = d.getElementsByTagName(appendTo).item(0);
        if (element == null) {
            element = d.createElement(appendTo);
        }
        Element newElement = d.createElement(tagName);
        element.appendChild(newElement);
        newElement.appendChild(d.createTextNode(tagValue));
        return newElement;
    }

    public static void addAttributeToElement(Document doc,
            Element projectElement, String attributeName, String attributeValue) {
        Attr xmlnsAttr = doc.createAttribute(attributeName);
        xmlnsAttr.setValue(attributeValue);
        projectElement.setAttributeNode(xmlnsAttr);
    }

    /**
     * Inserts a new value for an XML tag specified by <code>tagName</code> name
     * in a <code>Document</code> object.
     * 
     * @param doc
     *            Document object.
     * @param tagName
     *            Name of the tag as String.
     * @param tagValue
     *            Value of the tag as String.
     */
    public static Element insertTagValue(Document doc, String tagName,
            String tagValue) {
        Element element = doc.createElement(tagName);
        doc.getDocumentElement().appendChild(element);
        if (tagValue != null) {
            element.appendChild(doc.createTextNode(tagValue));
        }
        return element;
    }

    /**
     * Inserts a new value for an XML tag specified by <code>tagName</code> name
     * in a <code>Element</code> object.
     * 
     * @param elementToAppend
     *            Element object.
     * @param tagName
     *            Name of the tag as String.
     * @param tagValue
     *            Value of the tag as String.
     */
    public static Element insertTagInElement(Document document,
            Element elementToAppend, String tagName, String tagValue) {
        Element newElement = document.createElement(tagName);
        elementToAppend.appendChild(newElement);
        newElement.appendChild(document.createTextNode(tagValue));
        return newElement;
    }

    public static void insertOrUpdateTagValue(Document doc, String tagName,
            String tagValue) {
        if (XMLUtil.checkTagExists(doc, tagName)) {
            XMLUtil.replaceTagValue(doc, tagName, tagValue);
        } else {
            XMLUtil.insertTagValue(doc, tagName, tagValue);
        }
    }

    /**
     * This method takes Element object and String tagName as argument and
     * returns the value of the first child node it gets. If it does not find
     * any node it will return null.
     * 
     * @param element
     *            Element object
     * @param tagName
     *            Name of the tag.
     * @return the value of the first occurance of <code>tagName</code> tag in
     *         <code>element</code> object. Returns null, if doesn't find any.
     */
    public static String getTagValue(Element element, String tagName) {
        if (element == null) {
            return null;
        }
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes == null || nodes.getLength() <= 0) {
            return null;
        }
        return getTagValue(nodes.item(0));
    }

    /**
     * This method takes Node object as argument and return the value of the
     * first child of the node.
     * <p/>
     * If there is no such node, this will return null.
     * 
     * @param node
     *            Node object
     * @return <code>String</code> returns the value of the node
     */
    public static String getTagValue(Node node) {
        NodeList childNodeList = node.getChildNodes();
        String value;
        if (childNodeList == null) {
            value = "";
        } else {
            StringBuffer buffer = new StringBuffer();
            for (int j = 0; j < childNodeList.getLength(); j++) {
                buffer.append(childNodeList.item(j).getNodeValue());
            }
            value = buffer.toString();
        }
        return value;
    }

    /**
     * Deletes a tag based on passed <code>tagName</code> and <code>doc</code>
     * object.
     * 
     * @param doc
     *            Document object.
     * @param tagName
     *            Name of the tag as String.
     */
    public static void deleteTag(Document doc, String tagName) {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        int j = nodeList.getLength();
        Node node;
        Node parentNode;
        for (int i = 0; i < j; i++) {
            node = nodeList.item(i);
            parentNode = node.getParentNode();
            parentNode.removeChild(node);
        }
    }

    /**
     * This method is used to search all the <code>Element</code> objects with
     * given key in the passed <code>Element</code> object.
     * 
     * @param tagName
     *            Name of the tag as String.
     * @param input
     *            Element object.
     * @return <code>Element[]</code> Returns the array of elements, or an empty
     *         array in case here is no match.
     */
    public static List<Element> getElements(String tagName, Element input) {
        NodeList nodes = input.getElementsByTagName(tagName);

        int len = nodes.getLength();
        List<Element> elt = new ArrayList<Element>(len);
        Node node;
        for (int i = 0; i < len; i++) {
            node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elt.add((Element) node);
            }
        }

        return elt;
    }

    public static Document convertFileToDom(File xmlFile) {
        Document document;
        try {
            document = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().parse(
                    xmlFile);
        } catch (Exception e) {
            throw new IllegalStateException("Incoming file is not valid xml", e);
        }
        return document;
    }

    public static Document convertStringToDom(String xml) {
        Document document;
        ByteArrayInputStream stream = null;
        try {
            stream = new ByteArrayInputStream(xml.getBytes());
            document = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().parse(
                    stream);
        } catch (Exception e) {
            throw new IllegalStateException("Incoming text is not valid xml", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return document;
    }

    public static String prettyPrint(File file) {
        Transformer tf;
        try {
            tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult stringResult = new StreamResult(new StringWriter());
            tf.transform(new DOMSource(convertFileToDom(file)), stringResult);
            return stringResult.getWriter().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void prettyPrint(Document document) {
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
        try {
            serializer = tfactory.newTransformer();
            // Setup indenting to "pretty print"
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "2");

            serializer.transform(new DOMSource(document), new StreamResult(
                    System.out));
        } catch (TransformerException e) {
            // this is fatal, just dump the stack and throw a runtime exception
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    public static <K, V> String mapToXml(Map<K, V> map, String rootName,
            String childName, String keyName, String valueName)
            throws TransformerException, ParserConfigurationException {
        Document document = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder()
                .newDocument();

        Element root = document.createElement(rootName);
        document.appendChild(root);

        for (Map.Entry<K, V> errorCodeReport : map.entrySet()) {
            Element s = document.createElement(childName);
            root.appendChild(s);

            s.setAttribute(keyName, errorCodeReport.getKey().toString());
            s.setAttribute(valueName, errorCodeReport.getValue().toString());
        }

        return getStringFromDocument(document);
    }

    public static String getStringFromDocument(Document doc) {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            transformer.transform(domSource, result);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return writer.toString();
    }

    public static Document getDocument(File xmlFile) {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            return docBuilder.parse(xmlFile);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        } catch (SAXException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    public static Document getDocument(InputStream stream) {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            return docBuilder.parse(stream);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        } catch (SAXException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    public static Document getDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlString));
            return builder.parse(is);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        } catch (SAXException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    public static void writeDocumentToDisk(Document document,
            File directoryToWriteTo, String fileName) {
        DOMImplementation domImplementation = document.getImplementation();
        if (domImplementation.hasFeature("LS", "3.0")
                && domImplementation.hasFeature("Core", "2.0")) {
            DOMImplementationLS domImplementationLS = (DOMImplementationLS) domImplementation
                    .getFeature("LS", "3.0");
            LSSerializer lsSerializer = domImplementationLS
                    .createLSSerializer();
            DOMConfiguration domConfiguration = lsSerializer.getDomConfig();
            if (domConfiguration.canSetParameter("format-pretty-print",
                    Boolean.TRUE)) {
                lsSerializer.getDomConfig().setParameter("format-pretty-print",
                        Boolean.TRUE);
                LSOutput lsOutput = domImplementationLS.createLSOutput();
                lsOutput.setEncoding("UTF-8");
                try {
                    lsOutput.setByteStream(new FileOutputStream(new File(
                            directoryToWriteTo, fileName)));
                } catch (FileNotFoundException e) {
                    throw new IllegalStateException(e);
                }
                lsSerializer.write(document, lsOutput);
            }
        }

    }

    public static void writeDocument(Document document,
            File directoryToWriteTo, String fileName) {
        // Prepare the DOM document for writing
        Source source = new DOMSource(document);

        // Prepare the output file
        File file = new File(directoryToWriteTo, fileName);
        Result result = new StreamResult(file);

        // Write the DOM document to the file
        try {
            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            throw new IllegalStateException(e);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e);
        } catch (TransformerFactoryConfigurationError e) {
            throw new IllegalStateException(e);
        } catch (TransformerException e) {
            throw new IllegalStateException(e);
        }

    }
}