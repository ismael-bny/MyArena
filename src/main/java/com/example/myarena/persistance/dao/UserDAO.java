package com.example.myarena.persistance.dao;
import com.example.myarena.domain.User;
import com.example.myarena.domain.UserRole;

import java.util.List;

public interface UserDAO {

    // Remarque → retourne null si user non trouvé
    User getUserByID(Long id);

    // Remarque → retourne null si user non trouvé
    User getUserByCredentials(String email, String pwd);

    User getUserByEmail(String email);

    // Sauvegarder un user (création ou mise à jour)
    // Ne retourne rien (void)
    void saveUser(User u);

    // Update user information (name, email, phone)
    void updateUser(User u);

    // Change user password
    void changePassword(Long userId, String newPasswordHash);

    //Fetch all users for the list
    List<User> getAllUsers();

    //Update specific role
    void updateUserRole(Long userId, UserRole newRole);
}
