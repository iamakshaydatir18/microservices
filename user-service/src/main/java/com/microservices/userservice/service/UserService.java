package com.microservices.userservice.service;

import com.microservices.userservice.client.FileStorageClient;
import com.microservices.userservice.enums.Active;
import com.microservices.userservice.enums.Role;
import com.microservices.userservice.exc.NotFoundException;
import com.microservices.userservice.model.User;
import com.microservices.userservice.model.UserDetails;
import com.microservices.userservice.repository.UserRepository;
import com.microservices.userservice.request.RegisterRequest;
import com.microservices.userservice.request.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageClient fileStorageClient;
    private final ModelMapper modelMapper;

    public User saveUser(RegisterRequest request) {
        User toSave = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.USER)
                .active(Active.ACTIVE).build();
        return userRepository.save(toSave);
    }

    public List<User> getAll() {
        System.out.println("Called get All users from database....");
        return userRepository.findAllByActive(Active.ACTIVE);
    }

    public User getUserById(String id) {
        System.out.println("called get by ID from database...");
        return findUserById(id);
    }

    public User getUserByEmail(String email) {
        return findUserByEmail(email);
    }

    public void deleteUsers(){
        System.out.println("delete all users...");
        userRepository.deleteAll();
    }

    public User getUserByUsername(String username) {

        System.out.println("called get by username from database...");
        return findUserByUsername(username);
    }

    public User updateUserById(UserUpdateRequest request, MultipartFile file) {
        User toUpdate = findUserById(request.getId());

        request.setUserDetails(updateUserDetails(toUpdate.getUserDetails(), request.getUserDetails(), file));
        modelMapper.map(request, toUpdate);

        return userRepository.save(toUpdate);
    }

    public void deleteUserById(String id) {
        User toDelete = findUserById(id);
        toDelete.setActive(Active.INACTIVE);
        userRepository.save(toDelete);
    }

    protected User findUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    protected User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    protected User findUserByUsername(String username) {

        System.out.println("called getbyusername from database...");
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private UserDetails updateUserDetails(UserDetails toUpdate, UserDetails request, MultipartFile file) {
        toUpdate = toUpdate == null ? new UserDetails() : toUpdate;
        if (file != null) {

            String profilePicture = fileStorageClient.uploadImageToFIleSystem(file).getBody();
            if (profilePicture != null) {
                System.out.println("get toUpdate.getprofilePicture -- "+ toUpdate.getProfilePicture());
                System.out.println("profile picture is -- "+ profilePicture.toString());
                if(toUpdate.getProfilePicture() != null){
                    fileStorageClient.deleteImageFromFileSystem(toUpdate.getProfilePicture());
                }
                System.out.println("The user is first time uploading picture..");
                toUpdate.setProfilePicture(profilePicture);
            }
        }

        modelMapper.map(request, toUpdate);

        return toUpdate;
    }
}