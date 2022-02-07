package com.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.blog.model.User;
import com.blog.service.UserService;
import com.blog.util.Result;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class Controller {

	@Autowired
	private UserService userService;

	@PostMapping("/user")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Result> saveUser(@RequestBody User user) throws Exception {
		return userService.saveUser(user);
	}

	@PostMapping("/register")
	public Mono<ResponseEntity<Result>> registerUser(@RequestBody User user) throws Exception {
		return userService.registerUser(user).map(r -> ResponseEntity.ok(r))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}


	@PostMapping("/login")
	public Mono<ResponseEntity<Result>> login(@RequestBody User user) {
		return userService.login(user).map(r -> ResponseEntity.ok(r))
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}

	@PostMapping("/blog")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<User> addBlogs(@RequestBody User user) throws Exception {
		return userService.addBlogs(user);
	}

	@GetMapping("/user")
	public Flux<User> getUsers() {
		return userService.getUsers();
	}

	@GetMapping("/user/{userName}")
	public Mono<ResponseEntity<User>> getUser(@PathVariable String userName) {

		return userService.getUser(userName).map(u -> ResponseEntity.ok(u))
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}

	@PutMapping("/user/{userName}")
	public Mono<ResponseEntity<User>> updateUser(@PathVariable String userName, @RequestBody User user) {

		return userService.updateUser(userName, user).map(u -> ResponseEntity.ok(u))
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}

	@PutMapping("/blog/{blogId}")
	public Mono<ResponseEntity<User>> updateBlog(@PathVariable String blogId, @RequestBody User user) {

		return userService.updateBlog(user, blogId).map(u -> ResponseEntity.ok(u))
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}

	@DeleteMapping("/user/{userName}")
	public Mono<ResponseEntity<Result>> deleteUser(@PathVariable String userName) {

		return userService.deleteUser(userName).map(r -> ResponseEntity.ok(r))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

}