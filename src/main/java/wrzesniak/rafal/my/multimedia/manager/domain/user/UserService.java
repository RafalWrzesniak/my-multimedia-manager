package wrzesniak.rafal.my.multimedia.manager.domain.user;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public String getCurrentUsername() {
        return "Windxore";
//        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
