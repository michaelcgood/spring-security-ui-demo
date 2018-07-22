package com.axiomatics.service;

import com.axiomatics.model.User;

public interface UserService {
	public User findUserByEmail(String email);
	public void saveUser(User user);
}
