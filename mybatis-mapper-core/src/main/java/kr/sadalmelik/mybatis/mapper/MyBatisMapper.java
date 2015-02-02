package kr.sadalmelik.mybatis.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.sadalmelik.mybatis.mapper.util.MybatisReloader;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.session.*;

import java.io.IOException;
import java.util.HashMap;

public class MyBatisMapper {
    private SqlSessionFactory sqlSessionFactory;
    private ObjectMapper mapper;

    public MyBatisMapper(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.mapper = new ObjectMapper();

        try {
            new MybatisReloader(sqlSessionFactory.getConfiguration()).reload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String bind(String id, String paramterJson) {
        StringBuilder result = new StringBuilder();

        SqlSession sqlSession = sqlSessionFactory.openSession();
        Configuration configuration = sqlSession.getConfiguration();
        MappedStatement ms = configuration.getMappedStatement(id);

        Object paramterObject = convertToParamObject(paramterJson, ms.getParameterMap().getType());
        StatementType statementType = ms.getStatementType();

        //결과 SQL문 찍어주기
        BoundSql boundSql = ms.getBoundSql(paramterObject);

        result.append("변환된 SQL : " + "\n");
        result.append(boundSql.getSql() + "\n\n");


        if (statementType.equals(StatementType.PREPARED)) {
            sqlSession.select(id, paramterObject, new TempResultHandler(result));
        } else {
            int updateCount = sqlSession.update(id, paramterObject);
            result.append("updateCount : " + updateCount);
        }

        sqlSession.rollback();
        sqlSession.close();

        return result.toString();
    }

    private Object convertToParamObject(String paramterJson, Class paramterType) {
        Object paramterObject = null;
        if (paramterJson != null) {
            if (paramterType == null)
                paramterType = HashMap.class;

            try {
                paramterObject = mapper.readValue(paramterJson, paramterType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return paramterObject;
    }

    class TempResultHandler implements ResultHandler {
        private StringBuilder result;

        public TempResultHandler(StringBuilder result) {
            this.result = result;
        }

        @Override
        public void handleResult(ResultContext context) {
            result.append("SELECT실행!\n");
            result.append("총 갯수는.." + context.getResultCount() + "\n");
            result.append(context.getResultObject());
        }
    }


}


