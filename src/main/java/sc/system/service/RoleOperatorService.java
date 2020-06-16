package sc.system.service;

import sc.system.mapper.RoleOperatorMapper;
import sc.system.model.WebScRoleOperator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RoleOperatorService {

    @Resource
    private RoleOperatorMapper roleOperatorMapper;

    public int insert(WebScRoleOperator roleOperator) {
        return roleOperatorMapper.insert(roleOperator);
    }

}
