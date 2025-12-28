package com.example.myarena.persistance.dao;
import com.example.myarena.domain.User;

public interface UserDAO {

    // Remarque → retourne null si user non trouvé
    User getUserByID(Long id);

    // Remarque → retourne null si user non trouvé
    User getUserByCredentials(String email, String pwd);

    // Sauvegarder un user (création ou mise à jour)
    // Ne retourne rien (void)
    void saveUser(User u);
}
