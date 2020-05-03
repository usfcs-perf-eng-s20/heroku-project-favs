package cs.usfca.edu.histfavcheckout.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoCacheRepository extends CrudRepository<UserInfoResponse.UserInfo, Integer> {

}
