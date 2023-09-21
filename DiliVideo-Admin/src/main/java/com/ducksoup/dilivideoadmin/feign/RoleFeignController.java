package com.ducksoup.dilivideoadmin.feign;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import com.ducksoup.dilivideoadmin.entity.RoleApplication;
import com.ducksoup.dilivideoadmin.service.RoleApplicationService;
import com.ducksoup.dilivideoentity.admin.params.RoleApplicationParam;
import com.ducksoup.dilivideoentity.constant.CONSTANT_STATUS;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feign")
public class RoleFeignController {

    private RoleApplicationService roleApplicationService;

    /**
     * 请求某一角色
     * @param param RoleApplicationParam
     * @see RoleApplicationParam
     * @return boolean
     */
    @PostMapping("/role/application")
    public boolean RoleApplicationAction(@RequestBody RoleApplicationParam param) {

        RoleApplication roleApplication = new RoleApplication();
        roleApplication.setId(UUID.randomUUID().toString());
        roleApplication.setApplicantId(param.getApplicantId());
        roleApplication.setRoleName(param.getRoleName());
        roleApplication.setCreateTime(DateTime.now());

        //设置状态为待审批
        roleApplication.setCurrentStatus(CONSTANT_STATUS.WAITING_FOR_APPROVAL);
        roleApplication.setStatus(1);
        roleApplication.setReason(param.getReason());


        return roleApplicationService.save(roleApplication);
    }

}
