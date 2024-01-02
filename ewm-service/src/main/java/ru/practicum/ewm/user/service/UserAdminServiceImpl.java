package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotSaveException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.UtilService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {

    private final UserRepository userRepository;
    private final UtilService utilService;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));
        if (ids == null) {
            return UserMapper.convertUserListToUserDTOList(
                    userRepository.findAll(page).getContent());
        } else {
            return UserMapper.convertUserListToUserDTOList(
                    userRepository.findAllByIdIn(ids, page));
        }
    }

    @Override
    public UserDto saveUser(NewUserRequest newUserRequest) {
        try {
            User user = userRepository.save(
                    UserMapper.toUserFromNewDto(newUserRequest));
            return UserMapper.toUserDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new NotSaveException("Пользователь не был создан: " + newUserRequest);
        }
    }

    @Override
    public void deleteUserById(Long userId) {
        utilService.returnUser(userId);
        userRepository.deleteById(userId);
    }

}