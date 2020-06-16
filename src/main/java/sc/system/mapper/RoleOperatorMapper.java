package sc.system.mapper;

import sc.system.model.WebScRoleOperator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoleOperatorMapper {
    int insert(WebScRoleOperator roleOperator);

    Integer[] getOperatorsByRoleId(Integer roleId);

    int deleteByRoleId(@Param("roleId") Integer roleId);

    int insertRoleOperators(@Param("roleId") Integer roleId, @Param("operatorIds") Integer[] operatorIds);

    int deleteByOperatorId(@Param("operatorId") Integer operatorId);
}