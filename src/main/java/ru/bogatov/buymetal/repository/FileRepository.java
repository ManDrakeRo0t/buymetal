package ru.bogatov.buymetal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bogatov.buymetal.entity.File;

import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID> {
}
