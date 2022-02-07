package com.blog.service;

import static com.blog.util.Result.FAIL;
import static com.blog.util.Result.SUCCESS;
import static com.blog.util.Result.USER_ALREADY_EXIST;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
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

	public Mono<User> addBlogs(String userName, List<Blog> blogs) {

		return Mono.fromFuture(userRepo.getUserByID(userName)).flatMap(existingUser -> {

			List<Blog> existingBlogs = existingUser.getBlog();

			Iterator<Blog> itr = blogs.iterator();
			while (itr.hasNext()) {
				Blog z = (Blog) itr.next();
				z.setBlogId("blogId:" + UUID.randomUUID());
				z.setTimestamp(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
			}

			if (existingBlogs == null) {
				existingBlogs = new ArrayList<Blog>();
				existingBlogs.addAll(blogs);
			} else {
				existingBlogs.addAll(blogs);
			}

			existingUser.setBlog(existingBlogs);

			return Mono.fromFuture(userRepo.updateUser(existingUser));
		});
	}

	public Mono<User> updateUser(String userName, User user) {
		return Mono.fromFuture(userRepo.getUserByID(userName)).flatMap(existingUser -> {

			if (StringUtils.isNotBlank(user.getPassword()))
				existingUser.setPassword(user.getPassword());
			if (StringUtils.isNotBlank(user.getFirstName()))
				existingUser.setFirstName(user.getFirstName());
			if (StringUtils.isNotBlank(user.getPassword()))
				existingUser.setLastName(userName);

			return Mono.fromFuture(userRepo.updateUser(existingUser));
		});
	}

	public Mono<Result> deleteUser(String userName) {

		return Mono.fromFuture(userRepo.deleteUserById(userName)).doOnSuccess(Objects::requireNonNull)
				.thenReturn(SUCCESS).onErrorReturn(FAIL);
	}

	public Mono<Result> registerUser(User user) {

		return Mono.fromFuture(userRepo.getUserByID(user.getUserName()))
				.flatMap(exUser -> Mono.just(Optional.of(exUser))).defaultIfEmpty(Optional.empty())
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

	public Mono<User> updateBlog(String userName, String blogId, Blog blog) {

		return Mono.fromFuture(userRepo.getUserByID(userName)).flatMap(existingUser -> {

			List<Blog> existingBlogs = existingUser.getBlog();

			Blog getOne = existingBlogs.stream().filter(blogs -> blogId.equals(blogs.getBlogId())).findAny()
					.orElse(null);

			if (getOne != null) {
				int index = existingBlogs.indexOf(getOne);
				existingBlogs.remove(index);

				if (StringUtils.isNotBlank(blog.getBlogText()))
					getOne.setBlogText(blog.getBlogText());
				if (blog.getIsDraft() != null)
					getOne.setIsDraft(blog.getIsDraft());
				if (blog.getIsPublished() != null)
					getOne.setIsPublished(blog.getIsPublished());

				existingBlogs.set(index, getOne);

				existingUser.setBlog(existingBlogs);

				return Mono.fromFuture(userRepo.updateUser(existingUser));
			}

			else
				return Mono.empty();

		});
	}

	public Mono<Result> deleteBlog(String userName, String blogId) {

		return Mono.fromFuture(userRepo.getUserByID(userName)).flatMap(existingUser -> {

			List<Blog> existingBlogs = existingUser.getBlog();

			Blog getOne = existingBlogs.stream().filter(blogs -> blogId.equals(blogs.getBlogId())).findAny()
					.orElse(null);
			if (getOne != null) {
				existingBlogs.remove(existingBlogs.indexOf(getOne));
				return Mono.fromFuture(userRepo.updateUser(existingUser)).doOnSuccess(Objects::requireNonNull)
						.thenReturn(SUCCESS).onErrorReturn(FAIL);
			} else
				return Mono.just(FAIL);

		});
	}

}
