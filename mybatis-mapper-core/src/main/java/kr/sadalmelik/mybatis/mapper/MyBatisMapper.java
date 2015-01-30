package kr.sadalmelik.mybatis.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    }

    //TODO 단순화
    public void bind(String id, String paramterJson) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Configuration configuration = sqlSession.getConfiguration();
        MappedStatement ms = configuration.getMappedStatement(id);

        Object paramterObject = convertToParamObject(paramterJson, ms.getParameterMap().getType());

        StatementType statementType = ms.getStatementType();
        if (statementType.equals(StatementType.PREPARED))
            sqlSession.select(id, paramterObject, new TempResultHandler());
        else
            sqlSession.update(id, paramterObject);

        sqlSession.rollback();
        sqlSession.close();
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
        @Override
        public void handleResult(ResultContext context) {
            System.out.println(context.getResultCount());
        }
    }

}


