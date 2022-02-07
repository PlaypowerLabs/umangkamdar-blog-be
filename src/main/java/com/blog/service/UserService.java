package com.blog.service;

import static com.blog.util.Result.FAIL;
import static com.blog.util.Result.SUCCESS;
import static com.blog.util.Result.USER_ALREADY_EXIST;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blog.model.Blog;
import com.blog.model.User;
import com.blog.repo.UserRepo;
import com.blog.util.Result;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

	@Autowired
	private UserRepo userRepo;

	public Mono<Result> saveUser(User user) throws Exception {

		String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		user.setTimestamp(timestamp);

		return Mono.fromFuture(userRepo.save(user)).thenReturn(SUCCESS).onErrorReturn(FAIL);
	}

	public Flux<User> getUsers() {
		return Flux.from(userRepo.getAllUser().items());
	}

	public Mono<Result> login(User user) {
		return Mono.fromFuture(userRepo.getUserByID(user.getUserName())).flatMap(existingUser -> {

			if (user.getUserName().equals(existingUser.getUserName())
					&& user.getPassword().equals(existingUser.getPassword()))
				return Mono.just(SUCCESS);
			else
				return Mono.just(FAIL);
		});
	}

	public Mono<User> getUser(String userName) {
		return Mono.fromFuture(userRepo.getUserByID(userName));
	}

	public Mono<User> addBlogs(User user) {

		return Mono.fromFuture(userRepo.getUserByID(user.getUserName())).flatMap(existingUser -> {

			List<Blog> b = existingUser.getBlog();

			if (b == null) {
				b = new ArrayList<Blog>();
				b.addAll(user.getBlog());
			} else {
				b.addAll(user.getBlog());
			}

			existingUser.setBlog(b);

			if (user.getUserName() != null)
				existingUser.setUserName(user.getUserName());
			if (user.getPassword() != null)
				existingUser.setPassword(user.getPassword());

			return Mono.fromFuture(userRepo.updateUser(existingUser));
		});
	}

	public Mono<User> updateBlog(User user, String blogId) {

		return Mono.fromFuture(userRepo.getUserByID(user.getUserName())).flatMap(existingUser -> {

			List<Blog> b = existingUser.getBlog();

			Blog getOne = b.stream().filter(blogs -> blogId.equals(blogs.getBlogId())).findAny().orElse(null);

			int index = b.indexOf(getOne);

			getOne = user.getBlog().get(0);

			b.set(index, getOne);

			existingUser.setBlog(b);

			return Mono.fromFuture(userRepo.updateUser(existingUser));
		});
	}

	public Mono<User> updateUser(String userName, User user) {
		return Mono.fromFuture(userRepo.getUserByID(userName)).flatMap(existingUser -> {

			if (user.getUserName() != null)
				existingUser.setUserName(user.getUserName());
			if (user.getPassword() != null)
				existingUser.setPassword(user.getPassword());

			return Mono.fromFuture(userRepo.updateUser(existingUser));
		});
	}

	public Mono<Result> deleteUser(String userName) {

		return Mono.fromFuture(userRepo.deleteUserById(userName)).doOnSuccess(Objects::requireNonNull)
				.thenReturn(SUCCESS).onErrorReturn(FAIL);
	}

	public Mono<Result> registerUser(User user) {

		return Mono.fromFuture(userRepo.getUserByID(user.getUserName()))
				.flatMap(exUser -> Mono.just(Optional.of(exUser)))
				.defaultIfEmpty(Optional.empty())
				.flatMap(exUserOptional -> {
					if (!exUserOptional.isPresent()) {
						String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
						user.setTimestamp(timestamp);
						return Mono.fromFuture(userRepo.save(user)).thenReturn(SUCCESS).onErrorReturn(FAIL);
					} else {

						return Mono.just(USER_ALREADY_EXIST);
					}
				});

	}

}

