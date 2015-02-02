package learningtest;

import java.net.URL;

public class GetSystemPath{
    private static String pathname = ".";//

    public static void main(String[] args){
        URL resource = GetSystemPath.class.getResource("/");
        String path = resource.getPath();

        System.out.println(path);
        System.out.println(System.getProperty("user.dir"));
    }
}
