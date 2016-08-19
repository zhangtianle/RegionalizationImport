package me.tianle.mapper;

import me.tianle.model.Area;
import me.tianle.model.City;
import me.tianle.model.Province;

import java.util.List;

/**
 * Created by Tianle Zhang on 2016/8/19.
 */
public interface MyMapper {

    int insertProvince(List<Province> province);

    int insertCity(List<City> city);

    int insertArea(List<Area> area);

    Area selectAreaByCode(String code);

    City selectCityByCode(String code);

    Province selectProvinceByCode(String code);

}
