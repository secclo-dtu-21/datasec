package dk.dtu.security;

import dk.dtu.util.repository.AccessControlRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class AccessControlList implements AccessControlModel {

    public static AccessControlRepository accessControlRepository = new AccessControlRepository();
    private static final Logger logger = LogManager.getLogger(AccessControlList.class);

    @Override
    public boolean isMethodGranted(String username, String method) {
        Map<String, Boolean> accessControlListByName = accessControlRepository.getAccessControlListByName(username);
        boolean result = accessControlListByName.get(method);
        if (result)
            logger.info(String.format("User \'%s\' is allowed to use service \'%s\'", username, method));
        else
            logger.info(String.format("User \'%s\' is forbidden to use service \'%s\'", username, method));
        return result;
    }

}
