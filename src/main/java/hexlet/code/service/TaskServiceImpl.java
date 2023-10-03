package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    private final TaskStatusService taskStatusService;

    private final LabelService labelService;

    public Task getTaskById(final Long id) {
        return taskRepository.findById(id).orElseThrow();
    }

    public List<Task> getAllTasks(Predicate predicate) {// почему здесь предикат?
        return (List<Task>) taskRepository.findAll(predicate);
    }

    public Task createNewTask(final TaskDto taskDto) {
        final Task newTask = fromDto(taskDto);

        return taskRepository.save(newTask);
    }

    public Task updateTask(final Long id, final TaskDto taskDto) {
        final Task taskForUpdate = taskRepository.findById(id).orElseThrow();
        final Task newTask = fromDto(taskDto);

        taskForUpdate.setName(newTask.getName());
        taskForUpdate.setDescription(newTask.getDescription());
        taskForUpdate.setTaskStatus(newTask.getTaskStatus());
        taskForUpdate.setAuthor(newTask.getAuthor());
        taskForUpdate.setExecutor(newTask.getExecutor());

        return taskRepository.save(taskForUpdate);
    }

    public void deleteTask(final Long id) {
        final Task taskForDelete = taskRepository.findById(id).orElseThrow();

        taskRepository.delete(taskForDelete);
    }

    private Task fromDto(final TaskDto taskDto) {
        final Task task = new Task();
        final TaskStatus taskStatus = taskStatusService.getTaskStatusById(taskDto.getTaskStatusId());
        final Set<Long> labelIds = taskDto.getLabelIds().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setAuthor(userService.getCurrentUser());
        task.setTaskStatus(taskStatus);
        if (taskDto.getExecutorId() != null) {
            task.setExecutor(userService.getUserById(taskDto.getExecutorId()));
        }
        if (!labelIds.isEmpty()) {
            Set<Label> labels = labelIds.stream()
                    .map(labelService::getLabelById)
                    .collect(Collectors.toSet());

            task.setLabels(labels);
        }

        return task;
    }
}
