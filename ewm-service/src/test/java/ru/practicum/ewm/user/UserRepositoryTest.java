package ru.practicum.ewm.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.BaseDataJpaTest;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class UserRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private UserRepository userRepository;

    private final User user1 = new User();
    private final User user2 = new User();

    @BeforeEach
    public void addUsers() {
        user1.setEmail("email1@mail.ru");
        user1.setName("name 1");
        user2.setEmail("email2@mail.ru");
        user2.setName("name 2");

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @AfterEach
    public void deleteUsers() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("получены все пользователи с ид из списка ид, когда вызвана, " +
            "то получен список пользователей")
    void findAllByOwnerId() {
        List<User> actualUsers = userRepository.findAllByIdIn(List.of(user1.getId()), PageRequest.of(0, 5));

        assertThat(1, equalTo(actualUsers.size()));
        assertThat(user1, equalTo(actualUsers.get(0)));
    }

}