package org.sindria.xpipe.lib.nanoREST.helpers;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.config.models.Config;
import org.sindria.xpipe.lib.nanoREST.config.models.Application;
import org.sindria.xpipe.lib.nanoREST.config.models.Datasource;
import org.sindria.xpipe.lib.nanoREST.config.models.Nanohttpd;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.*;

public class YamlHelper {

    public Config load(String file) throws FileNotFoundException {



//        Yaml yaml = new Yaml();
//        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(file);
//        System.out.println(inputStream);
//        Map<String, Object> obj = yaml.load(inputStream);
//        System.out.println(obj);



        //ClassLoader classLoader = YamlHelper.class.getClassLoader();


        //InputStream inputStream = classLoader.getResourceAsStream(filename);
        //String data = readFromInputStream(inputStream);

//        InputStream inputStream = null;
//        try {
//            File file = new File(classLoader.getResource(filename).getFile());
//            inputStream = new FileInputStream(file);
//
//            //...
//        }
//        finally {
//            if (inputStream != null) {
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(file);

//        InputStream inputStream = null;
//        try {
//            inputStream = new FileInputStream(new File(file));
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }

        System.out.println(new JSONObject(inputStream));
        Yaml yaml = new Yaml(new Constructor(Config.class, new LoaderOptions()));
        System.out.println("Debug:");
        System.out.println(yaml);



        System.out.println(new JSONObject(yaml));

        //Map<String, Object> obj = yaml.load(inputStream);
        //System.out.println(obj);

        Config data = yaml.load(inputStream);
        System.out.println(data);
        System.out.println("Dump:");
        System.out.println(new JSONObject(data));

        // TODO: implement

        Application application = data.getNanorest().getApplication();
        Datasource datasource = data.getNanorest().getDatasource();
        Nanohttpd nanohttpd = data.getNanorest().getNanohttpd();

        String name = application.getName();

        System.out.println("TODO: implement debug");
        System.out.println(name);

        Pattern pattern = Pattern.compile("\\$*\\{(.*):([a-zA-Z]+)}");
        Matcher matcher = pattern.matcher(name);

        if (matcher.find())
        {
            String env = matcher.group(1);
            System.out.println(env);
            System.out.println("sticazzi");

            String value = System.getenv(env);
            System.out.println(value);


            if (value == null) {
                value = matcher.group(2);
                System.out.println(value);
            }

            application.setName(value);
        }




        System.out.println("Result:");
        System.out.println(application.getName());







        return data;

        //return obj;
    }
}
