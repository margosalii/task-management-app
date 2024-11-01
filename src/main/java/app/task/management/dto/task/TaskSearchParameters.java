package app.task.management.dto.task;

import app.task.management.model.Priority;

public record TaskSearchParameters(Priority[] priorities) {
}
