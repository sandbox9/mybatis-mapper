package kr.sadalmelik.mybatis.mapper.util;

import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//xml을 Map<key, SqlSource>로 변환합니다.
public class CheetahXMLParser {

    private XMLLanguageDriver langDriver = new XMLLanguageDriver();
    private Configuration dummyConfiguration = new Configuration();

    public Map<String, SqlSource> generateCrudSqlSourceMap(File file) {
        //파일을 읽고 mapper node를 반환합니다.
        XNode mapperNode = extractMapperXNode(file);

        //mapper node에서 CRUD node를 SqlSource형태로 변환하여 반환합니다.
        Map<String, SqlSource> crudSqlSourceMap = extractCrudNodeMap(mapperNode);

        return crudSqlSourceMap;
    }

    private Map<String, SqlSource> extractCrudNodeMap(XNode mapperNode) {

        //include구문을 치환하는데 사용되는 sql을 뽑아냅니다.
        Map<String, Node> referenceSqlMap = extractIncludeRefNodeMap(mapperNode);

        //CRUD구문 뽑아내기.
        List<XNode> crudNodes = mapperNode.evalNodes("select|insert|update|delete");

        Map<String, SqlSource> crudSqlSourceMap = new HashMap<String, SqlSource>();
        for (XNode context : crudNodes) {
            replaceIncludeNode(referenceSqlMap, context);
            removeSelectKeyNode(context);

            String mapperId = context.getStringAttribute("id");
            SqlSource sqlSource = langDriver.createSqlSource(dummyConfiguration, context, null);

            crudSqlSourceMap.put(mapperId, sqlSource);
        }

        return crudSqlSourceMap;
    }

    private void removeSelectKeyNode(XNode context) {
        List<XNode> selectKeyNodes = context.evalNodes("selectKey");

        for (XNode selectKeyNode : selectKeyNodes) {
            context.getNode().removeChild(selectKeyNode.getNode());
        }
    }

    private void replaceIncludeNode(Map<String, Node> refSqlMap, XNode context) {
        List<XNode> includeNodes = context.evalNodes("include");
        for (XNode includeXNode : includeNodes) {
            String refId = includeXNode.getStringAttribute("refid");
            Node refNode = refSqlMap.get(refId);
            Node includeNode = includeXNode.getNode();
            includeNode.appendChild(refNode.getFirstChild());
            includeNode.getParentNode().insertBefore(includeNode.getFirstChild(), includeNode);
            includeNode.getParentNode().removeChild(includeNode);
        }
    }

    private Map<String, Node> extractIncludeRefNodeMap(XNode mapperNode) {
        List<XNode> refNodes = mapperNode.evalNodes("sql");

        Map<String, Node> refSqlMap = new HashMap<String, Node>();
        for (XNode context : refNodes) {
            refSqlMap.put(context.getStringAttribute("id"), context.getNode().cloneNode(true));
        }
        return refSqlMap;
    }

    private XNode extractMapperXNode(File file) {
        XPathParser xPathParser;
        try {
            InputStream inputStream = new FileInputStream(file);
            xPathParser = new XPathParser(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("not valid mapperPath : " + file, e);
        } catch (NullPointerException e){
            throw new RuntimeException("not valid mapperPath : " + file, e);
        }

        //XML 내부 매퍼 내부 추출.
        return xPathParser.evalNode("/mapper");
    }


}
