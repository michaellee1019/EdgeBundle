/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edgebundle;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author Michael Lee
 */
public class GoogleAnalytics {

    SimpleDateFormat gaFormat = new SimpleDateFormat("yyyy-MM-dd");
    //Object[][] urlTotals;
    Object[][] rules;
    static Integer[] pageviews;
    static String[] pagepaths;
    static String[] prePaths;
    static String[] nextPaths;
    static Integer[] pathViews;
    static String[] hostPaths;
    final PageMngr mngr;
    String token;

    GoogleAnalytics(PageMngr mngr) {
        this.mngr = mngr;
    }

    public String Authenticate() throws Exception {
        final URL url;

        url = new URL("https://www.google.com/accounts/ClientLogin");

        final String authheader = "accountType=GOOGLE&Email=secret&Passwd=secret&service=analytics";

        final URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        final OutputStream outputStream = connection.getOutputStream();
        final BufferedReader rd;
        final StringBuffer stringBuffer;
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        outputStreamWriter.write(authheader);
        outputStreamWriter.flush();
        rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        stringBuffer = new StringBuffer();
        String line;
        while ((line = rd.readLine()) != null) {
            stringBuffer.append(line);
        }

        rd.close();

        token = stringBuffer.toString();
        token = token.substring(token.indexOf("Auth="));

        return token;

    }

    public void getData(String test) {
        Page p1 = mngr.findOrCreatePageByNameAndGroup("Awards", "About");
        p1.addPageViews(100);

        Page p2 = mngr.findOrCreatePageByNameAndGroup("Campus", "About");
        p2.addPageViews(100);

        Page p3 = mngr.findOrCreatePageByNameAndGroup("Contact", "About");
        p3.addPageViews(100);

        Page p4 = mngr.findOrCreatePageByNameAndGroup("Facts", "About");
        p4.addPageViews(100);

        Page p5 = mngr.findOrCreatePageByNameAndGroup("History", "About");
        p5.addPageViews(100);

        Page p6 = mngr.findOrCreatePageByNameAndGroup("Hoboken", "About");
        p6.addPageViews(100);

        Page p7 = mngr.findOrCreatePageByNameAndGroup("Home", "About");
        p7.addPageViews(100);

        Page p8 = mngr.findOrCreatePageByNameAndGroup("Mission", "About");
        p8.addPageViews(100);

        Page p9 = mngr.findOrCreatePageByNameAndGroup("Railroad", "About");
        p9.addPageViews(100);

        Page p10 = mngr.findOrCreatePageByNameAndGroup("Stevens", "About");
        p10.addPageViews(100);

        Page p11 = mngr.findOrCreatePageByNameAndGroup("Trustees", "About");
        p11.addPageViews(100);

        Page p12 = mngr.findOrCreatePageByNameAndGroup("Visit", "About");
        p12.addPageViews(100);

        Page p13 = mngr.findOrCreatePageByNameAndGroup("Home", "Academics");
        p13.addPageViews(100);

        Page p135 = mngr.findOrCreatePageByNameAndGroup("Test", "Academics");
        p135.addPageViews(100);

        Page p14 = mngr.findOrCreatePageByNameAndGroup("Admissions", "Administration");
        p14.addPageViews(100);

        Page p15 = mngr.findOrCreatePageByNameAndGroup("Athletics", "Administration");
        p15.addPageViews(100);

        Page p16 = mngr.findOrCreatePageByNameAndGroup("Campus Store", "Administration");
        p16.addPageViews(100);

        Page p17 = mngr.findOrCreatePageByNameAndGroup("Financial", "Administration");
        p17.addPageViews(100);

        Page p18 = mngr.findOrCreatePageByNameAndGroup("Graduates", "Administration");
        p18.addPageViews(100);

        Page p19 = mngr.findOrCreatePageByNameAndGroup("Home", "Administration");
        p19.addPageViews(100);

        Page p20 = mngr.findOrCreatePageByNameAndGroup("Human Resources", "Administration");
        p20.addPageViews(100);

        Page p21 = mngr.findOrCreatePageByNameAndGroup("Marketing", "Administration");
        p21.addPageViews(100);

        Page p22 = mngr.findOrCreatePageByNameAndGroup("Residence Life", "Administration");
        p22.addPageViews(100);

        Page p23 = mngr.findOrCreatePageByNameAndGroup("Special Functions", "Administration");
        p23.addPageViews(100);

        Page p24 = mngr.findOrCreatePageByNameAndGroup("Student Development", "Administration");
        p24.addPageViews(100);

        Page p25 = mngr.findOrCreatePageByNameAndGroup("Student Services", "Administration");
        p25.addPageViews(100);
        
        p25.addToPage(p24, 100);

    }

