package com.blog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Blog {

	private String blogId;
	private String blogText;
	private Boolean isPublished;
	private Boolean isDraft;
	private String timestamp;

	@DynamoDbPartitionKey
	public String getBlogId() {
		return blogId;
	}

}
