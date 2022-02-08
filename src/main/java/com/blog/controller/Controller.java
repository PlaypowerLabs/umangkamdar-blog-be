package com.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.model.Blog;
import com.blog.model.User;
import com.blog.service.UserService;
import com.blog.util.Result;
import static com.blog.util.Result.USER_ALREADY_EXIST;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/user")
public class Controller {

	@Autowired
	private UserService userService;

	@PostMapping("/register")
	public Mono<ResponseEntity<Result>> registerUser(@RequestBody User user) throws Exception {
		return userService.registerUser(user).map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r))
				.defaultIfEmpty(ResponseEntity.status(HttpStatus.CONFLICT).body(USER_ALREADY_EXIST));
	}

	@PostMapping("/login")
	public Mono<ResponseEntity<Result>> login(@RequestBody User user) {
		return userService.login(user).map(r -> ResponseEntity.ok(r)).defaultIfEmpty(ResponseEntity.notFound().build());

	}

	@GetMapping
	public Flux<User> getUsers() {
		return userService.getUsers();
	}

	@GetMapping("/{userName}")
	public Mono<ResponseEntity<User>> getUser(@PathVariable String userName) {

		return userService.getUser(userName).map(u -> ResponseEntity.ok(u))
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}

	@PutMapping("/{userName}")
	public Mono<ResponseEntity<User>> updateUser(@PathVariable String userName, @RequestBody User user) {

		return userService.updateUser(userName, user).map(u -> ResponseEntity.ok(u))
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}
	
	@DeleteMapping("/{userName}")
	public Mono<ResponseEntity<Result>> deleteUser(@PathVariable String userName) {

		return userService.deleteUser(userName).map(r -> ResponseEntity.ok(r))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	
	@PostMapping("/{userName}/blog")
	public Mono<ResponseEntity<User>> addBlogs(@PathVariable String userName, @RequestBody List<Blog> blog) throws Exception {
		return userService.addBlogs(userName, blog).map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}


	@PutMapping("/{userName}/blog/{blogId}")
	public Mono<ResponseEntity<User>> updateBlog(@PathVariable String userName, @PathVariable String blogId,
			@RequestBody Blog blog) {

		return userService.updateBlog(userName, blogId, blog).map(u -> ResponseEntity.ok(u))
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}

	@DeleteMapping("/{userName}/blog/{blogId}")
	public Mono<ResponseEntity<Result>> deleteBlog(@PathVariable String userName, @PathVariable String blogId) {

		return userService.deleteBlog(userName, blogId).map(u -> ResponseEntity.ok(u))
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}

}