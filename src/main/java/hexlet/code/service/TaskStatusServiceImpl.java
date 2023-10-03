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
    public TaskStatus getTaskStatusById(final Long id) {
        return taskStatusRepository.findById(id).orElseThrow();
    }

    @Override
    public List<TaskStatus> getAllTaskStatuses() {
        return taskStatusRepository.findAll();
    }

    @Override
    public TaskStatus createNewTaskStatus(final TaskStatusDto taskStatusDto) {
        final TaskStatus newTaskStatus = new TaskStatus();
        newTaskStatus.setName(taskStatusDto.getName());

        return taskStatusRepository.save(newTaskStatus);
    }

    @Override
    public TaskStatus updateTaskStatus(final Long id, final TaskStatusDto taskStatusDto) {
        final TaskStatus taskStatusForUpdate = taskStatusRepository.findById(id).orElseThrow();
        taskStatusForUpdate.setName(taskStatusDto.getName());

        return taskStatusRepository.save(taskStatusForUpdate);
    }

    @Override
    public void deleteTaskStatus(final Long id) {
        TaskStatus taskStatusForDelete = taskStatusRepository.findById(id).orElseThrow();
        taskStatusRepository.delete(taskStatusForDelete);
    }
}
