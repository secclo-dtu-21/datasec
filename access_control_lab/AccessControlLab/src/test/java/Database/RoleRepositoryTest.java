package Database;

import dk.dtu.util.repository.RoleRepository;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RoleRepositoryTest {

    private static final RoleRepository roleRepository  = new RoleRepository();

    @Test
    public void getAccessControlListByNameTest() {
        Map<String, Boolean> accessControlList = roleRepository.getAccessControlListByRole("manager");
        accessControlList.forEach((k, v) -> assertThat(v).isTrue());
        assertThat(accessControlList.size()).isEqualTo(9);
    }

}
