package in.knoxdev.ecom.service;

import in.knoxdev.ecom.model.User;
import in.knoxdev.ecom.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
//    private List<User> userList = new ArrayList<>();
//    private Long nextId = 1L;

    public List<User> fetchAllUsers()
    {
        return userRepository.findAll();
    }

    public void addUser(User user){
//        user.setId(nextId++);
        userRepository.save(user);

    }

    public Optional<User> fetchUser(Long id) {
        return  userRepository.findById(id);
    }

    public boolean updateUser(Long id,User updatedUser){
        return   userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setLastName(updatedUser.getLastName());
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setPhone(updatedUser.getPhone());
                    existingUser.setAddress(updatedUser.getAddress());
                    userRepository.save(existingUser);
                    return true;
                }).orElse(false);
    }
}
