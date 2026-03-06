package com.epam.training.dao;

import com.epam.training.model.User;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDao {

    private final SessionFactory sessionFactory;

    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(User user) {
        sessionFactory.getCurrentSession().persist(user);
    }

    public Optional<User> findByUsername(String username) {
        return sessionFactory.getCurrentSession()
                .createQuery("from User u where u.username = :username", User.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    public User merge(User user) {
        return (User) sessionFactory.getCurrentSession().merge(user);
    }
}