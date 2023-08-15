package com.ducksoup.dilivideoauth.mainServices;

import java.util.List;

public interface AuthService {

    List<String> getPermissionList(Object loginId);

    List<String> getRoleList(Object loginId);

    List<String> getRoleIds(String loginId);

}