    public void getData(Date date) throws Exception {
        Document total_doc;
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(ctx);

        String sdate = gaFormat.format(date);

        File file = new File("datastore/" + sdate + " t0.xml");
        if (!file.exists()) {
            if(token==null){
                Authenticate();
            }
            URL total_url = URI.create("https://www.google.com/analytics/feeds/data?ids=ga%3A13414079&dimensions=ga%3Ahostname%2Cga%3ApagePath&metrics=ga%3Apageviews&sort=-ga%3Apageviews&start-date=" + sdate + "&end-date=" + sdate + "&start-index=1&max-results=0").toURL();
            URLConnection total_urlconn = total_url.openConnection();
            total_urlconn.addRequestProperty("Authorization", "GoogleLogin " + token);

            InputStream total_istream = total_urlconn.getInputStream();
            total_doc = builder.parse(total_istream);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(total_doc);
            StreamResult result = new StreamResult(new File("datastore/" + sdate + " t0.xml"));

            transformer.transform(source, result);

        } else {
            total_doc = builder.parse("datastore/" + sdate + " t0.xml");
        }
        XPathExpression total_expr = xpath.compile("//openSearch:totalResults/text()");
        NodeList totalrows_nodes = (NodeList) total_expr.evaluate(total_doc, XPathConstants.NODESET);
        int totalrows = Integer.parseInt(totalrows_nodes.item(0).getNodeValue());
        pageviews = new Integer[totalrows];
        pagepaths = new String[totalrows];

        for (int start = 1; start < totalrows; start += 10000) {

            Document doc;

            file = new File("datastore/" + sdate + " t" + start + ".xml");
            if (!file.exists()) {
                URL data_url = URI.create("https://www.google.com/analytics/feeds/data?ids=ga%3A13414079&dimensions=ga%3Ahostname%2Cga%3ApagePath&metrics=ga%3Apageviews&sort=-ga%3Apageviews&start-date=" + sdate + "&end-date=" + sdate + "&start-index=" + start + "&max-results=10000").toURL();
                URLConnection data_conn = data_url.openConnection();
                data_conn.addRequestProperty("Authorization", "GoogleLogin " + token);
                data_conn.addRequestProperty("GData-Version", "2");
                final InputStream data_istream = data_conn.getInputStream();
                doc = builder.parse(data_istream); //parse(data_istream);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("datastore/" + sdate + " t" + start + ".xml"));

                transformer.transform(source, result);

            } else {

                doc = builder.parse("datastore/" + sdate + " t" + start + ".xml");

            }

            XPathExpression data_pageviews_expr = xpath.compile("//dxp:metric[@name='ga:pageviews']/@value");
            XPathExpression data_pagepaths_expr = xpath.compile("//dxp:dimension[@name='ga:pagePath']/@value");
            XPathExpression data_hostname_expr = xpath.compile("//dxp:dimension[@name='ga:hostname']/@value");

            NodeList data_pageviews_nodes = (NodeList) data_pageviews_expr.evaluate(doc, XPathConstants.NODESET);
            NodeList data_pagepaths_nodes = (NodeList) data_pagepaths_expr.evaluate(doc, XPathConstants.NODESET);
            NodeList data_hostname_nodes = (NodeList) data_hostname_expr.evaluate(doc, XPathConstants.NODESET);


            for (int j = 0; j < data_pagepaths_nodes.getLength(); j++) {
                Page p = mngr.findOrCreatePageByURL(data_hostname_nodes.item(j).getNodeValue() + data_pagepaths_nodes.item(j).getNodeValue());
                if (p != null) {
                    p.addPageViews(Integer.parseInt(data_pageviews_nodes.item(j + 1).getNodeValue()));
                }

            }

        }

        file = new File("datastore/" + sdate + " p0.xml");
        if (!file.exists()) {
            URL total_url = URI.create("https://www.google.com/analytics/feeds/data?ids=ga%3A13414079&dimensions=ga%3Ahostname%2Cga%3ApreviousPagePath%2Cga%3AnextPagePath&metrics=ga%3Apageviews&sort=-ga%3Apageviews&start-date=" + sdate + "&end-date=" + sdate + "&start-index=1&max-results=0").toURL();
            URLConnection total_urlconn = total_url.openConnection();
            total_urlconn.addRequestProperty("Authorization", "GoogleLogin " + token);

            InputStream total_istream = total_urlconn.getInputStream();
            total_doc = builder.parse(total_istream);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(total_doc);
            StreamResult result = new StreamResult(new File("datastore/" + sdate + " p0.xml"));
            transformer.transform(source, result);

        } else {
            total_doc = builder.parse("datastore/" + sdate + " p0.xml");
        }
        total_expr = xpath.compile("//openSearch:totalResults/text()");
        totalrows_nodes = (NodeList) total_expr.evaluate(total_doc, XPathConstants.NODESET);
        totalrows = Integer.parseInt(totalrows_nodes.item(0).getNodeValue());

        prePaths = new String[totalrows];
        hostPaths = new String[totalrows];
        nextPaths = new String[totalrows];
        pathViews = new Integer[totalrows];

        for (int start = 1; start < totalrows; start += 10000) {
            Document doc;
            file = new File("datastore/" + sdate + " p" + start + ".xml");
            if (!file.exists()) {
                URL data_url = URI.create("https://www.google.com/analytics/feeds/data?ids=ga%3A13414079&dimensions=ga%3Ahostname%2Cga%3ApreviousPagePath%2Cga%3AnextPagePath&metrics=ga%3Apageviews&sort=-ga%3Apageviews&start-date=" + sdate + "&end-date=" + sdate + "&start-index=" + start + "&max-results=10000").toURL();
                URLConnection data_conn = data_url.openConnection();
                data_conn.addRequestProperty("Authorization", "GoogleLogin " + token);
                data_conn.addRequestProperty("GData-Version", "2");
                final InputStream data_istream = data_conn.getInputStream();
                doc = builder.parse(data_istream);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("datastore/" + sdate + " p" + start + ".xml"));
                transformer.transform(source, result);
            } else {
                doc = builder.parse("datastore/" + sdate + " p" + start + ".xml");
            }

            XPathExpression data_pageviews_expr = xpath.compile("//dxp:metric[@name='ga:pageviews']/@value");
            XPathExpression data_pageprev_expr = xpath.compile("//dxp:dimension[@name='ga:previousPagePath']/@value");
            XPathExpression data_pagenext_expr = xpath.compile("//dxp:dimension[@name='ga:nextPagePath']/@value");
            XPathExpression data_hostname_expr = xpath.compile("//dxp:dimension[@name='ga:hostname']/@value");

            NodeList data_pageviews_nodes = (NodeList) data_pageviews_expr.evaluate(doc, XPathConstants.NODESET);
            NodeList data_prePaths_nodes = (NodeList) data_pageprev_expr.evaluate(doc, XPathConstants.NODESET);
            NodeList data_nextPaths_nodes = (NodeList) data_pagenext_expr.evaluate(doc, XPathConstants.NODESET);
            NodeList data_hostName_nodes = (NodeList) data_hostname_expr.evaluate(doc, XPathConstants.NODESET);

            for (int j = 0; j < data_prePaths_nodes.getLength(); j++) {
                Page from = mngr.findOrCreatePageByURL(data_hostName_nodes.item(j).getNodeValue() + data_prePaths_nodes.item(j).getNodeValue());
                Page to = mngr.findOrCreatePageByURL(data_hostName_nodes.item(j).getNodeValue() + data_nextPaths_nodes.item(j).getNodeValue());

                if (from != null && to != null) {
                    from.addToPage(to, Integer.parseInt(data_pageviews_nodes.item(j).getNodeValue()));
                    //to.addFromPage(from, Integer.parseInt(data_pageviews_nodes.item(j).getNodeValue()));
                }

            }

        }
    }
    static NamespaceContext ctx = new NamespaceContext() {

        @Override
        public String getNamespaceURI(String prefix) {
            String uri;
            switch (prefix) {
                case "dxp":
                    uri = "http://schemas.google.com/analytics/2009";
                    break;
                case "openSearch":
                    uri = "http://a9.com/-/spec/opensearchrss/1.0/";
                    break;
                case "ns1":
                    uri = "http://www.w3.org/2005/Atom";
                    break;
                default:
                    uri = null;
                    break;
            }
            return uri;
        }

        @Override
        public String getPrefix(String namespaceURI) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Iterator getPrefixes(String namespaceURI) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };
}
