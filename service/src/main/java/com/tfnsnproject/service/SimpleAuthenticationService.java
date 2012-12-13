package com.tfnsnproject.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.tfnsnproject.dao.UserDao;
import com.tfnsnproject.to.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class SimpleAuthenticationService implements AuthenticationService {

    MessageDigest md5;
    MessageDigest sha1;

    private Cache<String, User> tokenToUserCache;

    private UserDao userDao;

    public void init() throws NoSuchAlgorithmException {
        md5 = MessageDigest.getInstance("MD5");
        sha1 = MessageDigest.getInstance("SHA-1");

        tokenToUserCache = CacheBuilder.newBuilder().maximumSize(1000).build();
    }

    @Override
    public boolean checkPassword(String username, String password) {
        User user = userDao.loadUser(username);
        if (user == null) {
            return false;
        }

        String dbPass = user.getPassword();
        String code = user.getCode();

        String[] hash = new String[6];
        hash[0] = md5(password);
        hash[1] = md5(password + code);
        hash[2] = md5(password) + sha1(code + password) + md5(md5(password));
        hash[3] = sha1(password + code + password);
        hash[4] = md5(hash[3] + hash[0] + hash[1] + hash[2] + sha1(hash[3] + hash[2]));
        hash[5] = sha1(hash[0] + hash[1] + hash[2] + hash[3]) + md5(hash[4] + hash[4]) + sha1(code);
        String hashed = sha1(hash[0] + hash[1] + hash[2] +hash[3] + hash[4] + hash[5] + md5(code));
/*
         $hash[] = md5($password);
         $hash[] = md5($password . $user_code);
         $hash[] = md5($password) . sha1($user_code . $password) . md5(md5($password));
         $hash[] = sha1($password . $user_code . $password);
         $hash[] = md5($hash[3] . $hash[0] . $hash[1] . $hash[2] . sha1($hash[3] . $hash[2]));
         $hash[] = sha1($hash[0] . $hash[1] . $hash[2] . $hash[3]) . md5($hash[4] . $hash[4]) . sha1($user_code);
         return sha1($hash[0] . $hash[1] . $hash[2] . $hash[3] . $hash[4] . $hash[5] . md5($user_code));
*/
        return dbPass.equals(hashed);
    }

    @Override
    public String login(String username, String password) {
        if (checkPassword(username, password)) {
            String token = md5(UUID.randomUUID().toString());
            tokenToUserCache.put(token, userDao.loadUser(username));
            return token;
        }
        return null;
    }

    @Override
    public User authenticate(String token) {
        try {
            return tokenToUserCache.get(token, new Callable<User>() {
                @Override
                public User call() throws Exception {
                    throw new UnsupportedOperationException();
                }
            });
        } catch (ExecutionException e) {
            return null;
        } catch (UncheckedExecutionException e) {
            return null;
        }
    }


    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    private String md5(String msg) {
        return DigestUtils.md5Hex(msg);
    }

    private String sha1(String msg) {
        return DigestUtils.sha1Hex(msg);
    }

}




