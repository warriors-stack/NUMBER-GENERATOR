package com.numbergenerator.jpa;

import org.springframework.data.repository.CrudRepository;

import com.numbergenerator.model.TaskStatus;

public interface TaskStatusRepository extends CrudRepository<TaskStatus, String>{

}
