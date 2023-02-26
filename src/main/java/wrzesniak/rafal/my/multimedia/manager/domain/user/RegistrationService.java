package wrzesniak.rafal.my.multimedia.manager.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wrzesniak.rafal.my.multimedia.manager.config.security.LoginCredentials;
import wrzesniak.rafal.my.multimedia.manager.domain.content.BookContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.GameContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.content.MovieContentList;
import wrzesniak.rafal.my.multimedia.manager.domain.error.UserAlreadyExistException;
import wrzesniak.rafal.my.multimedia.manager.domain.mapper.DtoMapper;

import static wrzesniak.rafal.my.multimedia.manager.domain.user.UserRole.ADMIN;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RegistrationService {

    private static final String TO_WATCH = "Do oglądnięcia";
    public static final String ALL_MOVIES = "Wszystkie filmy";
    public static final String ALL_BOOKS = "Wszystkie książki";
    public static final String ALL_GAMES = "Wszystkie gry";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerNewUserAccount(LoginCredentials credentials) throws UserAlreadyExistException {
        if(userRepository.findByUsername(credentials.getUsername()).isPresent()) {
            throw new UserAlreadyExistException();
        }
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        User user = DtoMapper.mapToUser(credentials);
        addDefaultListsFor(user);
        User savedUser = userRepository.save(user);
        log.info("New user register successfully: {}", savedUser);
        return savedUser;
    }

    private void addDefaultListsFor(User user) {
        MovieContentList toWatchList = new MovieContentList(TO_WATCH);
        toWatchList.setToWatchList(true);
        user.getMovieLists().add(toWatchList);

        MovieContentList allMovies = new MovieContentList(ALL_MOVIES);
        allMovies.setAllContentList(true);
        user.getMovieLists().add(allMovies);

        BookContentList allBooks = new BookContentList(ALL_BOOKS);
        allBooks.setAllContentList(true);
        user.getBookLists().add(allBooks);

        GameContentList allGames = new GameContentList(ALL_GAMES);
        allGames.setAllContentList(true);
        user.getGameLists().add(allGames);
    }

    public User registerAdminUser(LoginCredentials credentials) throws UserAlreadyExistException {
        if(userRepository.findByUsername(credentials.getUsername()).isPresent()) {
            throw new UserAlreadyExistException();
        }
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        User user = DtoMapper.mapToUser(credentials);
        user.setUserRole(ADMIN);
        return userRepository.save(user);
    }

}
