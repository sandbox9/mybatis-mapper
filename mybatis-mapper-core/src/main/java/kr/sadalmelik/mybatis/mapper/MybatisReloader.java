package kr.sadalmelik.mybatis.mapper;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

public class MybatisReloader {
    private Configuration configuration;
    private CheetahXMLParser cheetahXMLParser = new CheetahXMLParser();
    private List<WatchTarget> watchTargets;

    public MybatisReloader(Configuration configuration) {
        this.configuration = configuration;
    }

    //xml파일을 읽은 뒤 파일이 변경될 경우 리로드합니다.
    public void reload() throws Exception {
        Field loadedResourcesFiled = Configuration.class.getDeclaredField("loadedResources");
        loadedResourcesFiled.setAccessible(true);
        Set<String> rawLoadedResourceSet = (Set<String>) loadedResourcesFiled.get(configuration);

        watchTargets = new ArrayList<WatchTarget>();

        //감시하는 파일 리스트
        for (String rawLoadedResource : rawLoadedResourceSet) {
            if (rawLoadedResource.endsWith(".xml")) {
                URL fileUrl = ClassLoader.getSystemResource(rawLoadedResource);
                File file = new File(fileUrl.getFile());

                watchTargets.add(new WatchTarget(rawLoadedResource, file, file.lastModified()));
            }
        }

        //이쯤되면... 쓰래드를 시작한다.
        Thread thread = new ResourceWatcher(watchTargets);
        thread.start();
    }


    //파일 왓쳐
    private class ResourceWatcher extends Thread {

        private List<WatchTarget> watchTargets;

        public ResourceWatcher(List<WatchTarget> watchTargetses) {
            this.watchTargets = watchTargetses;
        }

        @Override
        public void run() {
            try {
                //쓰래드는 항상 작동합니다.
                while (true) {

                    boolean isFileChange = false;
                    WatchTarget changedWatchTarget = null;

                    while (!isFileChange) {
                        for (WatchTarget watchTarget : watchTargets) {
                            long originalLastModified = watchTarget.getLastModified();
                            if (originalLastModified != watchTarget.getFile().lastModified()) {
                                isFileChange = true;
                                changedWatchTarget = watchTarget;
                                changedWatchTarget.setLastModified(watchTarget.getFile().lastModified());
                            }

                        }

                        Thread.sleep(1000);
                    }

                    System.out.println("파일이 변경되었습니다. --> " + changedWatchTarget.getFilePath());

                    Map<String, SqlSource> crudMap = cheetahXMLParser.generateCrudSqlSourceMap(changedWatchTarget.getFile());
                    //기존에 존재하던 sqlSource는 삭제.
                    for (String sqlId : crudMap.keySet()) {
                        MappedStatement ms = configuration.getMappedStatement(sqlId);

                        Field sqlSourceField = MappedStatement.class.getDeclaredField("sqlSource");
                        sqlSourceField.setAccessible(true);
                        sqlSourceField.set(ms, crudMap.get(sqlId));
                    }

                    configuration.setCacheEnabled(false);
                }
            } catch (InterruptedException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class WatchTarget {
        private String filePath;
        private File file;
        private long lastModified;

        public WatchTarget(String filePath, File file, long lastModified) {
            this.filePath = filePath;
            this.lastModified = lastModified;
            this.file = file;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }
    }
}
