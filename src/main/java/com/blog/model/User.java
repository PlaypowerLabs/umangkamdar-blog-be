package com.blog.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean()
public class User {

	private String userName;
	
    private String password;
  
    private String firstName;
    private String lastName;
    
    private String timestamp;
    
    private List<Blog> blog;
    
    @DynamoDbPartitionKey
    public String getUserName() {
        return userName;
    }
 
}
