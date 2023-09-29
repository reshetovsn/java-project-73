package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    @Override
    public TaskStatus getTaskStatusById(Long id) {
        return taskStatusRepository.findById(id).orElseThrow();
    }

    @Override
    public List<TaskStatus> getAllTaskStatuses() {
        return taskStatusRepository.findAll();
    }

    @Override
    public TaskStatus createNewTaskStatus(TaskStatusDto taskStatusDto) {
        final TaskStatus newTaskStatus = fromDto(taskStatusDto);

        return taskStatusRepository.save(newTaskStatus);
    }

    @Override
    public TaskStatus updateTaskStatus(Long id, TaskStatusDto taskStatusDto) {
        TaskStatus taskStatusForUpdate = taskStatusRepository.findById(id).orElseThrow();
        taskStatusForUpdate.setName(fromDto(taskStatusDto).getName());

        return taskStatusRepository.save(taskStatusForUpdate);
    }

    @Override
    public void deleteTaskStatus(Long id) {
        TaskStatus taskStatusForUpdate = taskStatusRepository.findById(id).orElseThrow();
        taskStatusRepository.delete(taskStatusForUpdate);
    }

    private TaskStatus fromDto(final TaskStatusDto taskStatusDto) {
        return TaskStatus.builder()
                .name(taskStatusDto.getName())
                .build();
    }
}
