package service;

import model.User;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class UserService {

    private static UserService userService;
    private UserService() {
    }
    public static UserService getInstance() {
        if (userService == null) {
            userService = new UserService();
        }
        return userService;
    }

    /* хранилище данных */
    private Map<Long, User> dataBase = Collections.synchronizedMap(new HashMap<>());
    /* счетчик id */
    private AtomicLong maxId = new AtomicLong(0);
    /* список авторизованных пользователей */
    private Map<Long, User> authMap = Collections.synchronizedMap(new HashMap<>());


    public List<User> getAllUsers() {
        return new ArrayList<>(dataBase.values());
    }

    public User getUserById(Long id) {
        return dataBase.get(id);
    }

    //свой метод, находит пользователя в dataBase по email
    public User getUserByEmail(String email) {
        List<User> userList = this.getAllUsers();
        for(User user : userList) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    public boolean addUser(User user) {
        if (user == null || getUserByEmail(user.getEmail()) != null) {
            return false;
        }
        if (user.getId() == null) {
            user.setId(maxId.getAndIncrement());
        }
        if (!isExistsThisUser(user)) {
            dataBase.put(user.getId(), user);
            return true;
        }
        return false;
    }

    public void deleteAllUser() {
        dataBase.clear();
    }

    public boolean isExistsThisUser(User user) {
        return dataBase.containsValue(user);
    }

    public List<User> getAllAuth() {
        return new ArrayList<>(authMap.values());
    }

    public boolean authUser(User user) {
        if (!this.isExistsThisUser(user)) {
            user = getUserByEmail(user.getEmail());
        }
        if (user == null) {
            return false;
        }
        if (!isUserAuthById(user.getId())) {
            authMap.put(user.getId(), user);
            return true;
        }
        return false;
    }

    public void logoutAllUsers() {
        authMap.clear();
    }

    public boolean isUserAuthById(Long id) {
        return authMap.containsKey(id);
    }

}
