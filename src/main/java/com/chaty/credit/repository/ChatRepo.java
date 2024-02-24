package com.chaty.credit.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.chaty.credit.model.ChatModel;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepo extends ElasticsearchRepository<ChatModel, String> {
	

}
