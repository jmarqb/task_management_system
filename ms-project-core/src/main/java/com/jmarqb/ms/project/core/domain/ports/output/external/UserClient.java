package com.jmarqb.ms.project.core.domain.ports.output.external;

import java.util.List;

public interface UserClient {

	List<User> checkUsers(List<Long> usersIds);
}
