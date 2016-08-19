package me.tianle;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import me.tianle.mapper.MyMapper;
import me.tianle.model.Area;
import me.tianle.model.City;
import me.tianle.model.Province;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Tianle Zhang on 2016/8/19.
 */
public class ReadData {


    private static int pId = 0;
    private static List<Province> provinces = new ArrayList<Province>();

    private static int cId = 0;
    private static List<City> cities = new ArrayList<City>();

    private static int aId = 0;
    private static List<Area> areas = new ArrayList<Area>();

    public static void main(String[] args) throws Exception {
        /* 读入TXT文件 */
        String pathname = "D:\\project\\idea\\GB2260.java-master\\src\\main\\resources\\data\\data2";
        File filename = new File(pathname); // 要读取以上路径的input。txt文件
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename)); // 建立一个输入流对象reader
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
        String line = "";
        int i = 0;
        while ((line = br.readLine()) != null) {
            System.out.println(++i);
            String[] strings = line.split("    ");
//          String[] strings = line.split("\\s+");
            String code = strings[0].trim();
            String cname = strings[1].trim();
            code = code.replace("　","");
            cname = cname.replace("　","");
            matcherProvince(code, cname);
        }
        myBaitsInsert();
        System.out.println("ok");
    }

    public static void myBaitsInsert() throws Exception {
        String resource = "configuration.xml";
        Reader reader = Resources.getResourceAsReader(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
                .build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        MyMapper mapper = sqlSession.getMapper(MyMapper.class);

        if (areas.size() > 0) {
            mapper.insertArea(areas);
        }
        if (cities.size() > 0) {
            mapper.insertCity(cities);
        }

        if (provinces.size() > 0) {
            mapper.insertProvince(provinces);
        }
        sqlSession.close();

    }

    private static void matcherProvince(String code, String cname) throws PinyinException, IOException {


        String resource = "configuration.xml";
        Reader reader = Resources.getResourceAsReader(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
                .build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        MyMapper mapper = sqlSession.getMapper(MyMapper.class);

        String ename = PinyinHelper.convertToPinyinString(cname, "", PinyinFormat.WITHOUT_TONE);

        Pattern p = Pattern.compile("[0-9]{2}0{4}");
        Pattern c = Pattern.compile("[0-9]{4}0{2}");
        Matcher mp = p.matcher(code);
        Matcher mc = c.matcher(code);
        if (mp.matches()) {
            Province province = new Province();
            province.setEname(ename);
            province.setCname(cname);
            province.setCode(code);

            Province province1 = mapper.selectProvinceByCode(code);
            if (province1 == null) {
                provinces.add(province);
            }
        } else if (mc.matches()) {
            City city = new City();
            city.setEname(ename);
            city.setCname(cname);
            city.setCode(code);
            city.setProvinceCode(code.substring(0,2)+"0000");

            City city1 = mapper.selectCityByCode(code);
            if (city1 == null) {
                cities.add(city);
            }
        } else {
            Area area = new Area();
            area.setEname(ename);
            area.setCname(cname);
            area.setCode(code);
            area.setCityCode(code.substring(0,4)+"00");

            Area eArea = mapper.selectAreaByCode(code);
            if (eArea == null) {
                areas.add(area);
            }
        }

        sqlSession.close();

    }


}
