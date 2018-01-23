package com.github.conanchen.gedit.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

/**
 * @author hai
 * @description 统计信息
 * @email hilin2333@gmail.com
 * @date 23/01/2018 2:24 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StatisticBalance {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(
            name = "uuid",
            strategy = "com.github.conanchen.gedit.accounting.utils.database.CustomUUIDGenerator"
    )
    @Column(columnDefinition = "varchar(32)")
    private String uuid;

    @Column(columnDefinition = "varchar(32)")
    private String userUuid ;

    @Column(columnDefinition = "int(11)")
    private Integer balance;

    @Column(columnDefinition = "varchar(32)")
    private String accountType;

    // 统计的时间（天）
    @Column(columnDefinition = "date")
    private Date statisticDate;

    @Column(columnDefinition = "datetime")
    private Date createdDate;
}