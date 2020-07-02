package sc.system.mapper;

import sc.system.model.WebScBureau;

public interface BureauMapper {
    int deleteByPrimaryKey(String bureauId);

    int insert(WebScBureau record);

    int insertSelective(WebScBureau record);

    WebScBureau selectByPrimaryKey(String bureauId);

    int updateByPrimaryKeySelective(WebScBureau record);

    int updateByPrimaryKey(WebScBureau record);
}