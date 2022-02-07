package com.blog.repo;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Repository;

import com.blog.model.User;

import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;

import software.amazon.awssdk.enhanced.dynamodb.*;

import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo;

@Repository
public class UserRepo {

	private DynamoDbAsyncTable<User> userDynamoDbAsyncTable;

	public UserRepo(DynamoDbAsyncTable<User> userDynamoDbAsyncTable) {
		this.userDynamoDbAsyncTable = userDynamoDbAsyncTable;
	}

	// CREATE
	public CompletableFuture<Void> save(User user) {
		return userDynamoDbAsyncTable.putItem(user);
	}

	// READ
	public CompletableFuture<User> getUserByID(String userName) {
		return userDynamoDbAsyncTable.getItem(getKeyBuild(userName));
	}

	// UPDATE
	public CompletableFuture<User> updateUser(User user) {
		return userDynamoDbAsyncTable.updateItem(user);
	}

	// DELETE
	public CompletableFuture<User> deleteUserById(String userName) {
		return userDynamoDbAsyncTable.deleteItem(getKeyBuild(userName));
	}

	public SdkPublisher<User> getUserBlogs(String userName) {
		return userDynamoDbAsyncTable.query(
				r -> r.queryConditional(keyEqualTo(k -> k.partitionValue(userName))).addAttributeToProject("UserBlog"))
				.items();
	}

	// GET_ALL_ITEM
	public PagePublisher<User> getAllUser() {
		return userDynamoDbAsyncTable.scan();
	}


	private Key getKeyBuild(String userName) {
		return Key.builder().partitionValue(userName).build();
	}


}
