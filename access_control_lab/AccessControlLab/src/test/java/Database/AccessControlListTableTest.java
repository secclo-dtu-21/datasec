package Database;

import dk.dtu.util.repository.AccessControlRepository;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AccessControlListTableTest {

    private static AccessControlRepository accessControlRepository  = new AccessControlRepository();

    @Test
    public void getAccessControlListByNameTest() {
        Map<String, Boolean> accessControlList = accessControlRepository.getAccessControlListByName("Alice");
        accessControlList.forEach((k, v) -> assertThat(v).isTrue());
        assertThat(accessControlList.size()).isEqualTo(9);
    }


}
