package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wrzesniak.rafal.my.multimedia.manager.config.security.LoginCredentials;
import wrzesniak.rafal.my.multimedia.manager.domain.error.UserAlreadyExistException;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.DtoMapper;

import static wrzesniak.rafal.my.multimedia.manager.domain.user.UserRole.ADMIN;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerNewUserAccount(LoginCredentials credentials) throws UserAlreadyExistException {
        if(userRepository.findByUsername(credentials.getUsername()).isPresent()) {
            throw new UserAlreadyExistException("There is an account with username: " + credentials.getUsername());
        }
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        User user = DtoMapper.mapToUser(credentials);
        User savedUser = userRepository.save(user);
        log.info("New user register successfully: {}", savedUser);
        return savedUser;
    }

    public User registerAdminUser(LoginCredentials credentials) throws UserAlreadyExistException {
        if(userRepository.findByUsername(credentials.getUsername()).isPresent()) {
            throw new UserAlreadyExistException("There is an account with username: " + credentials.getUsername());
        }
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        User user = DtoMapper.mapToUser(credentials);
        user.setUserRole(ADMIN);
        return userRepository.save(user);
    }

}
