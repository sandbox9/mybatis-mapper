package org.mybatis.jpetstore;

import kr.sadalmelik.mybatis.mapper.MyBatisMapper;
import kr.sadalmelik.mybatis.mapper.MyBatisMapperClient;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/context/applicationContext.xml"})
public class MyBatisMapperTest{

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Test
    public void test() throws InterruptedException {

        MyBatisMapper myBatisMapper = new MyBatisMapper(sqlSessionFactory);
        new MyBatisMapperClient(myBatisMapper).run();

        Thread.sleep(1000000000L);
    }
}
