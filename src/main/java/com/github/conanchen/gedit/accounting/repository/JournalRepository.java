package com.github.conanchen.gedit.accounting.repository;

import com.github.conanchen.gedit.accounting.model.Journal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author hai
 * @description
 * @email hilin2333@gmail.com
 * @date 23/01/2018 11:13 AM
 */
public interface JournalRepository extends JpaRepository<Journal,String> {
}
