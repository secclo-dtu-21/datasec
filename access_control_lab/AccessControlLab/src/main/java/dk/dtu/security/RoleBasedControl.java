package dk.dtu.security;

import dk.dtu.util.repository.RoleRepository;
import dk.dtu.util.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RoleBasedControl implements AccessControlModel{

    public static UserRepository userRepository = new UserRepository();
    public static RoleRepository roleRepository = new RoleRepository();
    private static final Logger logger = LogManager.getLogger(AccessControlModel.class);

    @Override
    public boolean isMethodGranted(String username, String method) {
        Set<String> roleSet = new HashSet<>();
        String userRoleByName = userRepository.getUserRoleByName(username);
        if (!userRoleByName.contains("&"))
            roleSet.add(userRoleByName);
        else
            roleSet.addAll(Arrays.stream(userRoleByName.split("&")).toList());
        boolean result = isMethodGrantedForRole(method, roleSet);
        if (result)
            logger.info(String.format("User \'%s\' with role \'%s\' is allowed to use service \'%s\'", username, userRoleByName, method));
        else
            logger.info(String.format("User \'%s\' with role \'%s\' is forbidden to use service \'%s\'", username, userRoleByName, method));
        return result;
    }

    private boolean isMethodGrantedForRole(String method, Set<String> roleSet) {
        for (var i : roleSet) {
            Map<String, Boolean> accessControlListByRole = roleRepository.getAccessControlListByRole(i);
            if (accessControlListByRole.get(method))
                return true;
        }

        return false;
    }
}
