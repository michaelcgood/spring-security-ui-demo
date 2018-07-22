package com.axiomatics;

import java.util.ArrayList;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.axiomatics.model.User;
import com.axiomatics.service.UserService;

@EnableBatchProcessing
@Configuration
public class ProcessOnStartup {
	
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    public UserService userService;
    
    // begin job info
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				  .tasklet(new Tasklet() {
	                    @Override
	                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
	                        
	                    	ArrayList<User> listUsers = addDefaultUsers();
	                    	System.out.println("Users successfully added: " + listUsers.toString());
	                        return RepeatStatus.FINISHED;
	                    }
	                }).build();
	}

	@Bean
	public Job addUsersOnStartUpJob() {
		return jobBuilderFactory.get("addUsersOnStartUpJob")
				.incrementer(new RunIdIncrementer())
				.start(step1())
				.build();
	}
	
	public ArrayList<User> addDefaultUsers() {
		
		ArrayList<User> listOfUsers = new ArrayList<>();
		// add first user, who is level one admin
		User userOne = new User();
		userOne.setEmail("userone@user.com");
		userOne.setName("user");
		userOne.setLastName("one");
		userOne.setPassword("Password1!");
		userOne.setSeniority(1);
		userService.saveUser(userOne);
		// add user one to list
		listOfUsers.add(userOne);
		// add second user, who is level two admin
		User userTwo = new User();
		userTwo.setEmail("usertwo@user.com");
		userTwo.setName("user");
		userTwo.setLastName("two");
		userTwo.setPassword("Password1!");
		userTwo.setSeniority(2);
		userService.saveUser(userTwo);
		// add user two to list
		listOfUsers.add(userTwo);
		
		return listOfUsers;
		
	}

}
