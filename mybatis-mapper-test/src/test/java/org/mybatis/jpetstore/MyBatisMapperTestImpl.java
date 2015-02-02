package org.mybatis.jpetstore;

import kr.sadalmelik.mybatis.mapper.MyBatisMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class MyBatisMapperTestImpl extends MyBatisMapperTest{

    @Override
    public void run() {
        String watcherPath = "/Users/SejongPark/dev/workspace/intelliJ/mybatis-mapper/mybatis-mapper-test/src/main/resources/";
        MyBatisMapper.run(sqlSessionFactory, watcherPath);
    }

}
