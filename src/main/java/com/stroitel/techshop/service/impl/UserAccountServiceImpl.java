package com.stroitel.techshop.service.impl;

import com.stroitel.techshop.dao.UserAccountDAO;
import com.stroitel.techshop.domain.Cart;
import com.stroitel.techshop.domain.Role;
import com.stroitel.techshop.domain.UserAccount;
import com.stroitel.techshop.service.UserAccountService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountDAO userAccountDAO;

    public UserAccountServiceImpl(UserAccountDAO userAccountDAO) {
        this.userAccountDAO = userAccountDAO;
    }

    @Transactional(readOnly = true)
    @Override
    public UserAccount findByEmail(String email) {
        return userAccountDAO.findByEmail(email);
    }

    @Transactional
    @Override
    public UserAccount activate(UserAccount userAccount) {

        UserAccount userAccountFromBd = userAccountDAO.findByName(userAccount.getName());

        userAccountFromBd.setActive(true);

        userAccountDAO.save(userAccountFromBd);

        return userAccountFromBd;
    }

    @Transactional
    @Override
    public UserAccount deactivate(UserAccount userAccount) {

        UserAccount userAccountFromBd = userAccountDAO.findByName(userAccount.getName());

        userAccountFromBd.setActive(false);

        userAccountDAO.save(userAccountFromBd);

        return userAccountFromBd;
    }

    @Transactional
    @Override
    public UserAccount create(UserAccount userAccount) throws Exception {
        if (findByEmail(userAccount.getEmail()) != null)
            throw new Exception("User already exists");
        System.out.println("hello");
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setTitle(Role.Roles.ROLE_USER.name());
        roles.add(role);
        userAccount.setActive(true);
        userAccount.setRoles(roles);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(userAccount.getPassword());
        userAccount.setPassword(hashedPassword);
        userAccount.setCart(new Cart(userAccount));
        userAccountDAO.save(userAccount);

        return userAccount;
    }

    @Transactional(readOnly = true)
    @Override
    public UserAccount findById(Long id) {
        return userAccountDAO.findById(id).orElse(null);
    }

    @Override
    public List<UserAccount> getAllUsers() {
        return userAccountDAO.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public UserAccount findByUsername(String name) {
        return userAccountDAO.findByName(name);
    }
}