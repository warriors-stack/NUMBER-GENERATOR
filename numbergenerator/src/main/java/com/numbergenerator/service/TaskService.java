package com.numbergenerator.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.numbergenerator.beans.StatusResponse;
import com.numbergenerator.beans.TaskRequest;
import com.numbergenerator.beans.TaskResponse;
import com.numbergenerator.jpa.TaskStatusRepository;
import com.numbergenerator.model.TaskStatus;

@Service
public class TaskService {

	private final Logger logger = LoggerFactory.getLogger(TaskService.class);

	@Value("${app.instance-id}")
	UUID uuid;


	@Autowired
	TaskStatusRepository taskStatusRepository;

	@Transactional
	public ResponseEntity<TaskResponse> generateTask(TaskRequest request) throws IOException {
		TaskStatus task = new TaskStatus();
		task.setTaskId(uuid.toString());
		task.setTaskStatus("IN_PROGRESS");
		taskStatusRepository.save(task);

		logger.info("Inside generateTask Service");
		TaskResponse response = new TaskResponse();
		// initialize UUID
		//UUID uuid = UUID.randomUUID();
		String directory = System.getProperty("user.home");
		File file = new File(directory.replace("\\", "/")+"/tmp");
		file.mkdir();
		File filePath = new File(directory+"/tmp/" + uuid.toString() + "_output.txt");
		filePath.createNewFile();
		FileOutputStream files = new FileOutputStream(filePath);
		List<String> output = new ArrayList<>();

		try {

			Integer goal = Integer.parseInt(request.getGoal());
			Integer step = Integer.parseInt(request.getStep());
			Integer temp = goal;

			Integer x = goal % step;
			System.out.println(x);
			logger.info("goal step value " + x);
			output.add(temp.toString());
			if (x == 0) {
				while (goal > 0) {
					goal -= step;
				
					output.add(goal.toString());
				}
					String result = output.stream().map(String::toUpperCase).collect(Collectors.joining(","));
				
					files.write(result.getBytes());
					files.close();
					task.setTaskId(uuid.toString());
					task.setTaskStatus("SUCCESS");
					taskStatusRepository.save(task);
					response.setTask(uuid.toString());
					return ResponseEntity.accepted().body(response);
				}
			 else {
				 task.setTaskId(uuid.toString());
				    response.setTask(uuid.toString());
					task.setTaskStatus("ERROR");
					taskStatusRepository.save(task);
					return ResponseEntity.badRequest().body(response);
			 }
				

		} catch (Exception e) {
			logger.error("Error in executing generateTask Service", ResponseEntity.notFound());
			task.setTaskId(uuid.toString());
			task.setTaskStatus("ERROR");
			taskStatusRepository.save(task);
			return ResponseEntity.notFound().build();
		}
	}

	public ResponseEntity<StatusResponse> getTaskStatus(String uUID) throws IOException {
		try {
			StatusResponse status = new StatusResponse();
			//List<TaskStatus> taskList = (List<TaskStatus>) taskStatusRepository.findAll();
			Optional<TaskStatus> task = taskStatusRepository.findById(uUID);
			status.setResult(task.get().getTaskStatus());
			return ResponseEntity.accepted().body(status);
		} catch (Exception e) {
			logger.error("Error in executing getTaskStatus Service", ResponseEntity.notFound());
			return ResponseEntity.notFound().build();
		}
	}

	public ResponseEntity<StatusResponse> getCompletedTask(String uUID, String action) throws IOException {
		StatusResponse response = new StatusResponse();
		try {

			if (action.equalsIgnoreCase("get_numlist")) {
				
				logger.info("Inside getCompletedTasks Service");
				FileInputStream fin = null;
				String directory = System.getProperty("user.home");
				 Stream<Path> paths = Files.walk(Paths.get(directory.replace("\\", "/")+"/tmp"));
				/*
				 * if (fileName.contains(uUID)) fin = new FileInputStream(fileName); byte[]
				 * value = new byte[(int) fileName.length()]; fin.read(value); fin.close();
				 */
				//String fileContent = new String(value, "UTF-8");
				 String result = paths.filter(Files::isRegularFile).map(path->{ String output
				 = ""; if(path.getFileName().toString().contains(uUID)) { try { output =
				  Files.readAllLines(path).stream().map(String::toUpperCase).collect(Collectors
				  .joining()); }catch(IOException e) {
				  logger.error("Error in reading from file"+e.getMessage()); } } return output;
				  }).collect(Collectors.joining());
				 

				logger.info("Output as read from file: " + fin);
				response.setResult(result);
				return ResponseEntity.accepted().body(response);
			} else
				response.setResult(null);
				return ResponseEntity.badRequest().body(response);

		}

		catch (Exception e) {
			logger.error("Error in executing getCompletedTask Service", ResponseEntity.notFound());
			return ResponseEntity.notFound().build();
		}

	}
}
