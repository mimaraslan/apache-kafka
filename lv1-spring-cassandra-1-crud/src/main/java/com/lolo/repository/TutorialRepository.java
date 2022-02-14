package com.lolo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import com.lolo.model.Tutorial;

public interface TutorialRepository extends CassandraRepository<Tutorial, UUID> {

	@AllowFiltering
	List<Tutorial> findByPublished(boolean published);

	List<Tutorial> findByTitleContaining(String title);
}
