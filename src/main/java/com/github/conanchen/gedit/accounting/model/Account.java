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
 * @description 账户表
 * @email hilin2333@gmail.com
 * @date 23/01/2018 10:18 AM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Account {

    /**
     * fix warning: not generate IETF RFC 4122 compliant UUID values
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(
            name = "uuid",
            strategy = "com.github.conanchen.gedit.accounting.utils.database.CustomUUIDGenerator"
    )
    @Column(columnDefinition = "char(32)")
    private String uuid;


    @Column(columnDefinition = "char(32)")
    private String userUuid ;

    @Column(columnDefinition = "varchar(32)")
    private String accountType;

    @Column(columnDefinition = "int(11)")
    private Integer balance;

    @Column(columnDefinition = "datetime")
    private Date createdDate;

    @Column(columnDefinition = "datetime")
    private Date updatedDate;
}
