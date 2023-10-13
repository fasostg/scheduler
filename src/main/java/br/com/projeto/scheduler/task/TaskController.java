package br.com.projeto.scheduler.task;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.projeto.scheduler.util.Utils;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    TaskRepository repository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody Task task, HttpServletRequest request) {
        System.out.println("Chegou no task controller");
        task.setIdUser((UUID) request.getAttribute("idUser"));

        var currentDate = LocalDateTime.now();
        if (currentDate.isAfter(task.getStartAt()) || currentDate.isAfter(task.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Start/End date should come after the current date!!");
        }

        if (task.getStartAt().isAfter(task.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("End date should come after the start date!!");
        }

        var taskCreated = this.repository.save(task);
        return ResponseEntity.ok(taskCreated);
    }

    @GetMapping("/")
    public ResponseEntity list(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        return ResponseEntity.ok(this.repository.findByIdUser((UUID) idUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody Task task, @PathVariable UUID id, HttpServletRequest request) {
        var taskFound = this.repository.findById(id).get();
        System.out.println(taskFound);
        if (taskFound != null) {
            UUID idUser = (UUID) request.getAttribute("idUser");
            if (!taskFound.getIdUser().equals(idUser)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User is not authorized to change this task!");
            }

            Utils.copyNonNullProperties(task, taskFound);
            this.repository.save(taskFound);

            return ResponseEntity.ok(taskFound);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task not found!");
    }
}
